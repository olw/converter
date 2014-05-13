package de.tu_darmstadt.elc.olw.api.media.video.camrec;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.jdom.Document;
import org.jdom.Element;

import com.adobe.xmp.XMPException;
import com.adobe.xmp.XMPMeta;
import com.adobe.xmp.XMPMetaFactory;

import de.tu_darmstadt.elc.olw.api.misc.io.FileExtractor;
import de.tu_darmstadt.elc.olw.api.media.XMLFlash;

public class CamRecXMLFlash implements XMLFlash {
	private final static String XML_ENCODING = "iso-8859-1";
	private final static String SCHEMA_NS = "http://ns.adobe.com/xmp/1.0/DynamicMedia/";
	private final static int START_TIME = 0;
	private final static int LENGTH = 1;
	private final static int TITLE = 2;

	private File configFile;
	private HashMap<String, String[]> timeTable;
	private boolean hasManyParts;
	private int countIndex;
	private String lectureURL;
	private String slidesVideoURL;
	private String streamURL;

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
	 * @return the slidesVideoURL
	 */
	public String getSlidesVideoURL() {
		return slidesVideoURL;
	}

	/**
	 * @param slidesVideoURL
	 *            the slidesVideoURL to set
	 */
	public void setSlidesVideoURL(String slidesVideoURL) {
		this.slidesVideoURL = slidesVideoURL;
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

	public CamRecXMLFlash(File configFile, boolean hasManyParts)
			throws IOException {
		this.configFile = configFile;
		this.countIndex = 0;
		this.timeTable = new HashMap<String, String[]>();
		this.hasManyParts = hasManyParts;
		try {
			if (hasManyParts)
				loadTimeInfoManyParts();
			else
				loadTimeInfoOnePart();
		} catch (Exception e) {
			// XMP Data
			try {
				loadTimeInfoXMP();
			} catch (XMPException e1) {
				throw new IOException();
			}
		}

	}

	private void loadTimeInfoXMP() throws FileNotFoundException, XMPException {
		InputStream in = new FileInputStream(configFile.getAbsolutePath());
		XMPMeta meta = XMPMetaFactory.parse(in);
		int trackNum = meta.countArrayItems(SCHEMA_NS, "xmpDM:Tracks");
		long duration = Long.parseLong(meta.getStructField(SCHEMA_NS,
				"xmpDM:duration", SCHEMA_NS, "value").getValue()) / 1000;
		long baseTime = duration;
		for (int i = 1; i <= trackNum; i++) {
			int markerNum = meta.countArrayItems(SCHEMA_NS, "xmpDM:Tracks[" + i
					+ "]/xmpDM:markers");
			for (int j = markerNum; j >= 1; j--) {
				String key = "xmpDM:Tracks[" + i + "]/xmpDM:markers[" + j + "]";
				long startTime = Long.parseLong(meta.getStructField(SCHEMA_NS,
						key, SCHEMA_NS, "startTime").getValue()) / 1000;
				String[] timeInfo = new String[3];
				timeInfo[START_TIME] = "" + startTime;
				long length = baseTime - startTime;
				timeInfo[LENGTH] = "" + length;
				timeInfo[TITLE] = meta.getStructField(SCHEMA_NS, key,
						SCHEMA_NS, "name").getValue();
				baseTime = startTime;
				timeTable.put(key, timeInfo);
			}
		}

	}

	@SuppressWarnings("rawtypes")
	private void loadTimeInfoOnePart_aux(Element parentNode,
			Vector<Double> startTime) {
		List childEntries = parentNode.getChildren();
		Iterator iterator = childEntries.iterator();

		while (iterator.hasNext()) {
			Element childNode = (Element) iterator.next();
			startTime.add(Double.parseDouble(childNode
					.getAttributeValue("time")));
			if (childNode.getChildren().size() > 0){
				loadTimeInfoOnePart_aux(childNode, startTime);
			}			
		}
	}

	private void loadTimeInfoOnePart() throws IOException {
		Vector<Double> startTime = new Vector<Double>();
		Element configRoot = FileExtractor.importXMLFile(configFile);
		double totalDuration = Double.parseDouble(configRoot
				.getChild("playlist").getChild("array").getChild("fileset")
				.getChild("video1").getChildText("duration"));

		Element tocInfoNode = configRoot.getChild("tocInfo");
		this.loadTimeInfoOnePart_aux(tocInfoNode, startTime);
		for (int i = 0; i < startTime.size(); i++) {
			String[] timeInfo = new String[2];
			timeInfo[START_TIME] = "" + startTime.get(i);
			if (i < startTime.size() - 1)
				timeInfo[LENGTH] = ""
						+ (startTime.get(i + 1) - startTime.get(i));
			else
				timeInfo[LENGTH] = "" + (totalDuration - startTime.get(i));
			timeTable.put("" + i, timeInfo);

		}

	}

	@SuppressWarnings("rawtypes")
	private void loadTimeInfoManyParts() throws IOException {
		Element configRoot = FileExtractor.importXMLFile(configFile);
		Element arrayNode = configRoot.getChild("playlist").getChild("array");

		List arrayEntries = arrayNode.getChildren();
		Iterator iterator = arrayEntries.iterator();

		double baseTime = 0;
		int fileset = 0;
		while (iterator.hasNext()) {
			Element filesetNode = (Element) iterator.next();
			Element durationNode = filesetNode.getChild("video1").getChild(
					"duration");
			String[] timeInfo = new String[2];
			timeInfo[START_TIME] = "" + baseTime;
			timeInfo[LENGTH] = durationNode.getText();

			double length = Double.parseDouble(timeInfo[LENGTH]);
			timeTable.put("" + fileset, timeInfo);
			baseTime = baseTime + length;
			fileset++;
		}

	}

	public void createXMLFlash(File xmlFile) throws IOException {

		Element infoRoot = new Element("vorlesung");

		Element mediaNode = new Element("media");
		infoRoot.addContent(mediaNode);
		mediaNode.addContent(new Element("type"));
		mediaNode.getChild("type").setText("video");
		mediaNode.addContent(new Element("lectureURL"));
		mediaNode.getChild("lectureURL").setText(lectureURL);
		mediaNode.addContent(new Element("slidesVideoURL"));
		mediaNode.getChild("slidesVideoURL").setText(slidesVideoURL);
		mediaNode.addContent(new Element("streamURL"));
		mediaNode.getChild("streamURL").setText(streamURL);

		infoRoot.addContent(new Element("teilen"));

		try {
			addPageInfo(infoRoot.getChild("teilen"));
		} catch (XMPException e) {
			throw new IOException();
		}

		FileExtractor.writeXMLwithEncoding(xmlFile, new Document(infoRoot),
				XML_ENCODING);
	}

	@SuppressWarnings("rawtypes")
	private void addPageInfo(Element teilenNode) throws FileNotFoundException,
			XMPException {
		try {
			Element configRoot = FileExtractor.importXMLFile(configFile);
			Element tocInfoNode = configRoot.getChild("tocInfo");

			List infoEntries = tocInfoNode.getChildren();
			Iterator iterator = infoEntries.iterator();
			
			
			if (iterator.hasNext()){

				while (iterator.hasNext()) {
					Element infoNode = (Element) iterator.next();
	
					addTeilenInfo(teilenNode, infoNode, countIndex);
					countIndex++;
				}

			}

			// if there are no childrens in <tocInfo>
			else{
				addTeilenInfoEmpty(teilenNode, 1, countIndex);
			}
			
		} catch (Exception e) {
			addPageInfoFromXMP(teilenNode);
		}

	}

	private void addPageInfoFromXMP(Element teilenNode)
			throws FileNotFoundException, XMPException {
		InputStream in = new FileInputStream(configFile.getAbsolutePath());
		XMPMeta meta = XMPMetaFactory.parse(in);
		int trackNum = meta.countArrayItems(SCHEMA_NS, "xmpDM:Tracks");
		
		for (int i = 1; i <= trackNum; i++) {
			int markerNum = meta.countArrayItems(SCHEMA_NS, "xmpDM:Tracks[" + i
					+ "]/xmpDM:markers");
			for (int j = 1; j <= markerNum; j++) {
				countIndex++;
				String key = "xmpDM:Tracks[" + i + "]/xmpDM:markers[" + j + "]";
				String[] timeInfo = timeTable.get(key);
				Element teilNode = new Element("teil");
				teilenNode.addContent(teilNode);
				teilNode.setAttribute("id", "" + countIndex);
				teilNode.setAttribute("kapitel", timeInfo[TITLE]);
				teilNode.setAttribute("rating", "1-5");
				Element startZeitNode = new Element("startZeit");
				teilNode.addContent(startZeitNode);
				startZeitNode.setText(timeInfo[START_TIME]);
				Element laengeNode = new Element("laenge");
				laengeNode.setText(timeInfo[LENGTH]);
				teilNode.addContent(laengeNode);
				

			}
		}

	}

	@SuppressWarnings("rawtypes")
	private void addTeilenInfo(Element teilenNode, Element infoNode, int index) {
		Element teilNode = new Element("teil");
		teilenNode.addContent(teilNode);
		if (!hasManyParts)
			teilNode.setAttribute("id", "" + (index + 1));
		else
			teilNode.setAttribute(
					"id",
					""
							+ (Integer.parseInt(infoNode
									.getAttributeValue("fileset")) + 1));

		teilNode.setAttribute("kapitel", infoNode.getAttributeValue("label"));
		teilNode.setAttribute("rating", "1-5");

		String[] timeInfo;
		if (!hasManyParts)
			timeInfo = timeTable.get("" + index);
		else
			timeInfo = timeTable.get(infoNode.getAttributeValue("fileset"));
		Element startZeitNode = new Element("startZeit");

		teilNode.addContent(startZeitNode);
		startZeitNode.setText(timeInfo[START_TIME]);
		Element laengeNode = new Element("laenge");
		laengeNode.setText(timeInfo[LENGTH]);
		teilNode.addContent(laengeNode);
		if (infoNode.getChildren().size() > 0) {
			List infoEntries = infoNode.getChildren();
			Iterator iterator = infoEntries.iterator();

			while (iterator.hasNext()) {
				Element childNode = (Element) iterator.next();
				countIndex++;
				addTeilenInfo(teilenNode, childNode, countIndex);

			}
		}

	}
	
	
	private void addTeilenInfoEmpty(Element teilenNode, int infoNode, int index) {
		Element teilNode = new Element("teil");
		teilenNode.addContent(teilNode);

		teilNode.setAttribute("id", "" + (index + 1));
		teilNode.setAttribute("kapitel", "");
		teilNode.setAttribute("rating", "1-5");

		String[] timeInfo;
	
		timeInfo = timeTable.get("" + index);
		Element startZeitNode = new Element("startZeit");
		teilNode.addContent(startZeitNode);
		startZeitNode.setText(timeInfo[START_TIME]);
		Element laengeNode = new Element("laenge");
		laengeNode.setText(timeInfo[LENGTH]);
		teilNode.addContent(laengeNode);


	}

}
