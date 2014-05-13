/**
 *  This package concludes api libraries to convert media data (audio/video)
 */
package de.tu_darmstadt.elc.olw.api.media;

import java.io.File;

import de.tu_darmstadt.elc.olw.api.misc.execution.Executer;
import de.tu_darmstadt.elc.olw.api.misc.execution.ExecutionException;



public class MediaConverter {
	
	public String ffmpegPath;

	/**
	 * @return the ffmpegPath
	 */
	public String getFfmpegPath() {
		return ffmpegPath;
	}

	/**
	 * @param ffmpegPath the ffmpegPath to set
	 */
	public void setFfmpegPath(String ffmpegPath) {
		this.ffmpegPath = ffmpegPath;
	}

	/**
	 * @param ffmpegPath
	 */
	public MediaConverter(String ffmpegPath) {
		super();
		this.ffmpegPath = ffmpegPath;
	}
	
	/**
	 * converts input media to output with given ffmpeg settings
	 * @param inputMedia
	 * @param outputMedia
	 * @param ffmpegSetting
	 * @throws ExecutionException
	 */
	public void convertMedia(File inputMedia, File outputMedia,
			String ffmpegSetting) throws ExecutionException {
		String ffmpegLine = ffmpegPath + "ffmpeg -y " + "-i " + 
		                    getFilePath(inputMedia) + " " +
		                    ffmpegSetting + " " + 
		                    getFilePath(outputMedia);
		Executer.execute(ffmpegLine);
	}
	
	public void convertMedia(File inputMedia, File outputMedia,
			String ffmpegSetting, File logFile) throws ExecutionException {
		String ffmpegLine = ffmpegPath + "ffmpeg -y " + "-i " + 
		                    getFilePath(inputMedia) + " " +
		                    ffmpegSetting + " " + 
		                    getFilePath(outputMedia);
		Executer.execute(ffmpegLine, logFile);
	}
	
	public void convertMedia(File[] inputMedias, File outputMedia,
			String ffmpegSetting) throws ExecutionException {
		String ffmpegLine = ffmpegPath + "ffmpeg -y ";
		for (File media : inputMedias) {
			ffmpegLine = ffmpegLine + " -i " + getFilePath(media);
		}
		                    
		ffmpegLine = ffmpegLine + " " + ffmpegSetting + " " + 
		                   getFilePath(outputMedia);
		
		Executer.execute(ffmpegLine);
	}
	
	public void convertMedia(File[] inputMedias, File outputMedia,
			String ffmpegSetting, File logFile) throws ExecutionException {
		String ffmpegLine = ffmpegPath + "ffmpeg -y ";
		for (File media : inputMedias) {
			ffmpegLine = ffmpegLine + " -i " + getFilePath(media);
		}
		                    
		ffmpegLine = ffmpegLine + " " + ffmpegSetting + " " + 
		                   getFilePath(outputMedia);	
		
		Executer.execute(ffmpegLine,logFile);
	}
	
	private String getFilePath (File file) {
		return "\"" + file.getAbsolutePath() + "\"";
	}
	
}
