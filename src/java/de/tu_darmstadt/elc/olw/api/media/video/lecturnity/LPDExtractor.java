package de.tu_darmstadt.elc.olw.api.media.video.lecturnity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;


import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;


import org.tritonus.share.sampled.AudioSystemShadow;
import org.tritonus.share.sampled.file.AudioOutputStream;
import org.tritonus.share.sampled.file.TDataOutputStream;

import de.tu_darmstadt.elc.olw.api.misc.io.FileExtractor;
import de.tu_darmstadt.elc.olw.aqs.AQSCodecFactory;


import de.tu_darmstadt.elc.olw.lpd.LPDEntry;
import de.tu_darmstadt.elc.olw.lpd.LPDFile;
import de.tu_darmstadt.elc.olw.lpd.evq.EvqParser;
import de.tu_darmstadt.elc.olw.lpd.obj.ObjParser;
import de.tu_darmstadt.elc.olw.lpd.obj.ObjRenderer;

public class LPDExtractor {
	
	private File lpdFile;
	private File targetFolder;
	private final static Integer WIDTH=720;
	private final static Integer HEIGHT=540;
	

	public LPDExtractor(File lpdFile, File folder) {
		this.lpdFile = lpdFile;
		this.targetFolder = folder;
	}

	/**
	 * extracts the content of lpd file
	 * 
	 * @return false, if an error occur
	 */
	public boolean extractLPD() {
		LPDFile lpdArchive = null;
		LPDEntry[] lpdEntries = null;
		try {
			lpdArchive = new LPDFile(lpdFile);
			lpdEntries = lpdArchive.getEntries();
			for (int i = 0; i < lpdEntries.length; i++) {
				System.out.println("#" + i + ": File "
						+ lpdEntries[i].getName() + " @"
						+ lpdEntries[i].getOffset() + ", compressed "
						+ lpdEntries[i].getCompressedSize() + ", uncompressed "
						+ lpdEntries[i].getUncompressedSize()
						+ ", compression? " + lpdEntries[i].isCompressed());
				extractSingleFile(lpdArchive, lpdEntries[i]);
			}

		} catch (IOException e) {
			e.printStackTrace();

		}
		return true;
	}

	/**
	 * extracts lpd to different entries
	 * 
	 * @param lpdArchive
	 * @param lpdEntry
	 * @throws IOException
	 */
	private void extractSingleFile(LPDFile lpdArchive, LPDEntry lpdEntry)
			throws IOException {
		InputStream inputStream = null;
		FileOutputStream outputStream = null;
		int size = 0;
		int length = 0;
		byte[] buffer = null;
		buffer = new byte[2048];
		File targetFile = null;
		if (lpdEntry.getName().endsWith(LPDContainer.AVI_EXT))
			targetFile = new File(targetFolder, FileExtractor
					.removeSpace(lpdEntry.getName()));
		else
			targetFile = new File(targetFolder, lpdEntry.getName());
		size = lpdEntry.getUncompressedSize();
		outputStream = new FileOutputStream(targetFile);
		inputStream = lpdArchive.getInputStream(lpdEntry);
		while (size > 0) {
			length = inputStream.read(buffer, 0, Math.min(size, buffer.length));
			if (length != -1) {
				outputStream.write(buffer, 0, length);
				size -= length;
			}
		}

		inputStream.close();
		outputStream.close();
	}
	
	public void renderSlides(File evqFile, File objFile, File slideDirectory) throws IOException {
		renderSlides(evqFile,objFile,slideDirectory,WIDTH,HEIGHT);
	}
	/**
	 * extracts the slide from the lertunity archive
	 * 
	 * @param evqFile
	 * @param objFile
	 * @param slideDirectory
	 * @throws IOException
	 */
	public void renderSlides(File evqFile, File objFile, File slideDirectory, int width, int height)
			throws IOException {
		EvqParser evqParser = null;
		ObjParser objParser = null;
		ObjRenderer renderer = null;

		// Prepare target directory
		if (!slideDirectory.exists()) {
			slideDirectory.mkdirs();
		}

		// Parse event queue
		evqParser = new EvqParser(evqFile); // new
		// File("data/20081124_RV_Ebner.evq"));
		evqParser.parse();

		// Parse object commands
		objParser = new ObjParser(objFile); // new
		// File("data/20081124_RV_Ebner.obj"));
		objParser.parse();

		// Render slides
		renderer = new ObjRenderer(evqParser.getEvents(), objParser
				.getCommands());
	
		renderer.render(slideDirectory,width,height);
	}

	public void renderThumbnails(File inputFolder, File outputFolder)
			throws IOException {
		
		File imageFile = null;
		File thumbnailFile = null;
		FileInputStream fileInputStream = null;
		FileOutputStream fileOutputStream = null;
		BufferedImage image = null;
		BufferedImage thumbnail = null;
		Graphics2D graphics = null;
		RenderingHints renderingHints = null;
		File[] inputFiles = null;

		// Create output folder if necessary;
		if (!outputFolder.exists()) {
			outputFolder.mkdirs();
		}

		// List all .png files
		inputFiles = inputFolder.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".png");
			}
		});

		// Loop over all .png files
		for (int i = 0; i < inputFiles.length; i++) {
			imageFile = inputFiles[i];
			thumbnailFile = new File(outputFolder, imageFile.getName());

			// Read .png file to image
			fileInputStream = new FileInputStream(imageFile);
			image = ImageIO.read(fileInputStream);
			fileInputStream.close();

//			System.out.printf("Creating thumbnail for file %s (%d, %d)\n",
//					imageFile.getName(), image.getWidth(), image.getHeight());

			// Prepare thumbnail image
			thumbnail = new BufferedImage(image.getWidth() * 85
					/ image.getHeight(), 85, BufferedImage.TYPE_INT_ARGB);
			graphics = (Graphics2D) thumbnail.getGraphics();
			renderingHints = graphics.getRenderingHints();
			renderingHints.put(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			renderingHints.put(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			graphics.setRenderingHints(renderingHints);

			// Render thumbnail image
			graphics.setColor(Color.RED);
			graphics
					.fillRect(0, 0, thumbnail.getWidth(), thumbnail.getHeight());
			graphics.drawImage(image, 0, 0, thumbnail.getWidth(), thumbnail
					.getHeight(), null);

			// Write thumbnail image to file
			thumbnailFile = new File(outputFolder, imageFile.getName());
			fileOutputStream = new FileOutputStream(thumbnailFile);
			ImageIO
					.write(thumbnail, "png",
							new FileOutputStream(thumbnailFile));
			fileOutputStream.close();
		}
	}

	/**
	 * convert the aqsFile to the wavFile
	 * 
	 * @param aqsFile
	 * @param wavFile
	 * @throws IOException
	 */
	public void renderAudio(File aqsFile, File wavFile) throws IOException {

		AudioFormat audioFormat;
		byte[] buffer = null;
		AudioInputStream audioInputStream = null;
		TDataOutputStream dataOutputStream = null;
		AudioOutputStream audioOutputStream = null;

		AudioFileFormat.Type fileType = null;
		int bytesRead = 0;

		long lLengthInBytes = 0;

		// Compute frame length in bytes
		// duration = 1001;
		// lLengthInFrames = Math.round(duration * sampleRate);
		lLengthInBytes = AudioSystem.NOT_SPECIFIED;

		/*
		 * if (lLengthInFrames != AudioSystem.NOT_SPECIFIED) { lLengthInBytes =
		 * lLengthInFrames * audioFormat.getFrameSize(); }
		 */

		// Prepare AQS file
		audioInputStream = AQSCodecFactory
				.createCodecStream(new FileInputStream(aqsFile));
		audioFormat = audioInputStream.getFormat();

		// audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
		// sampleRate, 16, 2, 4, sampleRate, false);
		buffer = new byte[128000];
		fileType = AudioFileFormat.Type.WAVE;
		try {
			// Create a data output stream
			dataOutputStream = AudioSystemShadow.getDataOutputStream(wavFile);

			// Create an audio output stream
			audioOutputStream = AudioSystemShadow.getAudioOutputStream(
					fileType, audioFormat, lLengthInBytes, dataOutputStream);

			while (bytesRead != -1) {
				bytesRead = audioInputStream.read(buffer, 0, buffer.length);
				if (bytesRead >= 0) {
					audioOutputStream.write(buffer, 0, bytesRead);
				}
			}

			// Close the audio stream
			audioOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
}
