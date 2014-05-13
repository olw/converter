package de.tu_darmstadt.elc.olw.api.media;

import java.io.File;

import de.tu_darmstadt.elc.olw.api.constant.EndProduct;


public interface Converter {
	public void convertMedia(File outputMedia, String ffmpegSetting,
			EndProduct product, File logFile);
}
