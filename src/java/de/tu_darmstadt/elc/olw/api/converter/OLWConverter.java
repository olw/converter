package de.tu_darmstadt.elc.olw.api.converter;

import java.io.File;
import java.io.IOException;
import java.util.Vector;
import java.util.zip.ZipException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import de.tu_darmstadt.elc.olw.api.constant.EndProduct;
import de.tu_darmstadt.elc.olw.api.constant.MaterialProfile;
import de.tu_darmstadt.elc.olw.api.constant.MaterialProfile.Profile;
import de.tu_darmstadt.elc.olw.api.constant.MaterialType;
import de.tu_darmstadt.elc.olw.api.constant.ProductName;
import de.tu_darmstadt.elc.olw.api.document.PDFConverter;
import de.tu_darmstadt.elc.olw.api.media.Converter;
import de.tu_darmstadt.elc.olw.api.media.XMLFlash;
import de.tu_darmstadt.elc.olw.api.media.audio.MP3Converter;
import de.tu_darmstadt.elc.olw.api.media.audio.MP3XMLFlash;
import de.tu_darmstadt.elc.olw.api.media.video.camrec.CamRecConverter;
import de.tu_darmstadt.elc.olw.api.media.video.camrec.CamRecExtractor;
import de.tu_darmstadt.elc.olw.api.media.video.camrec.CamRecXMLFlash;
import de.tu_darmstadt.elc.olw.api.media.video.lecturnity.LPDContainer;
import de.tu_darmstadt.elc.olw.api.media.video.lecturnity.LPDConverter;
import de.tu_darmstadt.elc.olw.api.media.video.lecturnity.LecturnityXMLFlash;
import de.tu_darmstadt.elc.olw.api.media.video.mp4.MP4Converter;
import de.tu_darmstadt.elc.olw.api.media.video.mp4.MP4XMLFlash;
import de.tu_darmstadt.elc.olw.api.misc.MaterialFileExplorer;
import de.tu_darmstadt.elc.olw.api.misc.UUIDGenerator;
import de.tu_darmstadt.elc.olw.api.misc.execution.ExecutionException;
import de.tu_darmstadt.elc.olw.api.misc.io.FileExtractor;

public class OLWConverter {
	private static Logger logger = Logger.getLogger(OLWConverter.class);
	private static final String STREAM_URL = "";
	private static final String XML_CONFIG = "100.xml";
	
	

	private String ffmpegPath = "";
	private File materialFolder = null;
	private File tmpFolder = null;
	private MaterialType materialType;

	/**
	 * @param ffmpegPath
	 * @param materialFolder
	 * @param tmpFolder
	 */
	public OLWConverter(String ffmpegPath, File materialFolder, File tmpFolder) {
		super();
		this.ffmpegPath = ffmpegPath;
		this.materialFolder = materialFolder;
		this.tmpFolder = tmpFolder;
		this.materialType = MaterialType.RAW;
	}

	public String getMaterialType() {
		return materialType.toString();
	}
	
	/**
	 * 
	 * @param materialFile
	 * @param outputFolder
	 * @throws ZipException
	 * @throws IOException
	 */
	private void exploreMaterialFile(File materialFile, File outputFolder) throws ZipException, IOException {
		MaterialFileExplorer explorer = new MaterialFileExplorer(materialFile,
				tmpFolder, ffmpegPath);
		materialType = explorer.getMaterialType();
		materialFolder = explorer.getMaterialFolder();
		if (materialFolder == null)
			materialFolder = materialFile;		
		if (!outputFolder.exists())
			outputFolder.mkdirs();
		if (explorer.getMetadataFile() != null && explorer.getMetadataFile().length() > 0)
			FileUtils.copyFileToDirectory(explorer.getMetadataFile(),
					outputFolder);		
	}
	/**
	 * converts material
	 * 
	 * @param materialFile
	 * @param outputFolder
	 * @throws ZipException
	 * @throws ExecutionException
	 * @throws IOException
	 */
	public void convertMaterial(File materialFile, File outputFolder,
			String uuid, File logFile) throws ZipException, IOException, ExecutionException {
		exploreMaterialFile(materialFile, outputFolder);	
		logger.info("Material Type: " + materialType);
		switch (materialType) {
		case MP3:
			if (materialFolder.isDirectory())
				materialFile = FileExtractor.findFileWithSuffix(materialFolder, "mp3");
			convertMP3(materialFile, outputFolder, uuid, logFile);
			break;
		case MP4_LQ:
		case MP4_HQ:
		case MP4_HD:
			if (materialFolder.isDirectory())
				materialFile = FileExtractor.findFileWithSuffix(materialFolder, "mp4");
			convertMP4(materialFile, outputFolder, uuid, logFile);
			break;
		case LPD_VIDEO_LQ:
		case LPD_VIDEO_HQ:
		case LPD_AUDIO:
			if (materialFolder.isDirectory())
				materialFile = FileExtractor.findFileWithSuffix(materialFolder, "lpd");
			convertLPD(materialFile, outputFolder, uuid, logFile);			
			break;
		case CAM_VIDEO_LQ:
		case CAM_VIDEO_HQ:
		case CAM_AUDIO:			
			convertCAM(materialFolder,outputFolder, uuid, logFile);
			
			break;
		case PDF:
			if (materialFolder.isDirectory())
				materialFile = FileExtractor.findFileWithSuffix(materialFolder, "pdf");
			convertPDF(materialFile,outputFolder, logFile);
			break;
		default:
			FileUtils.copyFileToDirectory(materialFile, outputFolder);
			break;
		}
		
	}	

	/**
	 * 
	 * @param materialFile
	 * @param uuid
	 * @throws ExecutionException
	 * @throws ZipException
	 * @throws IOException
	 */
	public void convertMaterial(File materialFile, String uuid, File logFile)
			throws ExecutionException, ZipException, IOException {
		File uuidFolder = new File(materialFolder,
				UUIDGenerator.getPathFromUUID(uuid));
		if (uuidFolder.exists())
			uuidFolder.mkdirs();
		convertMaterial(materialFile, uuidFolder, uuid, logFile);

	}

	private void convertMedia(File mediaFile, File destFolder,
			Vector<Profile> mediaProfile, Converter converter, File logFile)
			throws ExecutionException {
		for (Profile profile : mediaProfile) {
			File outputFolder = new File(destFolder,
					profile.getOutputFolderName());
			if (outputFolder.exists())
				outputFolder.mkdirs();
			File outputMedia = new File(outputFolder, profile.getProductName());
			converter.convertMedia(outputMedia, profile.getFfmpegSettings(),
					EndProduct.toEndProduct(profile.toString()), logFile);
		}
	}
	
	private void convertMP3(File materialFile, File outputFolder, String uuid, File logFile) throws ExecutionException, IOException {
		Vector<Profile> profile = MaterialProfile
		.getMaterialProfile(materialType);
		MP3Converter mp3Converter = new MP3Converter(materialFile,
				ffmpegPath);
		MP3XMLFlash mp3XML = new MP3XMLFlash();
		convertMedia(materialFolder, outputFolder, profile, mp3Converter,logFile);
		createXMLFlash(mp3XML, outputFolder, uuid, materialType);
	}
	
	private void convertMP4(File materialFile, File outputFolder, String uuid, File logFile)
			throws ExecutionException, IOException {
		Vector<Profile> profile = MaterialProfile
		.getMaterialProfile(materialType);
		MP4Converter mp4Converter = new MP4Converter(materialFile,
				ffmpegPath);
		MP4XMLFlash mp4XML = new MP4XMLFlash();
		convertMedia(materialFolder, outputFolder, profile, mp4Converter, logFile);
		createXMLFlash(mp4XML, outputFolder, uuid, materialType);
	}
	
	private void convertLPD(File materialFile, File outputFolder, String uuid, File logFile) throws ExecutionException, IOException {
		Vector<Profile> profile = MaterialProfile
		.getMaterialProfile(materialType);
		LPDContainer container = new LPDContainer(materialFile, tmpFolder,
				ffmpegPath);
		container.prepareLPD();
		LPDConverter lpdConverter = new LPDConverter(container, ffmpegPath);
		convertMedia(materialFolder, outputFolder, profile, lpdConverter, logFile);
		lpdConverter.createPresentationZipFile(new File(outputFolder,
				"red5"));
		LecturnityXMLFlash lpdXML = new LecturnityXMLFlash(
				container.getLmdFile(), container.getEvqFile());
		logger.info("Creating flash xml");
		createXMLFlash(lpdXML, outputFolder, uuid, materialType);
		
	}
	
	private void convertCAM(File materialFile, File outputFolder, String uuid, File logFile) throws IOException, ExecutionException {
		Vector<Profile> profile = MaterialProfile
		.getMaterialProfile(materialType);
		CamRecExtractor extractor = new CamRecExtractor(materialFolder);
		extractor.prepareCamtasia(ffmpegPath);
		CamRecConverter camConverter = new CamRecConverter(
				extractor.getUnzipFolder(), ffmpegPath);
		convertMedia(materialFile, outputFolder, profile, camConverter, logFile);
		CamRecXMLFlash camXML = new CamRecXMLFlash(
				extractor.getXmlConfigFile(), extractor.hasManyParts());
		logger.info("Creating flash xml ...");
		createXMLFlash(camXML, outputFolder, uuid, materialType);		
	}
	
	private void convertPDF(File materialFile, File outputFolder, File logFile) throws IOException {
		PDFConverter pdfConverter = new PDFConverter(materialFile,
				ffmpegPath);
		File imageFolder = new File (outputFolder,"11");
		pdfConverter.convertPDF(imageFolder,logFile);	
		FileUtils.copyFile(materialFile, new File(outputFolder,ProductName.getPDF_DocumentName()));
		
		
	}
	
	/**
	 * creates the xml file for flash player
	 * @param xml
	 * @param outputFolder
	 * @param uuid
	 * @param type
	 * @throws IOException
	 */
	private void createXMLFlash(XMLFlash xml, File outputFolder, String uuid,
			MaterialType type) throws IOException {
		logger.info("Creating xml config file for flash");
		String lectureURL = "";
		String slidesVideoURL = "";
		switch (type) {
		case MP3:
			lectureURL = UUIDGenerator.getPathFromUUID(uuid) + "/red5/7.flv";
			break;
		case MP4_LQ:
			lectureURL = UUIDGenerator.getPathFromUUID(uuid) + "/red5/5.flv";
			break;
		case MP4_HQ:
			lectureURL = UUIDGenerator.getPathFromUUID(uuid) + "/red5/6.flv";
			break;
		case MP4_HD:
			lectureURL = UUIDGenerator.getPathFromUUID(uuid) + "/red5/6.flv";
			break;
		case LPD_VIDEO_LQ:
			lectureURL = UUIDGenerator.getPathFromUUID(uuid) + "/red5/5.flv";
			break;
		case LPD_VIDEO_HQ:
			lectureURL = UUIDGenerator.getPathFromUUID(uuid) + "/red5/6.flv";
			break;
		case LPD_AUDIO:
			lectureURL = UUIDGenerator.getPathFromUUID(uuid) + "/red5/5.flv";
			break;
		case CAM_VIDEO_LQ:
		case CAM_AUDIO:	
			lectureURL = UUIDGenerator.getPathFromUUID(uuid) + "/red5/5.flv";
			slidesVideoURL = UUIDGenerator.getPathFromUUID(uuid)
					+ "/red5/25.flv";
			break;
		case CAM_VIDEO_HQ:
			lectureURL = UUIDGenerator.getPathFromUUID(uuid) + "/red5/6.flv";
			slidesVideoURL = UUIDGenerator.getPathFromUUID(uuid)
					+ "/red5/26.flv";
			break;
		default:
			break;
		
			
			
		}
		xml.setLectureURL(lectureURL);
		xml.setSlidesVideoURL(slidesVideoURL);
		xml.setStreamURL(STREAM_URL);
		File red5Folder = new File(outputFolder, "red5");
		File xmlFile = new File(red5Folder, XML_CONFIG);
		xml.createXMLFlash(xmlFile);
	}

	

}
