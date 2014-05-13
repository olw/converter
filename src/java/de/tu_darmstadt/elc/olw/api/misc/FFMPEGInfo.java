package de.tu_darmstadt.elc.olw.api.misc;

import java.io.ByteArrayInputStream;
import java.io.File;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import de.tu_darmstadt.elc.olw.api.misc.execution.Executer;
import de.tu_darmstadt.elc.olw.api.misc.execution.ExecutionException;


public class FFMPEGInfo {

	private File mediaFile;

	private int width;
	private int height;
	private int duration;

	/**
	 * @return the mediaFile
	 */
	public File getMediaFile() {
		return mediaFile;
	}

	/**
	 * @param mediaFile
	 *            the mediaFile to set
	 */
	public void setMediaFile(File mediaFile) {
		this.mediaFile = mediaFile;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public FFMPEGInfo(File mediaFile, String ffmpegPath) {
		super();
		this.mediaFile = mediaFile;
		if (mediaFile.exists())
			try {
				getInfo(ffmpegPath);
			} catch (Exception e) {
				
				e.printStackTrace();
			}

	}

	private void getInfo(String ffmpegPath) throws ExecutionException {
		String ffmpegLine = ffmpegPath
				+ "ffprobe -v quiet -print_format json -show_streams " + "\""
				+ mediaFile.getAbsolutePath() + "\"";
		ByteArrayInputStream stream = Executer.executeInfo(ffmpegLine);
		StringBuilder ffmpegOutput = new StringBuilder();
		int letter = 0;
		while ((letter = stream.read()) != -1) {
			ffmpegOutput.append((char) letter);
		}
		extractInfo(ffmpegOutput.toString());
	}

	private void extractInfo(String ffmpegOutput) {
		JsonObject infoJson = getStreamJson(ffmpegOutput);
		setWidth(getElementIntValue(infoJson, "width"));
		setHeight(getElementIntValue(infoJson, "height"));
		setDuration((int) Double.parseDouble(getElementStringValue(infoJson,
				"duration")));

	}

	public String getAspect() {
		if (width < 0 || height < 0)
			return "0:0";
		float aspect = (float) width / (float) height;
		float aspect_4_3 = ((float) 4) / 3;
		float aspect_16_9 = ((float) 16 / 9);
		if (Math.abs(aspect - aspect_4_3) > Math.abs(aspect - aspect_16_9))
			return "16:9";
		else
			return "4:3";
	}

	/**
	 * Low Quality: Resolution < 480 x 360, aspect 4:3
	 * 
	 * @return
	 */
	public int getVideoQuality() {
		if (width <= 0 || height <= 0)
			return -1;
		if (width <= 480 || height <= 360)
			return 0; // MP4_LQ
		else if (width <= 848 || height <= 480)
			return 1; // MP4_HQ
		else
			return 2; // MP4_HD

	}

	private int getElementIntValue(JsonObject obj, String key) {
		JsonPrimitive element = (JsonPrimitive) obj.get(key);
		return (element == null) ? -1 : element.getAsInt();
	}

	private String getElementStringValue(JsonObject obj, String key) {
		JsonPrimitive element = (JsonPrimitive) obj.get(key);
		return (element == null) ? "" : element.getAsString();
	}

	private JsonObject getStreamJson(String jsonStr) {
		JsonParser parser = new JsonParser();
		JsonObject infoObj = parser.parse(jsonStr).getAsJsonObject();
		JsonArray streams = infoObj.get("streams").getAsJsonArray();
		if (streams.size() < 2)
			return (JsonObject) streams.get(0);
		else {
			String codec = getElementStringValue((JsonObject) streams.get(0),
					"codec_name").toLowerCase();
			if (codec.contains("aac") || codec.contains("wma")
					|| codec.contains("mp3"))
				return (JsonObject) streams.get(1);
			else
				return (JsonObject) streams.get(0);
		}

	}

	public int getDurationInSecond() {
		return duration;
	}

	
}
