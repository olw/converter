package de.tu_darmstadt.elc.olw.api.media.video.camrec;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import de.tu_darmstadt.elc.olw.api.constant.EndProduct;

import de.tu_darmstadt.elc.olw.api.misc.execution.ExecutionException;
import de.tu_darmstadt.elc.olw.api.misc.io.FileExtractor;
import de.tu_darmstadt.elc.olw.api.media.Converter;
import de.tu_darmstadt.elc.olw.api.media.MediaConverter;


public class CamRecConverter extends MediaConverter implements Converter{

	private File lecturerVideoFull;
	private File audioFull;
	private File presentationVideoAudioFull;
	private File presentationVideoMuteFull;
	private File lecturerVideoFullHQ;
	private File originalZipFile;
	

	/**
	 * @param ffmpegPath
	 * @param camrecFolder
	 * @param uploadFolder
	 */
	public CamRecConverter(File camrecFolder,String ffmpegPath) {
		super(ffmpegPath);
		lecturerVideoFull = new File(camrecFolder, camrecFolder.getName()
				+ CamRecExtractor.LECTURER_VIDEO_FULL);
		audioFull = new File(camrecFolder, camrecFolder.getName()
				+ CamRecExtractor.AUDIO_FULL);
		lecturerVideoFullHQ = new File(camrecFolder, camrecFolder.getName()
				+ CamRecExtractor.LECTURER_VIDEO_FULL_HQ);
		presentationVideoAudioFull = new File(camrecFolder,
				camrecFolder.getName()
				+ CamRecExtractor.PRESENTATION_VIDEO_AUDIO_FULL);
		presentationVideoMuteFull = new File(camrecFolder, camrecFolder.getName()
				+ CamRecExtractor.PRESENTATION_VIDEO_MUTE_FULL);
		originalZipFile = FileExtractor.findFileWithSuffix(camrecFolder.getParentFile(), ".zip");
	}	

    /**
     * converts camtasia
     * @param outputMedia
     * @param ffmpegSetting
     * @param product
     * @throws ExecutionException
     */
	public void convertMedia(File outputMedia, String ffmpegSetting,
			EndProduct product, File logFile) {
		File lecturerVideo = null;
		if (lecturerVideoFullHQ.exists())
			lecturerVideo = lecturerVideoFullHQ;
		else
			lecturerVideo = lecturerVideoFull;
		switch (product) {
			case MP4_360P:
			case MP4_480P:
			case MP4_720P:
			case MP4_MOBILE:
			case FLV_360P:
			case FLV_480P:
			case FLV_DUMMY:	
			case WEBM_360P:
			case WEBM_480P:
			try {
				convertMedia(lecturerVideo, outputMedia, ffmpegSetting, logFile);
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
				break;
			
				
			case AAC_128K:
			case MP3_128K:
			try {
				convertMedia(audioFull, outputMedia, ffmpegSetting, logFile);
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
				break;				
			case FLV_SLIDE_MUTE_360P:
			case FLV_SLIDE_MUTE_480P:
			case FLV_360P_MUTE:
			case WEBM_SLIDE_MUTE_360P:
			case WEBM_SLIDE_MUTE_480P:
			try {
				convertMedia(presentationVideoMuteFull, outputMedia, ffmpegSetting, logFile);
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			    break; 				
			case MP4_SLIDE_AUDIO_480P:
			case MP4_SLIDE_AUDIO_MOBILE:
			try {
				convertMedia(presentationVideoAudioFull, outputMedia, ffmpegSetting, logFile);
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
				break;
			case RAW:
			try {
				FileUtils.copyFile(originalZipFile, outputMedia);
			} catch (IOException e) {
				e.printStackTrace();
			}
				break;
			default: 
				break;			
		}		
		
	}
	

}
