package de.tu_darmstadt.elc.olw.api.constant;

import java.util.Vector;

public class MaterialProfile {

	 public enum Profile {
		    MP4_360P (ProductName.getMP4_Lecturer_360pName(),FFMPEGSettings.MP4_360P,""),
		    MP4_480P (ProductName.getMP4_Lecturer_480pName(),FFMPEGSettings.MP4_480P,""), 
		    MP4_720P (ProductName.getMP4_Lecturer_720pName(),FFMPEGSettings.MP4_720P,""), 
		    MP4_MOBILE (ProductName.getMP4_Lecturer_MobileName(),FFMPEGSettings.MP4_MOBILE,""), 
		    FLV_360P (ProductName.getFLV_Lecturer_360pName(),FFMPEGSettings.FLV_360P,"red5"), 
		    FLV_DUMMY (ProductName.getFLV_Lecturer_360pName(),FFMPEGSettings.FLV_DUMMY,"red5"),
		    FLV_360P_MUTE (ProductName.getFLV_Presentation_360pName(),FFMPEGSettings.FLV_AUDIO,"red5"),
		    FLV_480P (ProductName.getFLV_Lecturer_480pName(),FFMPEGSettings.FLV_480P,"red5"), 
		    MP3_128K (ProductName.getMP3_128kName(),FFMPEGSettings.MP3_128K,""), 		    
		    OGG_128K (ProductName.getOGG_128kName(),FFMPEGSettings.OGG_128K,""), 
		    FLV_AUDIO (ProductName.getFLV_Audio_128kName(), FFMPEGSettings.FLV_AUDIO,"red5"),
		    FLV_SLIDE_MUTE_360P (ProductName.getFLV_Presentation_360pName(),FFMPEGSettings.FLV_CAM,"red5"), 
		    FLV_SLIDE_MUTE_480P (ProductName.getFLV_Presentation_480pName(),FFMPEGSettings.FLV_CAM,"red5"),
		    MP4_SLIDE_AUDIO_480P (ProductName.getMP4_Presentation_480pName(),FFMPEGSettings.MP4_480P,""), 
		    MP4_SLIDE_AUDIO_MOBILE (ProductName.getMP4_Presentation_MobileName(),FFMPEGSettings.MP4_MOBILE,""),
		    PDF (ProductName.getPDF_DocumentName(),"",""), 
		    WEBM_360P(ProductName.getWEBM_Lecturer_360pName(),FFMPEGSettings.WEBM_360P,""),
		    WEBM_480P(ProductName.getWEBM_Lecturer_480pName(),FFMPEGSettings.WEBM_480P,""),
		    WEBM_SLIDE_MUTE_360P(ProductName.getWEBM_Presentation_360pName(),FFMPEGSettings.WEBM_360P,""),
		    WEBM_SLIDE_MUTE_480P(ProductName.getWEBM_Presenation_480pName(),FFMPEGSettings.WEBM_480P,""),
		    RAW (ProductName.getRAW_Material_Name(),"","");
		    
		    
		private final String productName;
		private final String ffmpegSettings;
		private final String outputFolderName;
		    /**
		 * @return the productName
		 */
		public String getProductName() {
			return productName;
		}
		/**
		 * @return the ffmpegSettings
		 */
		public String getFfmpegSettings() {
			return ffmpegSettings;
		}
		/**
		 * @return the outputFolderName
		 */
		public String getOutputFolderName() {
			return outputFolderName;
		}
		
	    Profile (String productName, String ffmpegSettings, String outputFolderName) {
				this.productName = productName;
				this.ffmpegSettings = ffmpegSettings;
				this.outputFolderName = outputFolderName;
		}
	    
	    
		
	 };
	
	 public MaterialProfile() {
		 super();
	 }
	 
	public static final Vector<Profile> getMaterialProfile (MaterialType type) {
		switch(type) {
			case MP3:
				return MP3_PROFILE;
			case MP4_LQ:
				return MP4_LQ_PROFILE;
			case MP4_HQ:
				return MP4_HQ_PROFILE;
			case LPD_VIDEO_LQ:
				return LPD_VIDEO_LQ_PROFILE;
			case LPD_VIDEO_HQ:
				return LPD_VIDEO_HQ_PROFILE;
			case LPD_AUDIO:
				return LPD_AUDIO_PROFILE;
			case CAM_VIDEO_LQ:
				return CAM_VIDEO_LQ_PROFILE;
			case CAM_VIDEO_HQ:
				return CAM_VIDEO_HQ_PROFILE;
			case CAM_AUDIO:
				return CAM_AUDIO_PROFILE;
			case PDF:
				return PDF_PROFILE;
			case MP4_HD:
				return MP4_HD_PROFILE;
				
			default:
				return RAW_PROFILE;
		}
	}

	@SuppressWarnings("serial")
	private static final Vector<Profile> MP3_PROFILE = new Vector<Profile>(){	
		{
			add (Profile.MP3_128K);
			add (Profile.OGG_128K);
			add (Profile.FLV_AUDIO);
		}
	};
	@SuppressWarnings("serial")
	private static final Vector<Profile> MP4_LQ_PROFILE = new Vector<Profile>(){		
		{
			add (Profile.MP4_360P);
			add (Profile.MP4_MOBILE);
			add (Profile.FLV_360P);
			add (Profile.MP3_128K);
			add (Profile.WEBM_360P);
						
		}
	};
	@SuppressWarnings("serial")
	private static final Vector<Profile> MP4_HQ_PROFILE = new Vector<Profile>(){
		{
			add (Profile.MP4_480P);
			add (Profile.MP4_MOBILE);
			add (Profile.FLV_480P);
			add (Profile.MP3_128K);
			add (Profile.WEBM_480P);
						
		}
	};
	
	@SuppressWarnings("serial")
	private static final Vector<Profile> MP4_HD_PROFILE = new Vector<Profile>(){
		{
			add (Profile.MP4_480P);
			add (Profile.MP4_720P);
			add (Profile.MP4_MOBILE);
			add (Profile.FLV_480P);
			add (Profile.MP3_128K);
			add (Profile.WEBM_480P);
						
		}
	};
	@SuppressWarnings("serial")
	private static final Vector<Profile> CAM_VIDEO_LQ_PROFILE = new Vector<Profile>(){
		{
			add (Profile.MP4_360P);
			add (Profile.MP4_MOBILE);
			add (Profile.FLV_360P);
			add (Profile.MP3_128K);
			
			add (Profile.FLV_SLIDE_MUTE_360P);
			add (Profile.MP4_SLIDE_AUDIO_480P);
			add (Profile.MP4_SLIDE_AUDIO_MOBILE);
			add (Profile.WEBM_360P);
			add (Profile.WEBM_SLIDE_MUTE_360P);
			add (Profile.RAW);
		}
	};
	
	@SuppressWarnings("serial")
	private static final Vector<Profile> CAM_VIDEO_HQ_PROFILE = new Vector<Profile>(){
		{
			add (Profile.MP4_480P);
			add (Profile.MP4_MOBILE);
			add (Profile.FLV_480P);
			add (Profile.MP3_128K);
			
			add (Profile.FLV_SLIDE_MUTE_480P);
			add (Profile.MP4_SLIDE_AUDIO_480P);
			add (Profile.MP4_SLIDE_AUDIO_MOBILE);
			add (Profile.WEBM_480P);
			add (Profile.WEBM_SLIDE_MUTE_480P);
			add (Profile.RAW);
		}
	};
	
	@SuppressWarnings("serial")
	private static final Vector<Profile> CAM_AUDIO_PROFILE = new Vector<Profile>(){
		{
			add (Profile.MP3_128K);
			
			add (Profile.FLV_DUMMY);
			add (Profile.FLV_360P_MUTE);
			add (Profile.MP4_SLIDE_AUDIO_480P);
			add (Profile.MP4_SLIDE_AUDIO_MOBILE);
			add (Profile.WEBM_SLIDE_MUTE_480P);
			add (Profile.RAW);
		}
	};
	@SuppressWarnings("serial")
	private static final Vector<Profile> LPD_VIDEO_LQ_PROFILE = new Vector<Profile>(){
		{
			add (Profile.MP4_360P);
			add (Profile.MP4_MOBILE);
			add (Profile.FLV_360P);
			add (Profile.MP3_128K);
			
			add (Profile.MP4_SLIDE_AUDIO_480P);
			add (Profile.MP4_SLIDE_AUDIO_MOBILE);
			add (Profile.WEBM_360P);
		}
	};
	
	
	@SuppressWarnings("serial")
	private static final Vector<Profile> LPD_VIDEO_HQ_PROFILE = new Vector<Profile>(){
		{
			add (Profile.MP4_480P);
			add (Profile.MP4_MOBILE);
			add (Profile.FLV_480P);
			add (Profile.MP3_128K);
			
			add (Profile.MP4_SLIDE_AUDIO_480P);
			add (Profile.MP4_SLIDE_AUDIO_MOBILE);	
			add (Profile.WEBM_480P);
		}
	};
	
	@SuppressWarnings("serial")
	private static final Vector<Profile> LPD_AUDIO_PROFILE = new Vector<Profile>(){
		{
			add (Profile.MP3_128K);
			
			add (Profile.FLV_DUMMY);
			add (Profile.MP4_SLIDE_AUDIO_480P);
			add (Profile.MP4_SLIDE_AUDIO_MOBILE);			
		}
	};
	
	@SuppressWarnings("serial")
	private static final Vector<Profile> PDF_PROFILE = new Vector<Profile>(){
		{
			add (Profile.PDF);			
		}
	};
	
	@SuppressWarnings("serial")
	private static final Vector<Profile> RAW_PROFILE = new Vector<Profile>(){
		{
			add (Profile.RAW);			
		}
	};
	

}
