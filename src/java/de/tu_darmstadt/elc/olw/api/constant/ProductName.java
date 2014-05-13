package de.tu_darmstadt.elc.olw.api.constant;

public class ProductName {
	
	private static class MP4_Lecturer_360p {
		private static final String id = "1";
		private static final String extension = "mp4";
	}

	/**
	 * mp4 848x480 16:9
	 * 
	 * @author hungtu
	 * 
	 */
	private static class MP4_Lecturer_480p {
		private static final String id = "2";
		private static final String extension = "mp4";
	}

	/**
	 * mp4 1280x720 16:9
	 * 
	 * @author hungtu
	 * 
	 */
	private static class MP4_Lecturer_720p {
		private static final String id = "3";
		private static final String extension = "mp4";
	}

	/**
	 * mp4 320x240 4:3
	 * 
	 * @author hungtu
	 * 
	 */
	private static class MP4_Lecturer_Mobile {
		private static final String id = "4";
		private static final String extension = "mp4";
	}

	/**
	 * flv 480x360 4:3
	 * 
	 * @author hungtu
	 * 
	 */
	private static class FLV_Lecturer_360p {
		private static final String id = "5";
		private static final String extension = "flv";
	}

	/**
	 * flv 848x480 4:3
	 * 
	 * @author hungtu
	 * 
	 */
	private static class FLV_Lecturer_480p {
		private static final String id = "6";
		private static final String extension = "flv";
	}

	private static class MP3_128k {
		private static final String id = "7";
		private static final String extension = "mp3";
	}

	private static class OGG_128k {
		private static final String id = "8";
		private static final String extension = "ogg";
	}
/*	
	private static class AAC_128k {
		private static final String id = "8";
		private static final String extension = "aac";
	}
*/
	private static class MP4_Presentation_480p {
		private static final String id = "9";
		private static final String extension = "mp4";
	}

	private static class MP4_Presentation_Mobile {
		private static final String id = "90";
		private static final String extension = "mp4";
	}

	private static class FZIP_Lecturnity_Slide {
		private static final String id = "10";
		private static final String extension = "zip";
	}
	
	private static class XML_Presentation {
		private static final String id = "presentation";
		private static final String extension="xml";
	}
	
	private static class XML_Lecturnity_Slide_Info {
		private static final String id = "100";
		private static final String extension = "xml";
	}

	private static class Document_Image_Folder {
		private static final String id = "11";

	}

	private static class Document_Meta_Folder {
		private static final String id = "12";

	}
	
	private static class PDF_Document {
		private static final String id = "13";
		private static final String extension = "pdf";
	}

	private static class XML_Document_Info {
		private static final String id = "110";
		private static final String extension = "xml";
	}

	/**
	 * flv 480x360 4:3
	 * 
	 * @author hungtu
	 * 
	 */
	private static class FLV_Presentation_360p {
		private static final String id = "25";
		private static final String extension = "flv";
	}

	/**
	 * flv 848x480 4:3
	 * 
	 * @author hungtu
	 * 
	 */
	private static class FLV_Presentation_480p {
		private static final String id = "26";
		private static final String extension = "flv";
	}
	
	private static class WEBM_Lecturer_360p {
		private static final String id = "105";
		private static final String extension = "webm";
	}
	private static class WEBM_Lecturer_480p {
		private static final String id = "106";
		private static final String extension = "webm";
	}
	
	private static class WEBM_Presentation_360P {
		private static final String id = "205";
		private static final String extension = "webm";
	}
	
	private static class WEBM_Presentation_480P {
		private static final String id = "206";
		private static final String extension = "webm";
	}
	
	public static String getWEBM_Lecturer_360pName() {
		return WEBM_Lecturer_360p.id + "." + WEBM_Lecturer_360p.extension;
	}
	
	public static String getWEBM_Lecturer_480pName() {
		return WEBM_Lecturer_480p.id + "." + WEBM_Lecturer_480p.extension;
	}
	
	public static String getWEBM_Presentation_360pName() {
		return WEBM_Presentation_360P.id + "." + WEBM_Presentation_360P.extension;
	}
	
	public static String getWEBM_Presenation_480pName() {
		return WEBM_Presentation_480P.id + "." + WEBM_Presentation_480P.extension;
	}

	public static String getMP4_Lecturer_360pName() {
		return MP4_Lecturer_360p.id + "." + MP4_Lecturer_360p.extension;
	}

	public static String getMP4_Lecturer_480pName() {
		return MP4_Lecturer_480p.id + "." + MP4_Lecturer_480p.extension;
	}

	public static String getMP4_Lecturer_720pName() {
		return MP4_Lecturer_720p.id + "." + MP4_Lecturer_720p.extension;
	}

	public static String getMP3_128kName() {
		return MP3_128k.id + "." + MP3_128k.extension;
	}

	public static String getOGG_128kName() {
		return OGG_128k.id + "." + OGG_128k.extension;
	}
	
	public static String getFLV_Audio_128kName() {
		return MP3_128k.id + "." + FLV_Lecturer_360p.extension;
	}
/*
	public static String getAAC_128kName() {
		return AAC_128k.id + "." + AAC_128k.extension;
	}
*/
	public static String getMP4_Lecturer_MobileName() {
		return MP4_Lecturer_Mobile.id + "." + MP4_Lecturer_Mobile.extension;
	}

	public static String getFLV_Lecturer_360pName() {
		return FLV_Lecturer_360p.id + "." + FLV_Lecturer_360p.extension;
	}

	public static String getFLV_Lecturer_480pName() {
		return FLV_Lecturer_480p.id + "." + FLV_Lecturer_480p.extension;
	}

	public static String getMP4_Presentation_480pName() {
		return MP4_Presentation_480p.id + "." + MP4_Presentation_480p.extension;

	}

	public static String getMP4_Presentation_MobileName() {
		return MP4_Presentation_Mobile.id + "."
				+ MP4_Presentation_Mobile.extension;
	}

	public static String getFZIP_Lecturnity_SlideName() {
		return FZIP_Lecturnity_Slide.id + "." + FZIP_Lecturnity_Slide.extension;
	}

	public static String getXML_Lecturnity_Slide_InfoName() {
		return XML_Lecturnity_Slide_Info.id + "."
				+ XML_Lecturnity_Slide_Info.extension;
	}
	
	public static String getPDF_DocumentName() {
		return PDF_Document.id + "."  + PDF_Document.extension;
	}
	public static String getDocument_Image_FolderName() {
		return Document_Image_Folder.id;
	}

	public static String getDocument_Meta_FolderName() {
		return Document_Meta_Folder.id;
	}

	public static String getXML_Document_InfoName() {
		return XML_Document_Info.id + "." + XML_Document_Info.extension;
	}

	public static String getFLV_Presentation_360pName() {
		return FLV_Presentation_360p.id + "." + FLV_Presentation_360p.extension;
	}

	public static String getFLV_Presentation_480pName() {
		return FLV_Presentation_480p.id + "." + FLV_Presentation_480p.extension;
	}
	
	public static String getXML_PresentationName() {
		return XML_Presentation.id + "." + XML_Presentation.extension;
	}
	
	public static String getRAW_Material_Name() {
		return "30.zip";
	}
	
	public static String getProductName (int i) {
		switch (i) {
		case 1: return getMP4_Lecturer_360pName();
		case 2: return getMP4_Lecturer_480pName();
		case 3: return getMP4_Lecturer_720pName();
		case 4: return getMP4_Lecturer_MobileName();
		case 5: return getFLV_Lecturer_360pName();
		case 6: return getFLV_Lecturer_480pName();
		case 7: return getMP3_128kName();
		//case 8: return getAAC_128kName();
		case 8: return getOGG_128kName();
		case 9: return getMP4_Presentation_480pName();
		case 90: return getMP4_Presentation_MobileName();
		case 10: return getFZIP_Lecturnity_SlideName();
		case 100: return getXML_Lecturnity_Slide_InfoName();
		case 11: return getDocument_Image_FolderName();
		case 12: return getDocument_Meta_FolderName();
		case 110: return getXML_Document_InfoName();
		case 25: return getFLV_Presentation_360pName();
		case 26: return getFLV_Presentation_480pName();
		case 105: return getWEBM_Lecturer_360pName();
		case 106: return getWEBM_Lecturer_480pName();
		case 205: return getWEBM_Presentation_360pName();
		case 206: return getWEBM_Presenation_480pName();
		}
		return "unknown";
	}
}
