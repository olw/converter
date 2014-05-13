/**
 * 
 */
package de.tu_darmstadt.elc.olw.api.constant;

/**
 * @author Hung Tu
 * 
 */
public enum EndProduct {
	MP4_360P, MP4_480P, MP4_720P, MP4_MOBILE, FLV_360P, FLV_480P, MP3_128K, AAC_128K, OGG_128K, FLV_AUDIO, FLV_DUMMY, FLV_360P_MUTE, FLV_SLIDE_MUTE_360P, FLV_SLIDE_MUTE_480P, MP4_SLIDE_AUDIO_480P, MP4_SLIDE_AUDIO_MOBILE, WEBM_360P, WEBM_480P, WEBM_SLIDE_MUTE_360P, WEBM_SLIDE_MUTE_480P, PDF, RAW, UNKNOWN;

	public static EndProduct toEndProduct(String str) {
		try {
			return valueOf(str);
		} catch (Exception ex) {
			return UNKNOWN;
		}
	}

}
