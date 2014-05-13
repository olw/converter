package de.tu_darmstadt.elc.olw.jbi.messages;

import java.io.File;
import java.util.Vector;

import de.tu_darmstadt.elc.olw.api.constant.MaterialProfile;
import de.tu_darmstadt.elc.olw.api.constant.MaterialProfile.Profile;
import de.tu_darmstadt.elc.olw.api.constant.MaterialType;
import de.tu_darmstadt.elc.olw.api.misc.FileChecker;



public class Report {
	
	private String subject;
	private Vector<String> content;
	
	public Report() {
		subject = "";
		content = new Vector<String>();
	}
	
	public String getSubject() {
		return subject;
	}
	
	public String getContent() {
		StringBuilder builder = new StringBuilder();
		if (content.size() == 0) {
			return "";
		}
		for (String str : content) 
			builder.append(str).append("\n");
		return builder.toString();
	}
	
	public void generateReport(File materialFolder, MaterialType materialType, String uuid) {
		boolean isCompleted = true;
		
		switch (materialType) {
			case MP3:				
			case MP4_LQ:				
			case MP4_HQ:
			case CAM_AUDIO:
			case CAM_VIDEO_HQ:
			case CAM_VIDEO_LQ:
				isCompleted=generateMediaReport(materialFolder, MaterialProfile.getMaterialProfile(materialType));
				break;
			case LPD_VIDEO_HQ:
			case LPD_VIDEO_LQ:
			case LPD_AUDIO:
				isCompleted=generateMediaReport(materialFolder, MaterialProfile.getMaterialProfile(materialType));
				if (!FileChecker.isFileCompleted(new File(materialFolder,"red5/10.zip"))) {
					isCompleted = false;
					content.add("red5/10.zip is missed.");
				}
				break;
			case PDF:
				isCompleted=generatePDFReport(materialFolder);
				break;
			default:
				isCompleted=FileChecker.isFolderCompleted(materialFolder);
				break;			
		}
		if (isCompleted) 
			subject = "[FINISHED]["+ uuid + "] All end-products are completed.";
		else
			subject = "[NOTICE][" + uuid + "] Some end-products are missed.";
	}
	
	private boolean generatePDFReport(File materialFolder) {
		boolean isCompleted = true;
		File imgFolder = new File (materialFolder, "11");
		File xmlFile   = new File (materialFolder,"110.xml");
		if (!FileChecker.isFolderCompleted(imgFolder)) {
			isCompleted = false;
			content.add("/11 (Image Folder) is missed.");
		}			
		if (!FileChecker.isFileCompleted(new File(materialFolder, "thumbnail.jpg"))) {
			isCompleted = false;
			content.add("/thumbnail.jpg is missed.");			
		}
		if (!FileChecker.isFileCompleted(xmlFile)) {
			isCompleted = false;
			content.add("/110.xml is missed.");
		}
		
		return isCompleted;
	}
	
	/**
	 * report for mp3, cam, lpd, mp4
	 * @param materialFolder
	 * @param mediaProfile
	 * @return
	 */
	private boolean generateMediaReport(File materialFolder,Vector<Profile> mediaProfile) {
		boolean isCompleted = true;
		for (Profile profile : mediaProfile) {
			File outputFolder = new File(materialFolder,
					profile.getOutputFolderName());
			if (outputFolder.exists())
				outputFolder.mkdirs();
			File outputMedia = new File(outputFolder, profile.getProductName());
			if (!FileChecker.isFileCompleted(outputMedia)) {
				isCompleted = false;
				content.add(profile.getOutputFolderName() + "/" + 
						    profile.getProductName() + " is missed.");
				}
		}
		//check thumbnail
		File thumbnail = new File(materialFolder,"thumbnail.jpg");
		if (!FileChecker.isFileCompleted(thumbnail)) {
			isCompleted = false;
			content.add("/thumbnail.jpg is missed.");
		}
		File xmlFile   = new File(materialFolder,"red5/100.xml");
		if (!FileChecker.isFileCompleted(xmlFile)) {
			isCompleted = false;
			content.add("red5/100.xml is missed.");
		}
		return isCompleted;
	}
		
	

}
