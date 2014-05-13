package de.tu_darmstadt.elc.olw.api.media.video.lecturnity;

import java.io.File;
import java.io.IOException;

import de.tu_darmstadt.elc.olw.api.constant.EndProduct;
import de.tu_darmstadt.elc.olw.api.constant.ProductName;
import de.tu_darmstadt.elc.olw.api.misc.execution.ExecutionException;
import de.tu_darmstadt.elc.olw.api.misc.io.FileExtractor;
import de.tu_darmstadt.elc.olw.api.media.Converter;
import de.tu_darmstadt.elc.olw.api.media.MediaConverter;


public class LPDConverter extends MediaConverter implements Converter {
	private LPDContainer lpdContainer;

	/**
	 * @param ffmpegPath
	 * @param lpdContainer
	 */
	public LPDConverter(LPDContainer lpdContainer, String ffmpegPath) {
		super(ffmpegPath);
		this.lpdContainer = lpdContainer;
	}

	public void setLpdContainer(LPDContainer lpdContainer) {
		this.lpdContainer = lpdContainer;
	}

	public LPDContainer getLpdContainer() {
		return lpdContainer;
	}

	@Override
	public void convertMedia(File outputMedia, String ffmpegSetting,
			EndProduct product, File logFile) {
		File[] inputMedias = { lpdContainer.getWavFile(),
				lpdContainer.getAviFile() };
		// System.out.println(lpdContainer.getWavFile().getName());
		// System.out.println(lpdContainer.getAviFile().getName());
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
				convertMedia(inputMedias, outputMedia, ffmpegSetting, logFile);
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
				break;
			case AAC_128K:
			case MP3_128K:
			case FLV_AUDIO:
			try {
				convertMedia(lpdContainer.getWavFile(), outputMedia,
						ffmpegSetting, logFile);
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
				break;
			
			case MP4_SLIDE_AUDIO_480P:
			case MP4_SLIDE_AUDIO_MOBILE:
			case WEBM_SLIDE_MUTE_360P:
			case WEBM_SLIDE_MUTE_480P:
				File[] presentationVideoAudio = { lpdContainer.getWavFile(),
						lpdContainer.getPresentationVideoMute() };
				if (presentationVideoAudio[1] != null)
					try {
						convertMedia(presentationVideoAudio, outputMedia,
								ffmpegSetting, logFile);
					} catch (ExecutionException e) {
						e.printStackTrace();
					}
				break;
			default:
				break;
		}		
			
	}
	
	public void createPresentationZipFile(File outputFolder) throws IOException {
	String fzipName = ProductName.getFZIP_Lecturnity_SlideName();
	FileExtractor.zipWithChecksum(lpdContainer.getSlideFolder(), new File(
			outputFolder, fzipName));
	}
}
