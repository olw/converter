package de.tu_darmstadt.elc.olw.api.media.video.mp4;

import java.io.File;

import de.tu_darmstadt.elc.olw.api.constant.EndProduct;

import de.tu_darmstadt.elc.olw.api.misc.execution.ExecutionException;

import de.tu_darmstadt.elc.olw.api.media.Converter;
import de.tu_darmstadt.elc.olw.api.media.MediaConverter;


public class MP4Converter extends MediaConverter implements Converter{
	
	private File videoFile;
	/**
	 * @param ffmpegPath
	 */
	public MP4Converter(File materialFile, String ffmpegPath) {
		super(ffmpegPath);
		
		this.videoFile = materialFile;
	}
	
	public void convertMedia(File outputMedia, String ffmpegSetting,
			EndProduct product, File logFile)  {
		switch (product) {
			case MP4_360P:
			case MP4_480P:
			case MP4_720P:
			case MP4_MOBILE:
			case WEBM_360P:
			case WEBM_480P:	
			case FLV_360P:
			case FLV_480P:
			case AAC_128K:
			case MP3_128K:
			try {
				convertMedia(videoFile, outputMedia, ffmpegSetting, logFile);
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
				break;
			default: 
				break;			
		}		
		
	}	
	
}
