package de.tu_darmstadt.elc.olw.jbi.messages;

public class StandardMessage {

	public static String getObserverLogNewMaterialMessage(String materialUUID,
			String mimeType) {
		return "New material with the uuid " + materialUUID
				+ " is found on the repository. Material is " + mimeType
				+ " type.";
	}

	public static String getDownloaderSuccessMessage(String materialUUID,
			String localFolder) {
		return "The material with the uuid " + materialUUID
				+ " is downloaded in " + localFolder;
	}

	public static String getDownloaderErrorMessage(String materialUUID,
			String exceptionContent) {
		return "By downloading the material " + materialUUID
				+ ", an exception has occurred: " + exceptionContent;
	}

	public static String getConverterSuccessMessage(String materialUUID,
			String converter) {
		return "The material " + materialUUID
				+ " is successfully converterd by " + converter;
	}

	public static String getConverterPreparationErrorMessage(
			String materialUUID, String exceptionContent) {
		return "By unpacking the material " + materialUUID
				+ ", an exception has occurred: " + exceptionContent;
	}

	public static String getConverterPreparationSuccessMessage(
			String materialUUID, String tempFolder) {
		return "The material " + materialUUID + " is unpacked in folder "
				+ tempFolder;
	}

	public static String getConverterConversionErrorMessage(
			String materialUUID, String exceptionContent) {
		return "By converting the material " + materialUUID
				+ ", an exception has occurred: " + exceptionContent;
	}

	public static String getUploaderSuccessMessage(String materialUUID,
			String repositoryFolder) {
		return "The end products of the material " + materialUUID
				+ " can be found in " + repositoryFolder;
	}

	public static String getUploaderErrorMessage(String materialUUID,
			String exceptionContent) {
		return "By uploading the end products of the material " + materialUUID
				+ ", an exception has occurred: " + exceptionContent;
	}

	public static String getPreviousErrorMessage(String materialUUID,
			String errorModule) {
		return "By processing the material " + materialUUID
				+ ", an error has occurred at the module " + errorModule;
	}

	public static String getMailSenderSuccessMessage(String adminAddress) {
		return "A report e-mail is sended to " + adminAddress;
	}

	public static String getMailSenderErrorMessage(String adminAddress,
			String exceptionContent) {
		return "By sending an e-mail to " + adminAddress
				+ ", an exception has occurred: " + exceptionContent;
	}

	public static String getArchiveUploadSuccessMessage(String materialUUID,
			String repositoryFolder) {
		return "The archive of the material " + materialUUID
				+ " can be found in " + repositoryFolder;
	}

	public static String getArchiveUploadErrorMessage(String materialUUID,
			String localFolder, String exceptionContent) {
		return "By uploading the archive of the material " + materialUUID
				+ ", an exception has occurred: " + exceptionContent
				+ System.getProperty("line.separator")
				+ "The data can be found at local folder " + localFolder;
	}

	public static String getMailSuccessfulContent(String materiallUUID,
			String materialFolder) {
		return "The material " + materiallUUID
				+ " is successfully converted. It can be found at "
				+ materialFolder;
	}

	public static String getMailErrorContent(String materialUUID,
			String materialFolder, String listMissedFiles) {
		return "By converting the material "
				+ materialUUID
				+ ",uncritical errors have occurred. Some end-products are missed.\n" 
				+ listMissedFiles
				+ "More information can be found in log file " 
				+ materialFolder
				+ "/"
				+ materialUUID
				+ ".xml.";
	}

	public static String getMailCriticalErrorContent(String materialUUID,
			String archiveFolder, String materialFolder) {
		return "The material "
				+ materialUUID
				+ ", cannot be processed. The original data directory is located at "
				+ archiveFolder
				+ ". More information can be found in log file " + materialFolder
				+ "/" + materialUUID + ".xml. \n"
		        + "The temporary data is saved under /tmp/" + materialUUID;
	}
}
