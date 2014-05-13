package de.tu_darmstadt.elc.olw.api.media.video.camrec;

import java.io.File;
import java.io.IOException;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.zip.ZipException;

import org.apache.commons.io.FileUtils;
import org.jdom.Element;

import de.tu_darmstadt.elc.olw.api.misc.execution.Executer;
import de.tu_darmstadt.elc.olw.api.misc.execution.ExecutionException;
import de.tu_darmstadt.elc.olw.api.misc.io.FileExtractor;
import de.tu_darmstadt.elc.olw.api.misc.FFMPEGInfo;

/**
 * 
 * @author Hung Tu This class unpacks the camtasia and prepares the data for the
 *         conversion. Example: camtasia structure hanke.zip --hanke : upzip
 *         Folder ----01_handke_config.xml : XML_CONFIG ----01_handke.html
 *         ----01_handke_nofp_bg.gif ----01_handke_controller.swf
 *         ----01_handke.js ----01_handke_PIP.mp4 : LECTURER_VIDEO (bad quality)
 *         ----01_handke_HQ.mp4 : LECTURER_VIDEO_HQ (high quality)
 *         ----01_handke.mp4 : PRESENTATION_VIDEO_MUTE ----01_handke_preload.swf
 *         ----swfobject.js
 * 
 */
public class CamRecExtractor {

	public final static String XML_CONFIG = "config";
	public final static String LECTURER_VIDEO_FULL = "_LECTURER_VIDEO_FULL.mp4";
	public final static String PRESENTATION_VIDEO_MUTE_FULL = "_PRESENTATION_VIDEO_MUTE_FULL.mp4";
	public final static String AUDIO_FULL = "_AUDIO_FULL.mp3";
	public final static String PRESENTATION_VIDEO_AUDIO_FULL = "_PRESENTATION_VIDEO_AUDIO_FULL.mp4";
	public final static String LECTURER_VIDEO_FULL_HQ = "_LECTURER_VIDEO_FULL_HQ.mp4";
	private final static String OLW_LOGO_PATH = "/opt/olw/thumbnails/OLW_Logo.png";
	/**
	 * unzipFolder: folder, where the content of camtasia is unpacked
	 */
	private File unzipFolder;

	/**
	 * xmlConfigFile: ..._config.xml
	 */
	private File xmlConfigFile;

	private File lecturerVideoHQ;

	/**
	 * hasManyParts: whether the camtasia has only one media, or more.
	 */
	private boolean hasManyParts;
	private boolean hasOnlyAudio;

	/**
	 * @param xmlConfigFile
	 *            the xmlConfigFile to set
	 */
	public void setXmlConfigFile(File xmlConfigFile) {
		this.xmlConfigFile = xmlConfigFile;
	}

	/**
	 * @return the xmlConfigFile
	 */
	public File getXmlConfigFile() {
		return xmlConfigFile;
	}

	/**
	 * @return the unzipFolder
	 */
	public File getUnzipFolder() {
		return unzipFolder;
	}

	/**
	 * @param unzipFolder
	 *            the unzipFolder to set
	 */
	public void setUnzipFolder(File unzipFolder) {
		this.unzipFolder = unzipFolder;
	}

	/**
	 * @param lecturerVideoHQ
	 *            the lecturerVideoHQ to set
	 */
	public void setLecturerVideoHQ(File lecturerVideoHQ) {
		this.lecturerVideoHQ = lecturerVideoHQ;
	}

	/**
	 * @return the lecturerVideoHQ
	 */
	public File getLecturerVideoHQ() {
		return lecturerVideoHQ;
	}

	public boolean hasLecturerVideoHQ() {
		return (lecturerVideoHQ != null);
	}

	/**
	 * @param hasManyParts
	 *            the hasManyParts to set
	 */
	public void setHasManyParts(boolean hasManyParts) {
		this.hasManyParts = hasManyParts;
	}

	/**
	 * @return the hasManyParts
	 */
	public boolean isHasManyParts() {
		return hasManyParts;
	}

	public boolean hasManyParts() {
		int videoCount = 0;
		File[] listFile = unzipFolder.listFiles();
		for (File file : listFile) {
			if (file.getName().endsWith("_PIP.mp4")) {
				videoCount++;
			}
		}

		return (videoCount > 1);
	}

	/**
	 * @param hasOnlyAudio
	 *            the hasOnlyAudio to set
	 */
	public void setHasOnlyAudio(boolean hasOnlyAudio) {
		this.hasOnlyAudio = hasOnlyAudio;
	}

	/**
	 * @return the hasOnlyAudio
	 */
	public boolean isHasOnlyAudio() {
		return hasOnlyAudio;
	}

	/**
	 * Constructor
	 * 
	 * @param unzipFolder
	 */
	public CamRecExtractor(File unzipFolder) {
		this.unzipFolder = unzipFolder;
		setXmlConfigFile(findXMLConfigFile(unzipFolder));

		setLecturerVideoHQ(findLecturerVideoHQ(unzipFolder));
		hasManyParts = hasManyParts();
		hasOnlyAudio = (findLecturerVideoLQ(unzipFolder) == null);
	}

	/**
	 * Constructor
	 * 
	 * @param zipFile
	 *            Camtasia File
	 * @param tmpFolder
	 * @throws ZipException
	 * @throws IOException
	 */
	public CamRecExtractor(File zipFile, File tmpFolder) throws ZipException,
			IOException {
		// FileExtractor.unzip(zipFile, zipFile.getParentFile());
		FileExtractor.unzip(zipFile, tmpFolder);
		String fileName = FileExtractor.getFileName(zipFile.getName());
		unzipFolder = new File(tmpFolder, fileName);
		if (!unzipFolder.exists()) {
			unzipFolder = findUnzipFolder(tmpFolder);
		}
		setXmlConfigFile(findXMLConfigFile(unzipFolder));
		setLecturerVideoHQ(findLecturerVideoHQ(unzipFolder));
		hasManyParts = hasManyParts();
		hasOnlyAudio = (findLecturerVideoLQ(unzipFolder) == null);

	}

	/**
	 * Finds the xml configuration file
	 * 
	 * @param unzipFolder
	 * @return null, if not found
	 */
	private File findXMLConfigFile(File unzipFolder) {
		File[] fileList = unzipFolder.listFiles();
		for (File file : fileList)
			if (!file.isDirectory() && isXMLFile(file)
					&& file.getName().contains(XML_CONFIG)) {
				return file;
			}
		return null;
	}

	private File findLecturerVideoLQ(File unzipFolder) {
		File[] fileList = unzipFolder.listFiles();
		for (File file : fileList)
			if (!file.isDirectory() && file.getName().endsWith("PIP.mp4")) {
				return file;
			}

		return null;
	}

	/**
	 * 
	 * @param unzipFolder
	 * @return
	 */
	private File findLecturerVideoHQ(File unzipFolder) {
		File[] fileList = unzipFolder.listFiles();
		for (File file : fileList)
			if (!file.isDirectory() && file.getName().endsWith("HQ.mp4")) {
				return file;
			}

		return null;
	}

	/**
	 * Finds the folder of camtasia, in case the camtasia is packed in an extra
	 * directory.
	 * 
	 * @param parentFolder
	 * @return
	 */
	private File findUnzipFolder(File parentFolder) {
		File[] fileList = parentFolder.listFiles();
		for (File file : fileList)
			if (file.isDirectory()) {
				return file;
			}
		return parentFolder;
	}

	private boolean isXMLFile(File file) {
		String fileName = file.getName();
		fileName = fileName.toLowerCase();
		if (fileName.contains("xml"))
			return true;
		return false;
	}

	@SuppressWarnings("rawtypes")
	/**
	 * Appends video, in case camtasia has more than one recordings by using mencoders.
	 * mencoder video1.mp4 video2.mp4 -o video3.mp4 -of lavf -lavfopts
	 * format=mp4 -vf harddup -oac lavc -ovc lavc -lavcopts
	 * acodec=libfaac:vcodec=libx264
	 */
	private void appendVideo(String ffmpegPath) throws IOException {
		Element mainNode = FileExtractor.importXMLFile(xmlConfigFile);
		File mediaFile = new File(unzipFolder, unzipFolder.getName()
				+ LECTURER_VIDEO_FULL);
		File slideFile = new File(unzipFolder, unzipFolder.getName()
				+ PRESENTATION_VIDEO_MUTE_FULL);
		Vector<File> singleMediaFiles = new Vector<File>();
		Vector<File> singleSlideFiles = new Vector<File>();
		Element arrayNode = mainNode.getChild("playlist").getChild("array");
		List childNodes = arrayNode.getChildren();
		Iterator iterator = childNodes.iterator();
		while (iterator.hasNext()) {
			Element filesetNode = (Element) iterator.next();
			Element mediaNode = filesetNode.getChild("pip");
			Element slideNode = filesetNode.getChild("video1");
			if (slideNode == null || mediaNode == null)
				break;
			singleMediaFiles.add(new File(unzipFolder, mediaNode
					.getChildText("uri")));
			singleSlideFiles.add(new File(unzipFolder, slideNode
					.getChildText("uri")));
		}
		combineVideo(singleMediaFiles, unzipFolder, mediaFile, ffmpegPath);
		combineVideo(singleSlideFiles, unzipFolder, slideFile, ffmpegPath);

	}

	/**
	 * 
	 * @throws IOException
	 */
	private void processSinglePart() throws IOException {
		Element mainNode = FileExtractor.importXMLFile(xmlConfigFile);
		File mediaFile = new File(unzipFolder, unzipFolder.getName()
				+ LECTURER_VIDEO_FULL);
		File slideFile = new File(unzipFolder, unzipFolder.getName()
				+ PRESENTATION_VIDEO_MUTE_FULL);
		Element filesetNode = mainNode.getChild("playlist").getChild("array")
				.getChild("fileset");
		Element mediaNode = filesetNode.getChild("pip");
		Element slideNode = filesetNode.getChild("video1");
		if (slideNode != null) {
			File singleSlideFile = new File(unzipFolder,
					slideNode.getChildText("uri"));
			try {
				FileUtils.moveFile(singleSlideFile, slideFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (mediaNode != null) {
			File singleMediaFile = new File(unzipFolder,
					mediaNode.getChildText("uri"));
			try {
				FileUtils.moveFile(singleMediaFile, mediaFile);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		if (hasLecturerVideoHQ())
			try {
				FileUtils.moveFile(lecturerVideoHQ, new File(unzipFolder,
						unzipFolder.getName() + LECTURER_VIDEO_FULL_HQ));
			} catch (IOException e) {
				e.printStackTrace();
			}

	}

	private void processXMPPart() {
		File slideFile = new File(unzipFolder, unzipFolder.getName()
				+ PRESENTATION_VIDEO_MUTE_FULL);
		File mp4File = FileExtractor.findFileWithSuffix(unzipFolder, ".mp4");
		try {
			FileUtils.moveFile(mp4File, slideFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Extras the tone
	 * 
	 * @param ffmpegPath
	 * @throws ExecutionException
	 */
	private void extractAudio(String ffmpegPath) throws ExecutionException {
		File mediaFile = new File(unzipFolder, unzipFolder.getName()
				+ LECTURER_VIDEO_FULL);
		File slideFile = new File(unzipFolder, unzipFolder.getName()
				+ PRESENTATION_VIDEO_MUTE_FULL);
		File audioFile = new File(unzipFolder, unzipFolder.getName()
				+ AUDIO_FULL);
		String extractAudioLine = "";
		if (mediaFile.exists())
			extractAudioLine = ffmpegPath + "ffmpeg -y -i "
					+ mediaFile.getAbsolutePath() + " -qscale 0 "
					+ audioFile.getAbsolutePath();
		else
			extractAudioLine = ffmpegPath + "ffmpeg -y -i "
					+ slideFile.getAbsolutePath() + " -qscale 0 "
					+ audioFile.getAbsolutePath();

		Executer.execute(extractAudioLine);

	}

	/**
	 * joins the audio and the video from the lecturer
	 */
	private void joinAudioWithPresentation(String ffmpegPath)
			throws ExecutionException {
		File slideFile = new File(unzipFolder, unzipFolder.getName()
				+ PRESENTATION_VIDEO_MUTE_FULL);
		File audioFile = new File(unzipFolder, unzipFolder.getName()
				+ AUDIO_FULL);
		File slideAudioFile = new File(unzipFolder, unzipFolder.getName()
				+ PRESENTATION_VIDEO_AUDIO_FULL);
		String joinAudioLine = ffmpegPath + "ffmpeg -y "
				+ " -i "  + audioFile.getAbsolutePath()
				+ " -i " + slideFile.getAbsolutePath()
				+ " -map 0:0 -map 1:0 -acodec copy -vcodec copy "
				+ " " + slideAudioFile.getAbsolutePath();

		Executer.execute(joinAudioLine);
	}

	private void generateDummyLectureVideo(String ffmpegPath)
			throws ExecutionException {
		File audioFile = new File(unzipFolder, unzipFolder.getName()
				+ AUDIO_FULL);
		File dummyFile = new File(unzipFolder, unzipFolder.getName()
				+ CamRecExtractor.LECTURER_VIDEO_FULL);
		File olwLogo = new File(OLW_LOGO_PATH);
		String generateLine = "";
		FFMPEGInfo info = new FFMPEGInfo(audioFile, ffmpegPath);
		String duration = "" + info.getDurationInSecond();
		if (olwLogo.exists())
			generateLine = ffmpegPath + "ffmpeg -y " + " -loop 1 -f image2 "
					+ " -t " + duration + " -i " + olwLogo.getAbsolutePath()
					+ " -i " + audioFile.getAbsolutePath()
					+ " -acodec libfaac -ab 96k -vcodec libx264 -f mp4 "
					+ dummyFile.getAbsolutePath();
		else
			generateLine = ffmpegPath + "ffmpeg -y " + " -i "
					+ audioFile.getAbsolutePath()
					+ " -acodec libfaac -ab 96k -f mp4 "
					+ dummyFile.getAbsolutePath();
		Executer.execute(generateLine);
	}

	/**
	 * prepares data for conversion
	 * 
	 * @param ffmpegPath
	 * @throws IOException
	 * @throws ExecutionException
	 */
	public void prepareCamtasia(String ffmpegPath) throws IOException,
			ExecutionException {
		if (hasManyParts) {
			appendVideo(ffmpegPath);
		} else {
			try {
				processSinglePart();
			} catch (Exception e) {
				e.printStackTrace();
				processXMPPart();
			}
		}
		extractAudio(ffmpegPath);
		joinAudioWithPresentation(ffmpegPath);
		File lecturerFile = new File(unzipFolder, unzipFolder.getName()
				+ CamRecExtractor.LECTURER_VIDEO_FULL);
		if (!lecturerFile.exists())
			generateDummyLectureVideo(ffmpegPath);
	}

	private void combineVideo(Vector<File> fileList, File tmpFolder,
			File outputFile, String ffmpegPath) {
		if (fileList.size() < 2)
			return;
		String transferLine = "";
		String mergeLine = ffmpegPath + "ffmpeg -y" + " -isync -i \"concat:";
		for (int i = 0; i < fileList.size(); i++) {
			File file = fileList.get(i);
			File tmpFile = new File(tmpFolder, file.getName() + ".ts");
			transferLine = ffmpegPath + "ffmpeg -y" + " -i "
					+ file.getAbsolutePath()
					+ " -f mpegts -vcodec copy -vbsf h264_mp4toannexb "
					+ tmpFile.getAbsolutePath();
			try {
				Executer.execute(transferLine);
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			mergeLine += tmpFile.getAbsolutePath();
			if (i != fileList.size() - 1)
				mergeLine += "|";
		}

		mergeLine += "\" -f mp4 -absf aac_adtstoasc -vcodec copy "
				+ outputFile.getAbsolutePath();
		try {
			Executer.execute(mergeLine);
		} catch (ExecutionException e) {
			mergeLine.replace("-vcodec", "-dcodec");
			try {
				Executer.execute(mergeLine);
			} catch (ExecutionException e1) {
				e1.printStackTrace();
			}
		}

	}
}
