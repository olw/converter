package de.tu_darmstadt.elc.olw.api.media.video.lecturnity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import de.tu_darmstadt.elc.olw.api.misc.execution.Executer;
import de.tu_darmstadt.elc.olw.api.misc.execution.ExecutionException;


public class PresentationWithAudio {
	public final static String SLIDE_PREFIX = "slide";
	public final static String SLIDE_IMAGE = "png";
	public final static int NAME = 0;
	public final static int START_TIME = 1;
	public final static int DURATION = 2;
	public final static String PRESENTATION_VIDEO_MUTE_FULL  = "_PRESENTATION_VIDEO_MUTE_FULL.mp4";

	private File slideFolder;
	private File evqFile;
	private File audioFile;
	private Vector<String[]> timeTable;
	private String ffmpegPath;
	

	public PresentationWithAudio(String ffmpegPath) {
		timeTable = new Vector<String[]>();
		this.ffmpegPath = ffmpegPath;
		
	}

	public void setSlideFolder(File slideFolder) {
		this.slideFolder = slideFolder;
	}

	public File getSlideFolder() {
		return slideFolder;
	}

	public void setEvqFile(File evqFile) {
		this.evqFile = evqFile;
	}

	public File getEvqFile() {
		return evqFile;
	}

	public void setAudioFile(File audioFile) {
		this.audioFile = audioFile;
	}

	public File getAudioFile() {
		return audioFile;
	}

	private String getSlideName(int i) {
		String name = SLIDE_PREFIX;
		if (i / 10 < 1)
			name += "000" + i;
		else if (i / 100 < 1)
			name += "00" + i;
		else if (i / 1000 < 1)
			name += "0" + i;
		else
			name += i;
		return name;

	}

	public void setTimeTable(Vector<String[]> timeTable) {
		this.timeTable = timeTable;
	}

	public Vector<String[]> getTimeTable() {
		return timeTable;
	}

	private void extractTimeTable() {
		try {
			FileReader fileReader = new FileReader(evqFile);
			int i = 1;
			@SuppressWarnings("resource")
			BufferedReader in = new BufferedReader(fileReader);
			timeTable.add(new String[] { "zero", "0", "0" });
			// first slide
			String line = in.readLine();
			String slideName = getSlideName(i);
			int startTime = Integer.valueOf(line.split("\\s+", 2)[0]);
			timeTable.add(i, new String[] { slideName, "0", "0" });

			i = 2;

			while ((line = in.readLine()) != null) {
				slideName = getSlideName(i);

				startTime = Integer.valueOf(line.split("\\s+", 2)[0]);
				timeTable.add(i,
						new String[] { slideName, "" + startTime, "0" });
				// calculate duration
				int prevDuration = startTime
						- Integer.valueOf(timeTable.get(i - 1)[START_TIME]);
				prevDuration = Math.round(prevDuration / 1000); // in second
				timeTable.get(i - 1)[DURATION] = "" + prevDuration;
				i++;
			}

		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("File Not Found");
		} catch (IOException e) {
			throw new IllegalArgumentException("IO Error");
		}
	}

	private String convertImageToVideo(File slideFile, String duration,
			File outputFile) {
		
		return ffmpegPath + "ffmpeg -y " +
				" -loop 1 -f image2 " +
				" -t " + duration +
				" -i " + slideFile.getAbsolutePath() + 
				" -f mp4 " + outputFile.getAbsolutePath();


	}
	
	public boolean buildSlideShow() throws ExecutionException {
		this.extractTimeTable();
		int fileCount = 0;
		File videoFolder = new File(slideFolder.getParentFile(),
				"videoSlideTemp");
		if (videoFolder.exists())
			videoFolder.delete();
		videoFolder.mkdirs();
		File videoStream = new File(slideFolder.getParentFile(),PRESENTATION_VIDEO_MUTE_FULL );
		String mergeLine = ffmpegPath + "ffmpeg -y" + " -isync -i \"concat:";
		String transferLine = "";
		for (int i = 1; i < timeTable.size(); i++) {
			String[] slide = timeTable.get(i);
			File slideFile = new File(slideFolder, slide[NAME] + "."
					+ SLIDE_IMAGE);
			File outputFile = new File(videoFolder, slide[NAME] + "_tmp"
					+ ".mp4");
			// convert images to videos
			if (!slide[DURATION].equals("0")) {
				fileCount++;
				Executer.execute(convertImageToVideo(slideFile,
						slide[DURATION], outputFile));
				File tmpFile = new File(videoFolder, outputFile.getName() + ".ts");
				transferLine = ffmpegPath + "ffmpeg -y" + " -i "
						+ outputFile.getAbsolutePath()
						+ " -f mpegts -vcodec copy -vbsf h264_mp4toannexb "
						+ tmpFile.getAbsolutePath();
				Executer.execute(transferLine);
				mergeLine += tmpFile.getAbsolutePath();
				if (i != timeTable.size() - 1)
					mergeLine += "|";

				
			}
		}
		mergeLine += "\" -f mp4 -absf aac_adtstoasc -dcodec copy "
			+ videoStream.getAbsolutePath();

		if (fileCount == 0)
			return false;		
		System.out.println("FFMPEG: " + mergeLine);
		Executer.execute(mergeLine);	
		
		return true;
	}

	
	/**
	 * builds slide show for an lpd archiv
	 * 
	 * @param lpdContainer
	 * @param resultFile
	 * @return
	 * @throws ExecutionException
	 */
	public boolean buildSlideShow(File wavFile, File evqFile, File slideFolder)
			throws ExecutionException {
		setAudioFile(wavFile);
		setEvqFile(evqFile);
		setSlideFolder(slideFolder);
		return buildSlideShow();

	}

}
