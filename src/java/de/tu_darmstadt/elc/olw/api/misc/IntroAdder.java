package de.tu_darmstadt.elc.olw.api.misc;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import de.tu_darmstadt.elc.olw.api.misc.execution.Executer;
import de.tu_darmstadt.elc.olw.api.misc.execution.ExecutionException;
import de.tu_darmstadt.elc.olw.api.misc.io.FileExtractor;



public class IntroAdder {

	private final static String INTRO_AUDIO = "olw_intro_audio.mp3";
	private final static String INTRO_240P = "olw_intro_240p.mp4";
	private final static String INTRO_360P = "olw_intro_360p.mp4";
	private final static String INTRO_480P = "olw_intro_480p.mp4";
	private final static String INTRO_720P = "olw_intro_720p.mp4";
	private String ffmpegPath;
	private String thumbnailPath;

	/**
	 * @param ffmpegPath
	 * @param thumbnailPath
	 */
	public IntroAdder(String ffmpegPath, String thumbnailPath) {
		super();
		this.ffmpegPath = ffmpegPath;
		this.thumbnailPath = thumbnailPath;
	}

	public void addIntroToMaterial(File uuidFolder, File tmpFolder) {
		if (!uuidFolder.exists())
			return;
		File videoFile = null;
		
		videoFile = new File(uuidFolder, "1.mp4");
		if (videoFile.exists())
			addIntro360P(videoFile, tmpFolder);

		videoFile = new File(uuidFolder, "2.mp4");
		if (videoFile.exists())
			addIntro480P(videoFile, tmpFolder);
		
		videoFile = new File(uuidFolder, "3.mp4");
		if (videoFile.exists())
			addIntro720P(videoFile, tmpFolder);
		
		videoFile = new File(uuidFolder, "4.mp4");
		if (videoFile.exists())
			addIntro240P(videoFile, tmpFolder);
		
		videoFile = new File(uuidFolder, "9.mp4");
		if (videoFile.exists())
			addIntro480P(videoFile, tmpFolder);
		
		videoFile = new File(uuidFolder, "90.mp4");
		if (videoFile.exists())
			addIntro240P(videoFile, tmpFolder);


		

	}

	@SuppressWarnings("unused")
	private void addIntroMP3(File audioFile, File tmpFolder) {
		File introFile = new File(thumbnailPath, INTRO_AUDIO);
		if (!introFile.exists())
			return;
		File outputFile = new File(tmpFolder,
				FileExtractor.getFileName(audioFile.getName()) + "_tmp.mp3");
		String mergeLine = ffmpegPath + "ffmpeg -y " + " -i " + "\"concat:"
				+ introFile.getAbsolutePath() + "|"
				+ audioFile.getAbsolutePath() + "\" " + " -acodec copy "
				+ outputFile.getAbsolutePath();
		try {
			Executer.execute(mergeLine);
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

		try {
			FileUtils.forceDelete(audioFile);
			FileUtils.moveFile(outputFile, audioFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void addIntro240P(File videoFile, File tmpFolder) {
		File introFile = new File(thumbnailPath, INTRO_240P);
		if (introFile.exists())
			addIntro(videoFile, introFile, tmpFolder);
	}

	private void addIntro360P(File videoFile, File tmpFolder) {
		File introFile = new File(thumbnailPath, INTRO_360P);
		if (introFile.exists())
			addIntro(videoFile, introFile, tmpFolder);
	}

	private void addIntro480P(File videoFile, File tmpFolder) {
		File introFile = new File(thumbnailPath, INTRO_480P);
		if (introFile.exists())
			addIntro(videoFile, introFile, tmpFolder);
	}
	
	private void addIntro720P(File videoFile, File tmpFolder) {
		File introFile = new File(thumbnailPath, INTRO_720P);
		if (introFile.exists())
			addIntro(videoFile, introFile, tmpFolder);
	}

	/**
	 * adds intro video clip
	 * 
	 * @param videoFile
	 * @param introFile
	 */
	private void addIntro(File videoFile, File introFile, File tmpFolder) {
		File[] fileList = new File[] { introFile, videoFile };

		File outputFile = new File(tmpFolder,
				FileExtractor.getFileName(videoFile.getName()) + "_tmp.mp4");

		try {
			combineVideo(fileList, tmpFolder, outputFile, ffmpegPath);
			FFMPEGInfo outputInfo = new FFMPEGInfo(outputFile, ffmpegPath);
			FFMPEGInfo videoInfo = new FFMPEGInfo(videoFile, ffmpegPath);
			if (outputInfo.getDurationInSecond() > videoInfo
					.getDurationInSecond())
				try {
					FileUtils.forceDelete(videoFile);
					FileUtils.moveFile(outputFile, videoFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param fileList
	 * @param tmpFolder
	 * @param outputFile
	 * @param ffmpegPath
	 * @throws ExecutionException
	 */
	private void combineVideo(File[] fileList, File tmpFolder, File outputFile,
			String ffmpegPath) {
		if (fileList.length < 2)
			return;
		String transferLine = "";
		String mergeLine = ffmpegPath + "ffmpeg -y" + " -isync -i \"concat:";
		for (int i = 0; i < fileList.length; i++) {
			File file = fileList[i];
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
			if (i != fileList.length - 1)
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
