package de.tu_darmstadt.elc.olw.api.constant;

import java.io.File;
import java.io.IOException;

import java.util.zip.ZipException;

import de.tu_darmstadt.elc.olw.api.misc.execution.ExecutionException;

import de.tu_darmstadt.elc.olw.api.misc.MaterialFileExplorer;

public enum MaterialType {

	// audio
	MP3,
	// mp4 video low resolution
	MP4_LQ,
	// mp4 video high resolution
	MP4_HQ,
	// mp4 HD
	MP4_HD,
	// lecturnity with lecture video in low qualitat
	LPD_VIDEO_LQ,
	// lecturnity with lecture video in high qualitat
	LPD_VIDEO_HQ,
	// lecturnity with audio
	LPD_AUDIO, 
	CAM_VIDEO_LQ, 
	CAM_VIDEO_HQ, 
	CAM_AUDIO,
	// pdf document
	PDF,
	// online reader, tutorial, ....
	RAW;

	public static MaterialType toMaterialType(String str) {
		try {
			return valueOf(str);
		} catch (Exception ex) {
			return RAW;
		}
	}

	public static MaterialType getMaterialType(File materialFile,
			String ffmpegPath) throws ExecutionException, ZipException,
			IOException {
		File tmpFolder = new File("/tmp", materialFile.getName());
		tmpFolder.mkdirs();
		MaterialFileExplorer explorer = new MaterialFileExplorer(materialFile,
				tmpFolder, ffmpegPath);
		return explorer.getMaterialType();

	}
	
	/**
	 * Return material type code from database
	 * @param str
	 * @return
	 */
	public static String getMaterialTypeCode(String str) {
		int id = 0;
		MaterialType type = MaterialType.toMaterialType(str);
		switch (type) {
		case MP3:
			id = 3;
			break;
		case MP4_LQ:
			id = 4;
			break;
		case MP4_HD:
			id = 10;
			break;
		case LPD_VIDEO_LQ:
			id = 6;
			break;
		case LPD_AUDIO:
			id = 7;
			break;
		case CAM_VIDEO_LQ:
			id = 5;
			break;
		case CAM_AUDIO:
			id = 8;
			break;
		case PDF:
			id = 1;
			break;
		case RAW: 
			id = 9;
			break;
			
		case LPD_VIDEO_HQ:		
		case CAM_VIDEO_HQ:			
			id = 0;
			break;
			
		case MP4_HQ:
			id = 2;
			break;
		default:
			id = 9;
		}
		return "" + id;
	}
}
