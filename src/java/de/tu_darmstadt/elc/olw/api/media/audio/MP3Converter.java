package de.tu_darmstadt.elc.olw.api.media.audio;


import java.io.File;

import de.tu_darmstadt.elc.olw.api.constant.EndProduct;

import de.tu_darmstadt.elc.olw.api.misc.execution.ExecutionException;

import de.tu_darmstadt.elc.olw.api.media.Converter;
import de.tu_darmstadt.elc.olw.api.media.MediaConverter;

public class MP3Converter extends MediaConverter implements Converter{
	
	private File audioFile;
	/**
	 * @param ffmpegPath
	 */
	public MP3Converter(File materialFile,String ffmpegPath) {
		super(ffmpegPath);
		audioFile = materialFile;
	}

	/**
	 * @param audioFile the audioFile to set
	 */
	public void setAudioFile(File audioFile) {
		this.audioFile = audioFile;
	}

	/**
	 * @return the audioFile
	 */
	public File getAudioFile() {
		return audioFile;
	}

	@Override
	public void convertMedia(File outputMedia, String ffmpegSetting,
			EndProduct product, File logFile) {
		switch (product) {
		case OGG_128K:
		case MP3_128K:
		case FLV_AUDIO:
			try {
				convertMedia(audioFile, outputMedia, ffmpegSetting, logFile);
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			break;
		default: 
			break;			
		}		
		
	}


}
