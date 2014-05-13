package de.tu_darmstadt.elc.olw.api.document;

import java.io.File;

import org.jdom.Document;
import org.jdom.Element;

import de.tu_darmstadt.elc.olw.api.misc.io.FileExtractor;

public class PDFXMLFlash {
	private final static String XML_ENCODING = "iso-8859-1";

	public static void createXMLFlash(File imageFolder,File xmlFile) {
		Element vorlesungNode = new Element ("vorlesung");
		vorlesungNode.addContent(new Element("thumbs"));
		File[] fileList = imageFolder.listFiles();
		for (int i = 0; i < fileList.length; i++)
			if (fileList[i].getName().endsWith(".png")) {
				int id = extractID(fileList[i].getName());
				addThumbsInfo(vorlesungNode.getChild("thumbs"), id,
						fileList[i].getName());
			}

		FileExtractor.writeXMLwithEncoding(xmlFile, new Document(vorlesungNode),
				XML_ENCODING);
	}

	private static void addThumbsInfo(Element thumbsNode, int id, String fileName) {
		Element thumbEntry = new Element("thumb");
		thumbsNode.addContent(thumbEntry);
		thumbEntry.setAttribute("id", "" + id);

		thumbEntry.addContent(new Element("untertitle").setText("Slide " + id
				+ " untertitle"));
		thumbEntry.addContent(new Element("thumbName").setText(fileName));

	}
	
	private static int extractID(String fileName) {

		String idString = fileName.replace("slide", "");
		idString = idString.replace(".png", "");
		return Integer.parseInt(idString);
	}
	
	
}
