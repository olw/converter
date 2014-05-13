/**
 * 
 */
package de.tu_darmstadt.elc.olw.jbi.component.producer;

import java.io.File;
import java.io.IOException;

import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.MessagingException;
import javax.jbi.messaging.NormalizedMessage;
import javax.jbi.servicedesc.ServiceEndpoint;
import javax.xml.transform.dom.DOMSource;

import org.apache.log4j.Logger;
import org.apache.servicemix.common.endpoints.ProviderEndpoint;

import de.tu_darmstadt.elc.olw.api.constant.MaterialType;
import de.tu_darmstadt.elc.olw.api.converter.OLWConverter;
import de.tu_darmstadt.elc.olw.api.misc.ThumbnailCreator;
import de.tu_darmstadt.elc.olw.api.misc.execution.ExecutionException;
import de.tu_darmstadt.elc.olw.jbi.component.main.ConverterComponent;
import de.tu_darmstadt.elc.olw.jbi.messages.ConverterMessage;
import de.tu_darmstadt.elc.olw.jbi.messages.StandardMessage;
import de.tu_darmstadt.elc.olw.jbi.messages.StandardStatus;

/**
 * @author Hung Tu
 * @org.apache.xbean.XBean element="material-converter-provider"
 * 
 */
public class MaterialConverter extends ProviderEndpoint {

	private static Logger logger = Logger.getLogger(MaterialConverter.class);

	private String ffmpegPath;
	private String thumbnailPath;

	public MaterialConverter() {
		super();
	}

	public MaterialConverter(ConverterComponent component,
			ServiceEndpoint endpoint) {
		super(component, endpoint);

	}

	/**
	 * @return the ffmpegPath
	 */
	public String getFfmpegPath() {
		return ffmpegPath;
	}

	/**
	 * @param ffmpegPath
	 *            the ffmpegPath to set
	 */
	public void setFfmpegPath(String ffmpegPath) {
		this.ffmpegPath = ffmpegPath;
	}

	/**
	 * @param thumbnailPath
	 *            the thumbnailPath to set
	 */
	public void setThumbnailPath(String thumbnailPath) {
		this.thumbnailPath = thumbnailPath;
	}

	/**
	 * @return the thumbnailPath
	 */
	public String getThumbnailPath() {
		return thumbnailPath;
	}

	@Override
	protected void processInOut(MessageExchange messageExchange,
			NormalizedMessage in, NormalizedMessage out) {
		ConverterMessage msg = new ConverterMessage(
				"material-converter-message");
		try {
			msg.loadNodeInfo(in);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (msg.isUntoleratedError()) {
			logger.error("Material with uuid " + msg.getmaterialUUID()
					+ " does not exist");
			msg.setLog(
					StandardMessage.getPreviousErrorMessage(
							msg.getmaterialUUID(), msg.getServiceName()),
					StandardStatus.UNTOLERACED_ERROR);
		}

		else {
			convertMaterial(msg);
			createThumbnail(msg);
			
		}
		msg.setServiceUnit(msg.getMimeType() + "-converter");
		try {
			messageExchange.getMessage("out").setContent(
					new DOMSource(msg.getMessage()));
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Converts material that are included in the message msg
	 * 
	 * @param msg
	 */
	private void convertMaterial(ConverterMessage msg) {
		File materialFolder = new File(msg.getLocalFolder());
		File tmpFolder = new File(msg.getTempFolder());
		File uploadFolder = new File(msg.getUploadFolder());

		File logFile = null;
		String[] contents = msg.getContents();
		OLWConverter converter = new OLWConverter(ffmpegPath, materialFolder,
				tmpFolder);
		String mimeType = "RAW";
		for (int i = 0; i < contents.length; i++) {
			File materialFile = new File(msg.getLocalFolder(), contents[i]);
			
			try {
				logger.info("Material: " + contents[i]);
				logger.info("Converting material: " + msg.getmaterialUUID());
				converter.convertMaterial(materialFile, uploadFolder,
						msg.getmaterialUUID(), logFile);
				mimeType = converter.getMaterialType();
				msg.setMimeType(mimeType);
				
				if (msg.isSuccessful())
					msg.setLog(
							StandardMessage.getConverterSuccessMessage(
									msg.getmaterialUUID(), msg.getMimeType()
											+ "-converter"),
							StandardStatus.SUCCESSFUL);
			} catch (ExecutionException e) {
				logger.error("By converting the material "
						+ msg.getmaterialUUID()
						+ ", an exception has occurred: " + e.getMessage());
				msg.setLog(
						StandardMessage.getConverterConversionErrorMessage(
								msg.getmaterialUUID(), e.getMessage()),
						StandardStatus.TOLERACED_ERROR);
			} catch (IOException e) {
				logger.error("By converting the material "
						+ msg.getmaterialUUID()
						+ ", an exception has occurred: " + e.getMessage());
				msg.setLog(
						StandardMessage.getConverterConversionErrorMessage(
								msg.getmaterialUUID(), e.getMessage()),
						StandardStatus.TOLERACED_ERROR);
			}
		}

	}

	/**
	 * creates thumbnail
	 * 
	 * @param msg
	 */
	private void createThumbnail(ConverterMessage msg) {
		logger.info("Creating thumbnail");
		ThumbnailCreator thumbCreator = new ThumbnailCreator(ffmpegPath,
				thumbnailPath);
		File uploadFolder = new File(msg.getUploadFolder());
		try {
			thumbCreator.createThumbnail(uploadFolder,
					MaterialType.toMaterialType(msg.getMimeType()));
		} catch (IOException e) {
			logger.error("By converting the material " + msg.getmaterialUUID()
					+ ", an exception has occurred: " + e.getMessage());
			msg.setLog(
					StandardMessage.getConverterConversionErrorMessage(
							msg.getmaterialUUID(), e.getMessage()),
					StandardStatus.TOLERACED_ERROR);
		} catch (ExecutionException e) {
			logger.error("By converting the material " + msg.getmaterialUUID()
					+ ", an exception has occurred: " + e.getMessage());
			msg.setLog(
					StandardMessage.getConverterConversionErrorMessage(
							msg.getmaterialUUID(), e.getMessage()),
					StandardStatus.TOLERACED_ERROR);
		} catch (Exception e) {
			logger.error("By converting the material " + msg.getmaterialUUID()
					+ ", an exception has occurred: " + e.getMessage());
			
		}
	}

	
}
