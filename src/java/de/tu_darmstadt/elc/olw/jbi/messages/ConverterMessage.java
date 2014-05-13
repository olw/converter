package de.tu_darmstadt.elc.olw.jbi.messages;

import java.io.IOException;

import javax.jbi.messaging.MessagingException;
import javax.jbi.messaging.NormalizedMessage;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.servicemix.jbi.jaxp.SourceTransformer;
import org.jdom.Element;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.output.XMLOutputter;
import org.xml.sax.SAXException;

/**
 * This class describes the general messages that are used in converter
 * component
 * 
 * 
 * 
 */
public class ConverterMessage {

	private Element msgNode;
	

	/**
	 * @param msgNode
	 *            the msgNode to set
	 */
	public void setMsgNode(Element msgNode) {
		this.msgNode = msgNode;
	}

	/**
	 * @return the msgNode
	 */
	public Element getMsgNode() {
		return msgNode;
	}

	public ConverterMessage(String name) {

		msgNode = new Element(name);
		msgNode.addContent(new Element("serviceUnit"));
		msgNode.addContent(new Element("originalTime"));
		msgNode.addContent(new Element("materialUUID"));
		msgNode.addContent(new Element("repositoryPath"));
		msgNode.addContent(new Element("mimeType"));
		msgNode.addContent(new Element("contents"));
		msgNode.addContent(new Element("contentSize"));
		msgNode.addContent(new Element("localFolder"));
		msgNode.addContent(new Element("uploadFolder"));
		msgNode.addContent(new Element("tempFolder"));
		msgNode.addContent(new Element("logContent"));
		msgNode.addContent(new Element("status"));
		msgNode.addContent(new Element("uploaderEmails"));
	}

	public void setServiceUnit(String name) {
		msgNode.getChild("serviceUnit").setText(name);
	}

	public void setMaterialUUID(String materialUUID) {
		msgNode.getChild("materialUUID").setText(materialUUID);
	}

	public void setRepositoryPath(String repositoryPath) {
		msgNode.getChild("repositoryPath").setText(repositoryPath);
	}

	public void setContents(String contents) {
		msgNode.getChild("contents").setText(contents);
	}

	public void setLocalFolder(String localFolder) {
		msgNode.getChild("localFolder").setText(localFolder);
	}

	public void setUploadFolder(String uploadFolder) {
		msgNode.getChild("uploadFolder").setText(uploadFolder);
	}

	public void setTempFolder(String tempFolder) {
		msgNode.getChild("tempFolder").setText(tempFolder);
	}

	public void setLog(String logContent, String status) {
		msgNode.getChild("logContent").setText(logContent);
		String oldStatus = getStatus();
		if (StandardStatus.getPriority(oldStatus) < StandardStatus
				.getPriority(status))
			return;
		else
			msgNode.getChild("status").setText(status);
	}

	public void setOriginalTime(String originalTime) {
		msgNode.getChild("originalTime").setText(originalTime);
	}

	public void setContentSize(String contentSize) {
		msgNode.getChild("contentSize").setText(contentSize);
	}

	public void setUploaderEmails(String uploaderEmails) {
		msgNode.getChild("uploaderEmails").setText(uploaderEmails);
	}

	/**
	 * loads the info from the input message
	 * 
	 * @param in
	 * @throws SAXException
	 * @throws IOExceptionnewValue
	 * @throws ParserConfigurationException
	 * @throws TransformerException
	 * @throws MessagingException
	 */

	public void loadUUID(NormalizedMessage in) throws MessagingException,
			TransformerException, ParserConfigurationException, IOException,
			SAXException {
		if (in == null)
			return;
		SourceTransformer sourceTransformer = null;
		sourceTransformer = new SourceTransformer();

		org.w3c.dom.Document doc = sourceTransformer.toDOMDocument(in);
		org.w3c.dom.Element root = doc.getDocumentElement();

		this.setMaterialUUID(root.getFirstChild().getTextContent());
		this.setUploaderEmails(root.getLastChild().getTextContent());

	}

	public void loadNodeInfo(NormalizedMessage in) throws MessagingException,
			TransformerException, ParserConfigurationException, IOException,
			SAXException {
		if (in == null)
			return;

		SourceTransformer sourceTransformer = null;
		sourceTransformer = new SourceTransformer();

		org.w3c.dom.Document doc = sourceTransformer.toDOMDocument(in);

		org.w3c.dom.Element root = doc.getDocumentElement();

		this.setServiceUnit(root.getElementsByTagName("serviceUnit").item(0)
				.getTextContent());
		this.setOriginalTime(root.getElementsByTagName("originalTime").item(0)
				.getTextContent());
		this.setMaterialUUID(root.getElementsByTagName("materialUUID").item(0)
				.getTextContent());
		this.setRepositoryPath(root.getElementsByTagName("repositoryPath")
				.item(0).getTextContent());
		this.setContentSize(root.getElementsByTagName("contentSize").item(0)
				.getTextContent());
		// set Contents
		this.setContents(root.getElementsByTagName("contents").item(0)
				.getTextContent());

		this.setMimeType(root.getElementsByTagName("mimeType").item(0)
				.getTextContent());

		this.setLocalFolder(root.getElementsByTagName("localFolder").item(0)
				.getTextContent());
		this.setUploadFolder(root.getElementsByTagName("uploadFolder").item(0)
				.getTextContent());
		this.setTempFolder(root.getElementsByTagName("tempFolder").item(0)
				.getTextContent());
		this.setLogContent(root.getElementsByTagName("logContent").item(0)
				.getTextContent());
		this.setStatus(root.getElementsByTagName("status").item(0)
				.getTextContent());
		this.setUploaderEmails(root.getElementsByTagName("uploaderEmails")
				.item(0).getTextContent());

	}

	public void setMimeType(String mimeType) {
		msgNode.getChild("mimeType").setText(mimeType);

	}

	public void setStatus(String status) {
		msgNode.getChild("status").setText(status);
	}

	public void setLogContent(String content) {
		msgNode.getChild("logContent").setText(content);
	}

	public org.w3c.dom.Document getMessage() {

		Document doc = new Document(msgNode);
		org.jdom.output.DOMOutputter exporter = new org.jdom.output.DOMOutputter();
		try {
			return exporter.output(doc);
		} catch (JDOMException e) {
			return null;
		}
	}

	public String[] getContents() {
		String contents = msgNode.getChild("contents").getText();
		if (!contents.equals(""))
			return contents.split(";");
		return null;
	}

	public String getRepositoryPath() {
		return msgNode.getChildText("repositoryPath");
	}

	public String getLocalFolder() {
		return msgNode.getChildText("localFolder");
	}

	public String getUploadFolder() {
		return msgNode.getChildText("uploadFolder");
	}

	public String getTempFolder() {
		return msgNode.getChildText("tempFolder");
	}

	public String getStatus() {
		return msgNode.getChildText("status");
	}

	public String getLogContent() {
		return msgNode.getChildText("logContent");
	}

	public String getmaterialUUID() {
		return msgNode.getChildText("materialUUID");
	}

	public String getServiceName() {
		return msgNode.getChildText("serviceUnit");
	}

	public String getOriginalTime() {
		return msgNode.getChildText("originalTime");
	}

	public String getContentSize() {
		return msgNode.getChildText("contentSize");
	}

	public String getMimeType() {
		return msgNode.getChildText("mimeType");
	}

	public String getUploaderEmails() {
		return msgNode.getChildText("uploaderEmails");
	}

	public boolean contentMedia(String mediaType) {
		return msgNode.getChildText("contents").contains(mediaType);

	}

	public void clearlogContent() {
		this.setLogContent("");
		this.setStatus("");

	}

	public void appendLogContent(String newContent) {
		String newEntry = this.getLogContent();
		newEntry += System.getProperty("line.separator") + newContent;
		this.setLogContent(newEntry);
	}

	public boolean isSuccessful() {
		return getStatus().equals(StandardStatus.SUCCESSFUL);
	}

	public boolean isToleratedError() {
		return getStatus().equals(StandardStatus.TOLERACED_ERROR);
	}

	public boolean isUntoleratedError() {
		return getStatus().equals(StandardStatus.UNTOLERACED_ERROR);
	}

	public boolean isFinished() {
		return getStatus().equals(StandardStatus.FINISHED);
	}
	
	public String toString() {
		XMLOutputter output = new XMLOutputter();
		return output.outputString(msgNode);
		
	}

}
