package de.tu_darmstadt.elc.olw.api.media.video.lecturnity;

import java.io.File;
import java.io.IOException;

import de.tu_darmstadt.elc.olw.api.misc.execution.Executer;
import de.tu_darmstadt.elc.olw.api.misc.execution.ExecutionException;
import de.tu_darmstadt.elc.olw.api.misc.io.FileExtractor;
import de.tu_darmstadt.elc.olw.api.misc.FFMPEGInfo;



public class LPDContainer {
	private final static Integer SLIDE_WIDTH = 720;
	private final static Integer SLIDE_HEIGHT = 540;
	
	public final static String WAV_EXT = "wav";
	public final static String AVI_EXT = "avi";
	public final static String EVQ_EXT = "evq";
	public final static String AQS_EXT = "aqs";
	public final static String OBJ_EXT = "obj";
	public final static String MP4_EXT = "mp4";
	public final static String MP3_EXT = "mp3";
	public final static String AAC_EXT = "aac";
	public final static String LMD_EXT = "lmd";
	public static final String FLV_EXT = "flv";
	public final static String PRESENTATION_VIDEO_MUTE_FULL  = "_PRESENTATION_VIDEO_MUTE_FULL.mp4";
	private final static String OLW_LOGO_PATH = "/opt/olw/thumbnails/OLW_Logo.png";

	private File lpdFile 		 = null;
	private File aqsFile 		 = null;
	private File evqFile 		 = null;
	private File objFile 		 = null;
	private File wavFile 		 = null;
	private File aviFile 	 	 = null;
	private File lmdFile 		 = null;
	private File slideFolder	 = null;
	private File thumbFolder     = null;
	private File tempFolder      = null;
	private String wavPrefix 	 = null;
	private String ffmpegPath    = null;
	
	/**
	 * constructs a new LPDcontainer Instance
	 * @param materialFile
	 */
	public LPDContainer(File materialFile, File tempFolder, String ffmpegPath) {
		this.lpdFile = materialFile;
		this.tempFolder = tempFolder;
		this.ffmpegPath = ffmpegPath;
	}
	
	public LPDContainer (File lpdFolder, String ffmpegPath) {
		this.tempFolder = lpdFolder;
		this.ffmpegPath = ffmpegPath;
	}
	

	/**** set/get method ************************************/
	
	public void setTempFolder(File tempFolder) {
		this.tempFolder = tempFolder;
	}

	public File getTempFolder() {
		return tempFolder;
	}
	public File getLpdFile() {
		return lpdFile;
	}

	public void setLpdFile(File lpdFile) {
		this.lpdFile = lpdFile;
	}

	public File getAqsFile() {
		return aqsFile;
	}

	public void setAqsFile(File aqsFile) {
		this.aqsFile = aqsFile;
	}

	public File getWavFile() {
		return wavFile;
	}

	public void setWavFile(File wavFile) {
		this.wavFile = wavFile;
	}

	public File getAviFile() {
		return aviFile;
	}

	public void setAviFile(File aviFile) {
		this.aviFile = aviFile;
	}

	
	public void setWavPrefix(String wavPrefix) {
		this.wavPrefix = wavPrefix;
	}

	public String getWavPrefix() {
		return wavPrefix;
	}

	public void setSlideFolder(File slideFolder) {
		this.slideFolder = slideFolder;
	}

	public File getSlideFolder() {
		return slideFolder;
	}

	public void setThumbFolder(File thumbFolder) {
		this.thumbFolder = thumbFolder;
	}

	public File getThumbFolder() {
		return thumbFolder;
	}

	public void setObjFile(File objFile) {
		this.objFile = objFile;
	}

	public File getObjFile() {
		return objFile;
	}

	public void setEvqFile(File evqFile) {
		this.evqFile = evqFile;
	}

	public File getEvqFile() {
		return evqFile;
	}

	public void setLmdFile(File lmdFile) {
		this.lmdFile = lmdFile;
	}

	public File getLmdFile() {
		return lmdFile;
	}
	
	
	public File getPresentationVideoMute() {
		File presentationVideoMute = new File (tempFolder,PRESENTATION_VIDEO_MUTE_FULL);
		if (presentationVideoMute.exists())
			return presentationVideoMute;
		return null;
	}
	/****************************************************/


	private void exploreLPDContainer(File folder) {
		String[] fileList = folder.list();
		for (int i = 0; i < fileList.length; i++) {
			if (fileList[i].endsWith(AQS_EXT)) {
				aqsFile = new File(folder, fileList[i]);
				wavPrefix = FileExtractor.removeSpace(FileExtractor
						.getFileName(aqsFile.getName()));
			}
			if (fileList[i].endsWith(AVI_EXT))
				aviFile = new File(folder, fileList[i]);
			if (fileList[i].endsWith(OBJ_EXT))
				objFile = new File(folder, fileList[i]);
			if (fileList[i].endsWith(EVQ_EXT))
				setEvqFile(new File(folder, fileList[i]));
			if (fileList[i].endsWith(LMD_EXT))
				lmdFile = new File(folder, fileList[i]);
		}
		LPDExtractor extractor = new LPDExtractor(lpdFile, getTempFolder());
		try {
			wavFile = new File(folder, wavPrefix + "." + WAV_EXT);
			extractor.renderAudio(aqsFile, wavFile);
		} catch (IOException e) {
			throw new RuntimeException("Error occurs by rendering audio.");
		}
		// customize evq to eliminate duplication
		String name = FileExtractor.getFileName(evqFile.getName());
		File newEVQFile = new File (evqFile.getParentFile(),name + "_modified.evq");
		File newLMDFile = new File (lmdFile.getParentFile(),name + "_modified.lmd");
		try {
			ConfigFileModifier.customizeLMDFile(lmdFile, newLMDFile);
			this.setLmdFile(newLMDFile);
		} catch (IOException e) {
			throw new RuntimeException(e.toString());
		}
		try {
			ConfigFileModifier.customizeEVQFile(evqFile, lmdFile, newEVQFile);
			this.setEvqFile(newEVQFile);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e.toString());
		}
		
				
		

	}
	
	/**
	 * extracts a folder, and sets the attributes for lpdContainer
	 * @param folder
	 * @return
	 */
	public boolean setLPDContentfromFolder(File folder) {
		File[] fileList = folder.listFiles();
		if (!folder.exists())
			return false;
		tempFolder = folder;
		for (int i = 0; i < fileList.length; i++) {
			if (fileList[i].getName().endsWith(AQS_EXT))
				aqsFile = fileList[i];
			else if (fileList[i].getName().endsWith(AVI_EXT))
				aviFile = fileList[i];
			else if (fileList[i].getName().endsWith(WAV_EXT))
				wavFile = fileList[i];
			else if (fileList[i].getName().endsWith(EVQ_EXT))
				evqFile = fileList[i];
			else if (fileList[i].getName().endsWith(LMD_EXT))
				lmdFile = fileList[i];
			else if (fileList[i].isDirectory()
					&& fileList[i].getName().contains("slides"))
				slideFolder = fileList[i];
			else if (fileList[i].isDirectory() && fileList[i].getName().contains("thumbs"))
				thumbFolder = fileList[i];
				
		}
		return true;
	}
	
	/**
	 * unpacks a LPDContainer
	 * @param tempFolder
	 * @param uploadFolder
	 */
	public void unpackLPDContainer(File tempFolder) {
		this.tempFolder = tempFolder;
		if (!tempFolder.exists())
			tempFolder.mkdir();		
		
		LPDExtractor extractor = new LPDExtractor(lpdFile, getTempFolder());
		if (!extractor.extractLPD())
			throw new RuntimeException("Extracting LPD is uncessful");		
	}
	
	/**
	 * renders the audio, slide and thumbnail.
	 * 
	 * @param tempFolder
	 */
	
	public void createSlides(File tempFolder, int width, int height) {
		LPDExtractor extractor = new LPDExtractor(lpdFile, getTempFolder());
		try {
			slideFolder = new File(tempFolder, "slides");
			extractor.renderSlides(evqFile, objFile, slideFolder,width,height);
		} catch (IOException e) {
			throw new RuntimeException("Error occurs by creating Slides.");
		}
	}
	
	public void createThumbnails(File tempFolder) {
		LPDExtractor extractor = new LPDExtractor(lpdFile, getTempFolder());
		try {
			thumbFolder = new File(tempFolder, "thumbs");
			if (slideFolder.exists())
				extractor.renderThumbnails(slideFolder, thumbFolder);
		} catch (IOException e) {
			throw new RuntimeException("Error occurs by creating thumbnails.");
		}
	}
	
	public void prepareLPD() {
		unpackLPDContainer(tempFolder);
		exploreLPDContainer(tempFolder);
		createSlides(tempFolder, SLIDE_WIDTH, SLIDE_HEIGHT);
		createThumbnails(tempFolder);
		if (aviFile == null)
			try {
				generateDummyLectureVideo(ffmpegPath);
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		
		PresentationWithAudio presAudio = new PresentationWithAudio(ffmpegPath);
		try {
			presAudio.buildSlideShow(wavFile, evqFile, slideFolder);
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	private void generateDummyLectureVideo(String ffmpegPath) throws ExecutionException {
		aviFile    = new File (wavFile.getParentFile(),wavPrefix + ".avi");
		File olwLogo = new File(OLW_LOGO_PATH);
		String generateLine = "";
		FFMPEGInfo info = new FFMPEGInfo(wavFile,ffmpegPath);
		String duration = ""+ info.getDurationInSecond();
		if (olwLogo.exists()) 
			generateLine = ffmpegPath + "ffmpeg -y " +
			               " -loop_input -r 1 -pix_fmt argb -b 9600 " +
			               " -i " + olwLogo.getAbsolutePath() + 
			               " -t " + duration +
			               " -i " + wavFile.getAbsolutePath() + 
			               " -acodec libfaac -ab 96k -f avi " +
			               aviFile.getAbsolutePath();
		else
			generateLine = ffmpegPath + "ffmpeg -y " +
            			   " -i " + wavFile.getAbsolutePath() + 
            			   " -acodec libfaac -ab 96k -f avi " +
            			   aviFile.getAbsolutePath();
		Executer.execute(generateLine);			
	}
	
	
}
