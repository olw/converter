package de.tu_darmstadt.elc.olw.api.misc;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipException;

import org.apache.log4j.Logger;

import de.tu_darmstadt.elc.olw.api.constant.MaterialType;
import de.tu_darmstadt.elc.olw.api.media.video.camrec.CamRecExtractor;
import de.tu_darmstadt.elc.olw.api.media.video.lecturnity.LPDContainer;
import de.tu_darmstadt.elc.olw.api.misc.execution.ExecutionException;
import de.tu_darmstadt.elc.olw.api.misc.io.FileExtractor;


public class MaterialFileExplorer {
	private static Logger logger = Logger.getLogger(MaterialFileExplorer.class);
	
	private File materialFile;
	private File tmpFolder;
	private String ffmpegPath;
	private MaterialType materialType;
	private File materialFolder;

	/**
	 * 
	 * @return the corresponding type
	 */
	public MaterialType getMaterialType() {
		return materialType;
	}

	/**
	 * 
	 * @return the folder, where the material file is stored. If the given
	 *         material data is an archiv file then the unzip folder is
	 *         returned. Otherwise its parent folder is delivered.
	 */
	public File getMaterialFolder() {
		return materialFolder;
	}

	/**
	 * 
	 * @return metadata file
	 */
	public File getMetadataFile() {
		File metadataFile = new File(materialFolder, "metadata.xml");
		if (metadataFile.exists())
			return metadataFile;
		metadataFile = new File(materialFolder, "xml_resource_vorlage.xml");
		if (metadataFile.exists())
			return metadataFile;

		return null;
	}

	/**
	 * Constructor
	 * 
	 * @param materialFile
	 *            input file
	 * @param tmpFolder
	 *            temporary folder
	 * @throws IOException
	 * @throws ZipException
	 */
	public MaterialFileExplorer(File materialFile, File tmpFolder,
			String ffmpegPath) throws ZipException, IOException {
		super();
		this.materialFile = materialFile;
		this.tmpFolder = tmpFolder;
		this.ffmpegPath = ffmpegPath;
		this.materialFolder = null;
		this.materialType = MaterialType.RAW;
		this.exploreMaterialFile();
	}

	/**
	 * main function to investigate the input file
	 * 
	 * @throws ZipException
	 * @throws IOException
	 */
	private void exploreMaterialFile() throws ZipException, IOException {
		if (materialFile.getName().endsWith("zip")) {
			FileExtractor.unzip(materialFile, tmpFolder);
			File[] listFiles = tmpFolder.listFiles();
			for (File file : listFiles) {
				if (file.isDirectory())
					exploreDirectory(file);
				else
					try {
						// camtasia
						CamRecExtractor extractor = new CamRecExtractor(
								tmpFolder);
						if (extractor.getXmlConfigFile() != null) {
							materialFolder = tmpFolder;
							if (extractor.getLecturerVideoHQ() != null)
								materialType = MaterialType.CAM_VIDEO_HQ;
							else {
								if (extractor.isHasOnlyAudio())
									materialType = MaterialType.CAM_AUDIO;
								else
									materialType = MaterialType.CAM_VIDEO_LQ;
							}
						} else
							exploreFile(file);
					} catch (Exception e) {
						e.printStackTrace();
					}
			}
		} else
			try {
				exploreFile(materialFile);
			} catch (Exception e) {
				e.printStackTrace();
			}

	}

	/**
	 * 
	 * @param folder
	 *            Folder, where material data is stored
	 */
	private void exploreDirectory(File folder) {
		// camtasia
		CamRecExtractor extractor = new CamRecExtractor(folder);
		if (extractor.getXmlConfigFile() != null) {
			materialFolder = folder;
			if (extractor.getLecturerVideoHQ() != null)
				materialType = MaterialType.CAM_VIDEO_HQ;
			else {
				if (extractor.isHasOnlyAudio())
					materialType = MaterialType.CAM_AUDIO;
				else
					materialType = MaterialType.CAM_VIDEO_LQ;
			}
		}
		// not cam file
		else {
			File[] listFiles = folder.listFiles();
			for (File file : listFiles) {
				if (file.isDirectory())
					exploreDirectory(file);
				else
					try {
						exploreFile(file);
					} catch (Exception e) {
						e.printStackTrace();
					}
			}
		}

	}

	/**
	 * investigates single file
	 * 
	 * @param file
	 * @throws ExecutionException
	 */
	private void exploreFile(File file) throws ExecutionException {

		String fileName = file.getName().toLowerCase();
		FFMPEGInfo info = null;

		logger.info("File Name: " + fileName);

		// audio file mp3 && wma video
		if (fileName.endsWith("mp3") || fileName.endsWith("wma")) {
			materialFolder = file.getParentFile();
			materialType = MaterialType.MP3;
		}
		// mp4 file
		if (fileName.endsWith("mp4")) {
			materialFolder = file.getParentFile();

			info = new FFMPEGInfo(file, ffmpegPath);
			logger.info("MP4 Quality: " + info.getVideoQuality());
			if (info.getVideoQuality() == 0)
				materialType = MaterialType.MP4_LQ;
			else if (info.getVideoQuality() == 1)
				materialType = MaterialType.MP4_HQ;
			else
				materialType = MaterialType.MP4_HD;
		}
		// lecturnity file
		if (fileName.endsWith("lpd")) {
			materialFolder = file.getParentFile();
			LPDContainer container = new LPDContainer(file, tmpFolder,
					ffmpegPath);
			container.unpackLPDContainer(tmpFolder);
			File aviFile = FileExtractor.findFileWithSuffix(tmpFolder, "avi");
			if (aviFile == null)
				materialType = MaterialType.LPD_AUDIO;
			else {
				info = new FFMPEGInfo(aviFile, ffmpegPath);
				if (info.getVideoQuality() == 0)
					materialType = MaterialType.LPD_VIDEO_LQ;
				else
					materialType = MaterialType.LPD_VIDEO_HQ;
			}
		}
		// pdf file
		if (fileName.endsWith("pdf")) {
			materialFolder = file.getParentFile();
			materialType = MaterialType.PDF;
		}
	}

}
