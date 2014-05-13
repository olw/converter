package de.tu_darmstadt.elc.olw.api.media.video.lecturnity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;

import de.tu_darmstadt.elc.olw.api.misc.io.FileExtractor;

public class ConfigFileModifier {

	// private static final Integer TIME_DELTA = 500; // 500 ms

	@SuppressWarnings("rawtypes")
	public static void customizeEVQFile(File evqFile, File lmdFile,
			File newEVQFile) throws IOException {
		if (newEVQFile.exists() && newEVQFile.length() > 0)
			return;
		HashMap<String, Boolean> timeTable = new HashMap<String, Boolean>();
		Element lmdRoot = FileExtractor.importXMLFile(lmdFile);
		Element structureNode = lmdRoot.getChild("structure");
		List chapterList = structureNode.getChildren();
		Iterator chapterIter = chapterList.iterator();
		while (chapterIter.hasNext()) {
			Element chapterNode = (Element) chapterIter.next();
			List pageList = chapterNode.getChildren();
			Iterator pageIter = pageList.iterator();
			while (pageIter.hasNext()) {
				Element pageNode = (Element) pageIter.next();
				String startTime = pageNode.getChildText("begin");
				timeTable.put(startTime, true);
			}
		}

		@SuppressWarnings("resource")
		BufferedReader evqBuffer = new BufferedReader(new InputStreamReader(
				new FileInputStream(evqFile), "UTF-8"));
		FileWriter fw = new FileWriter(newEVQFile);
		BufferedWriter bw = new BufferedWriter(fw);
		String line = "";

		while ((line = evqBuffer.readLine()) != null) {
			String[] tokens = line.split(" ");
			String startTime = tokens[0];
			if (timeTable.containsKey(startTime)) {
				bw.write(line);
				bw.newLine();
				timeTable.remove(startTime);
			}

		}
		bw.flush();
		bw.close();
		fw.close();
	}

	public static void customizeLMDFile(File lmdFile, File newLMDFile)
			throws IOException {
		if (newLMDFile.exists() && newLMDFile.length() > 0)
			return;
		@SuppressWarnings("resource")
		BufferedReader lmdBuffer = new BufferedReader(new InputStreamReader(
				new FileInputStream(lmdFile), "UTF-8"));
		FileWriter fw = new FileWriter(newLMDFile);
		BufferedWriter bw = new BufferedWriter(fw);
		String line = "";
		while ((line = lmdBuffer.readLine()) != null) {
			bw.write(line.replace("&", " "));
			bw.newLine();

		}
		bw.flush();
		bw.close();
		fw.close();
	}
}
