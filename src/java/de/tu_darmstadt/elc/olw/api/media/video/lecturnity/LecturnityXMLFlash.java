package de.tu_darmstadt.elc.olw.api.media.video.lecturnity;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;

import de.tu_darmstadt.elc.olw.api.misc.io.FileExtractor;
import de.tu_darmstadt.elc.olw.api.media.XMLFlash;



public class LecturnityXMLFlash implements XMLFlash{
	private final static String VIDEO_TYPE = "video";
	private final static String SLIDE_PREFIX = "slide";
	private final static String SLIDE_SUFFIX = ".png";
	private final static String XML_ENCODING = "iso-8859-1";

	private String mediaURL;
	private String zipfileURL;
	private String streamURL;
	private File lmdFile;
	private File evqFile;
	

	/**
	 * @param streamURL
	 *            the streamURL to set
	 */
	public void setStreamURL(String streamURL) {
		this.streamURL = streamURL;
	}

	
	/**
	 * @return the streamURL
	 */
	public String getStreamURL() {
		return streamURL;
	}

	/**
	 * @param zipfileURL
	 *            the zipfileURL to set
	 */
	public void setZipfileURL(String zipfileURL) {
		this.zipfileURL = zipfileURL;
	}

	/**
	 * @return the zipfileURL
	 */
	public String getZipfileURL() {
		return zipfileURL;
	}

	/**
	 * @param mediaURL
	 *            the mediaURL to set
	 */
	public void setMediaURL(String mediaURL) {
		this.mediaURL = mediaURL;
	}

	/**
	 * @return the mediaURL
	 */
	public String getMediaURL() {
		return mediaURL;
	}
	
	public LecturnityXMLFlash(File lmdFile, File evqFile) {
		this.lmdFile = lmdFile;
		this.evqFile = evqFile;
		this.zipfileURL = "";
		
	}
	public LecturnityXMLFlash(String mediaURL, String zipfileURL, String streamURL, File lmdFile, File evqFile) {
		super();
		this.mediaURL = mediaURL;
		this.zipfileURL = zipfileURL;
		this.streamURL = streamURL;
		this.lmdFile = lmdFile;
		this.evqFile = evqFile;
		
		
	}

	/**
	 * creates the xml info file for the fzip
	 * 
	 * @param xmlFile
	 * @param lmdFile
	 * @throws IOException
	 */
	public void createXMLFlash(File xmlFile)
			throws IOException {
		System.out.println("Adding info");
		String length = "";
		Element lmdRoot = FileExtractor.importXMLFile(lmdFile);
		// travelNode(lmdRoot);
		Element infoRoot = new Element("vorlesung");


		infoRoot.addContent(new Element("media"));
		infoRoot.addContent(new Element("teilen"));
		infoRoot.addContent(new Element("bildtext"));
		infoRoot.addContent(new Element("thumbs").setAttribute("zip_file",
				zipfileURL));

		HashMap<String, Integer> startTimeTable = FileExtractor
				.loadStartTime(evqFile.getAbsolutePath());
		
		length = addPageInfo(infoRoot.getChild("teilen"), infoRoot
				.getChild("bildtext"), infoRoot.getChild("thumbs"), lmdRoot,startTimeTable);
		addMediaInfo(infoRoot.getChild("media"), VIDEO_TYPE, mediaURL,
				calculateTime(length));

		FileExtractor.writeXMLwithEncoding(xmlFile, new Document(infoRoot),
				XML_ENCODING);
	}

	/**
	 * extracts the page info
	 * 
	 * @param bildtextNode
	 * @param thumbsNode
	 * @param lmdRoot
	 */
	
	@SuppressWarnings("rawtypes")
	private String addPageInfo(Element teilenNode, Element bildtextNode,
			Element thumbsNode, Element lmdRoot,HashMap<String,Integer> startTimeTable) {
		String length = "0";
		Element chapterNode = lmdRoot.getChild("structure").getChild("chapter");

		List pageEntries = chapterNode.getChildren();
		Iterator iterator = pageEntries.iterator();
		int id = 1;
		while (iterator.hasNext()) {
			Element pageNode = (Element) iterator.next();
			addBildTextInfo(bildtextNode, pageNode, id);
			addThumbsInfo(thumbsNode, pageNode, id,startTimeTable);
			addTeilenInfo(teilenNode, pageNode, id);
			length = pageNode.getChildText("end");
			id++;
		}
		return length;

	}

	/**
	 * 
	 * @param teilenNode
	 * @param pageNode
	 * @param id
	 */
	private void addTeilenInfo(Element teilenNode, Element pageNode, int id) {
		Element teilNode = new Element("teil");
		teilenNode.addContent(teilNode);
		teilNode.setAttribute("id", "" + id);
		teilNode.setAttribute("kapitel", "");
		teilNode.setAttribute("rating", "1-5");

		String startTime = pageNode.getChildText("begin");

		Element startZeitNode = new Element("startZeit");
		// startZeitNode.setAttribute("format", calculateTime(startTime));
		teilNode.addContent(startZeitNode);
		String length = ""
				+ (Integer.valueOf(pageNode.getChildText("end")) - Integer
						.valueOf(startTime));
		startTime = "" + Math.round(Double.valueOf(startTime) / 1000);
		startZeitNode.setText(startTime);

		Element laengeNode = new Element("laenge");
		// laengeNode.setAttribute("format", calculateTime(length));
		length = "" + Math.round(Double.valueOf(length) / 1000);
		laengeNode.setText(length);
		teilNode.addContent(laengeNode);

	}

	/**
	 * 
	 * @param thumbsNode
	 * @param pageNode
	 * @param id
	 */
	private void addThumbsInfo(Element thumbsNode, Element pageNode, int id, HashMap<String,Integer> startTimeTable) {
		Element thumbEntry = new Element("thumb");
		thumbsNode.addContent(thumbEntry);
		thumbEntry.setAttribute("id", "" + id);

		thumbEntry.addContent(new Element("thumbNumber").setText(this
				.getNumberLowerTen(pageNode.getChildText("nr"))));

		String thumbName = SLIDE_PREFIX;
		String startTime = pageNode.getChildText("begin");
		int thumbNumber = startTimeTable.get(startTime);
		if (thumbNumber / 10 < 1)
			thumbName += "000" + thumbNumber;
		else if (thumbNumber / 100 < 1)
			thumbName += "00" + thumbNumber;
		else if (thumbNumber / 1000 < 1)
			thumbName += "0" + thumbNumber;
		else
			thumbName += thumbNumber;
		thumbName += SLIDE_SUFFIX;
		
		
		thumbEntry.addContent(new Element("thumbName").setText(thumbName));
		

	}

	/**
	 * 
	 * @param bildtextNode
	 * @param pageNode
	 * @param id
	 */
	private void addBildTextInfo(Element bildtextNode, Element pageNode, int id) {
		// add bildtext element
		Element bildTextEntry = new Element("bildtext");
		bildtextNode.addContent(bildTextEntry);
		bildTextEntry.setAttribute("id", "" + id);
		bildTextEntry.setAttribute("kapitel", "");
		bildTextEntry.setAttribute("rating", "1-5");
		bildTextEntry.addContent(new Element("folieNumber").setText(this
				.getNumberLowerTen(pageNode.getChildText("nr"))));
		bildTextEntry.addContent(new Element("untertitel").setText(pageNode
				.getChildText("title")));
	}

	/**
	 * extracts the media info
	 * 
	 * @param mediaNode
	 * @param lmdRoot
	 */
	private void addMediaInfo(Element mediaNode, String type, String mediaURL,
			String length) {
		mediaNode.addContent(new Element("type").setText(type));
		mediaNode.addContent(new Element("lectureURL").setText(mediaURL));
		mediaNode.addContent(new Element("streamURL").setText(streamURL));
		// mediaNode.addContent(new Element("length").setText(length));

	}

	/**
	 * returns time in format 00:00:00.000
	 * 
	 * @param lpdTime
	 * @return
	 */
	private String calculateTime(String lpdTime) {
		String time = "";
		int timeNumber = Integer.valueOf(lpdTime);
		int millisecond = timeNumber % 1000;
		timeNumber = (timeNumber - millisecond) / 1000; // second
		int second = timeNumber % 60;
		timeNumber = (timeNumber - second) / 60; // minute
		int minute = timeNumber % 60;
		int hour = (timeNumber - minute) / 60;

		time += (hour > 9) ? hour : "0" + hour;
		time += ":";
		time += (minute > 9) ? minute : "0" + minute;
		time += ":";
		time += (second > 9) ? second : "0" + second;
		time += ":";
		if (millisecond > 99)
			time += millisecond;
		else if (millisecond > 9)
			time += "0" + millisecond;
		else
			time += "00" + millisecond;
		return time;
	}

	private String getNumberLowerTen(String number) {
		if (Integer.parseInt(number) < 10)
			return "0" + number;
		return number;
	}
	
	@SuppressWarnings("unused")
	private String getSlideName(int id) {
		String thumbName = SLIDE_PREFIX;
		if (id / 10 < 1)
			thumbName += "000" + id;
		else if (id / 100 < 1)
			thumbName += "00" + id;
		else if (id / 1000 < 1)
			thumbName += "0" + id;
		else
			thumbName += id;
		thumbName += SLIDE_SUFFIX;
		
		return thumbName;
	}


	@Override
	public void setLectureURL(String lectureURL) {
		this.mediaURL = lectureURL;
		
	}


	@Override
	public void setSlidesVideoURL(String slidesVideoURL) {
		// do nothing
		
		return;
		
	}
}
