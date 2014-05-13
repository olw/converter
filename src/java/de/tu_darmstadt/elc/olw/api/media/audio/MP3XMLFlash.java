package de.tu_darmstadt.elc.olw.api.media.audio;

import java.io.File;
import java.io.IOException;

import org.jdom.Document;
import org.jdom.Element;

import de.tu_darmstadt.elc.olw.api.misc.io.FileExtractor;
import de.tu_darmstadt.elc.olw.api.media.XMLFlash;


public class MP3XMLFlash implements XMLFlash{
	private final static String AUDIO_TYPE = "audio";
	private final static String XML_ENCODING = "iso-8859-1";

	private String lectureURL;
	private String streamURL;
	private String length;

	/**
	 * @return the lectureURL
	 */
	public String getLectureURL() {
		return lectureURL;
	}

	/**
	 * @param lectureURL
	 *            the lectureURL to set
	 */
	public void setLectureURL(String lectureURL) {
		this.lectureURL = lectureURL;
	}

	/**
	 * @return the streamURL
	 */
	public String getStreamURL() {
		return streamURL;
	}

	/**
	 * @param streamURL
	 *            the streamURL to set
	 */
	public void setStreamURL(String streamURL) {
		this.streamURL = streamURL;
	}

	/**
	 * @return the length
	 */
	public String getLength() {
		return length;
	}

	/**
	 * @param length
	 *            the length to set
	 */
	public void setLength(String length) {
		this.length = length;
	}
	
	public MP3XMLFlash() {
		this.length = "0";
	}
	public MP3XMLFlash(String lectureURL, String streamURL, String length) {
		super();
		this.lectureURL = lectureURL;
		this.streamURL = streamURL;
		this.length = length;
	}

	public void createXMLFlash(File xmlFile) throws IOException{
		Element infoRoot = new Element("vorlesung");

		infoRoot.addContent(new Element("name").setText(""));
		infoRoot.addContent(new Element("veranstalter").setText(""));
		infoRoot.addContent(new Element("datum").setText(""));
		Element mediaNode = new Element("media");
		addMediaInfo(mediaNode);
		infoRoot.addContent(mediaNode);

		Element teilenNode = new Element("teilen");
		addTeilenInfo(teilenNode);
		infoRoot.addContent(teilenNode);
		FileExtractor.writeXMLwithEncoding(xmlFile, new Document(infoRoot),
				XML_ENCODING);
	}

	private void addMediaInfo(Element mediaNode) {
		mediaNode.addContent(new Element("type").setText(AUDIO_TYPE));
		mediaNode.addContent(new Element("streamURL").setText(getStreamURL()));
		mediaNode
				.addContent(new Element("lectureURL").setText(getLectureURL()));
	}

	private void addTeilenInfo(Element teilenNode) {
		Element teilNode = new Element("teil");
		teilNode.setAttribute("id", "1");
		teilNode.setAttribute("kapitel", "");
		teilNode.setAttribute("rating", "1-5");
		teilNode.addContent(new Element("startZeit").setText("0"));
		teilNode.addContent(new Element("laenge").setText(getLength()));
		teilenNode.addContent(teilNode);
	}

	@Override
	public void setSlidesVideoURL(String slidesVideoURL) {
		//do nothing
		return;
		
	}

	
}
