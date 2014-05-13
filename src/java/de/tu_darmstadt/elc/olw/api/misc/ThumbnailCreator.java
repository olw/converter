package de.tu_darmstadt.elc.olw.api.misc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.Transforms;
import org.openimaj.image.processing.face.detection.DetectedFace;
import org.openimaj.image.processing.face.detection.FaceDetector;
import org.openimaj.image.processing.face.detection.HaarCascadeDetector;

import de.tu_darmstadt.elc.olw.api.constant.MaterialType;
import de.tu_darmstadt.elc.olw.api.misc.execution.Executer;
import de.tu_darmstadt.elc.olw.api.misc.execution.ExecutionException;

public class ThumbnailCreator {

	
	private static String THUMB_NAME = "thumbnail.jpg";
	private static String THUMB_RESOLUTION = "152x114";
	private static String IMG_FOLDER = "11";
	private static Integer TIME_STEP = 5;
	private static Integer MAX_DETECTION = 20;
	

	private String ffmpegPath;
	private String thumbnailPath;
	
	/**
	 * @param ffmpegPath
	 *            the ffmpegPath to set
	 */
	public void setFfmpegPath(String ffmpegPath) {
		this.ffmpegPath = ffmpegPath;
	}

	/**
	 * @return the ffmpegPath
	 */
	public String getFfmpegPath() {
		return ffmpegPath;
	}

	/**
	 * @param thumbnailPath the thumbnailPath to set
	 */
	public void setThumbnailPath(String thumbnailPath) {
		this.thumbnailPath = thumbnailPath;
	}

	/**
	 * @return the thumbnailPath
	 */
	public String getThumbnailPath() {
		return thumbnailPath;
	}
	
	
	public ThumbnailCreator(String ffmpegPath, String thumbnailPath) {
		super();
		this.setFfmpegPath(ffmpegPath);
		this.setThumbnailPath(thumbnailPath);
	}
	
	public void createThumbnail (File uuidFolder) {
		
	}
	public void createThumbnail(File uuidFolder, MaterialType type)
			throws IOException, ExecutionException {
		switch (type) {
		case MP3:
			FileUtils.copyFile(new File(thumbnailPath, "audio.jpg"), new File(
					uuidFolder, THUMB_NAME));
			break;
		case MP4_LQ:
		case MP4_HQ:
		case MP4_HD:	
		case LPD_VIDEO_LQ:
		case LPD_VIDEO_HQ:		
		case CAM_VIDEO_LQ:
		case CAM_VIDEO_HQ:		
			createThumbnailForVideo(uuidFolder);
			break;
		case LPD_AUDIO:
		case CAM_AUDIO:
			createThumbnailForSlideVideo(uuidFolder);
			break;
		case PDF:
			File imgFolder = new File(uuidFolder, IMG_FOLDER);
			if (!imgFolder.exists()) {
				FileUtils.copyFile(new File(thumbnailPath, "pdf.jpg"), new File(
						uuidFolder, THUMB_NAME));
				break;
			}
			// ImageMagick
			String thumbCommand = "convert " + imgFolder.getAbsolutePath()
					+ "/slide1.png" + " -resize " + THUMB_RESOLUTION + " "
					+ imgFolder.getParentFile().getAbsolutePath() + "/"
					+ THUMB_NAME;
			Executer.execute(thumbCommand);
			// delete image folder
			FileUtils.deleteDirectory(imgFolder);
			break;
		case RAW:
			FileUtils.copyFile(new File(thumbnailPath, "wbt.jpg"), new File(
					uuidFolder, THUMB_NAME));
			break;
		default:
			break;

		}
	}

	private void createThumbnailForSlideVideo(File uuidFolder) throws ExecutionException {
		File videoFile = new File(uuidFolder, "9.mp4");
		if (!videoFile.exists())
			videoFile = new File(uuidFolder, "red5/25.flv");
		if (!videoFile.exists())
			return;
		String time = "5";
		File thumbnail = new File(videoFile.getParent(), THUMB_NAME);
		Executer.execute(this.getFFmpegLine(videoFile, thumbnail,time,THUMB_RESOLUTION));
	}

	private void createThumbnailForVideo(File uuidFolder) throws FileNotFoundException, ExecutionException, IOException {
		File videoFile = new File(uuidFolder, "1.mp4");
		if (!videoFile.exists())
			videoFile = new File(uuidFolder, "2.mp4");
		if (!videoFile.exists())
			videoFile = new File(uuidFolder, "4.mp4");
		if (!videoFile.exists())
			return;
		String time = getTimeWithFace(videoFile);
		File thumbnail = new File(videoFile.getParent(), THUMB_NAME);
		Executer.execute(this.getFFmpegLine(videoFile, thumbnail,time,THUMB_RESOLUTION));
	}

	/**
	 * Simple command line face detection based on the OpenIMAJ framework -
	 * http://www.openimaj.org 
	 * 
	 */
	private boolean detectFace(File imgFile) throws FileNotFoundException,
			IOException {
		MBFImage image = ImageUtilities.readMBF(new FileInputStream(imgFile));
		FaceDetector<DetectedFace, FImage> fd = new HaarCascadeDetector(80);
		List<DetectedFace> faces = fd.detectFaces(Transforms
				.calculateIntensity(image));
		return (faces.size() > 0);
	}

	private String getTimeWithFace(File videoFile) throws ExecutionException, FileNotFoundException, IOException {		
		int timeBase = 0;
		int detectionTry = 0;
		File thumbnail = new File(videoFile.getParent(), THUMB_NAME);
		do {
			timeBase += TIME_STEP;
			Executer.execute(this.getFFmpegLine(videoFile, thumbnail, ""+timeBase,"1024x740"));
			detectionTry++;
		} while (!this.detectFace(thumbnail) && detectionTry < MAX_DETECTION);
			
		return ""+ timeBase;
	}

	private String getFFmpegLine(File videoFile, File thumbnail, String timeBase, String resolution) {
		return ffmpegPath + "ffmpeg -y -itsoffset " + "-" + timeBase + " "
				+ "-i" + " " + videoFile.getAbsolutePath() + " "
				+ "-vcodec mjpeg -vframes 1 -an -f rawvideo " + "-s " + resolution
				+ " " + thumbnail.getAbsolutePath();
	}

	
	
	
}
