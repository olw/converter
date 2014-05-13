package de.tu_darmstadt.elc.olw.jbi.component.producer;

import java.io.File;
import java.io.IOException;

import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.MessagingException;
import javax.jbi.messaging.NormalizedMessage;
import javax.jbi.servicedesc.ServiceEndpoint;

import javax.xml.transform.dom.DOMSource;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.servicemix.common.endpoints.ProviderEndpoint;
import com.googlecode.sardine.util.SardineException;

import de.tu_darmstadt.elc.olw.api.misc.io.FileExtractor;
import de.tu_darmstadt.elc.olw.api.misc.UUIDGenerator;
import de.tu_darmstadt.elc.olw.api.webdav.WebDAVClient;
import de.tu_darmstadt.elc.olw.jbi.component.main.ConverterComponent;
import de.tu_darmstadt.elc.olw.jbi.messages.ConverterMessage;
import de.tu_darmstadt.elc.olw.jbi.messages.StandardMessage;
import de.tu_darmstadt.elc.olw.jbi.messages.StandardStatus;

/**
 * 
 * @author Hung Tu
 * @org.apache.xbean.XBean element="material-download-provider"
 */
public class MaterialDownloader extends ProviderEndpoint {

	private String webDAVServer;
	private String username;
	private String password;
	private String workspaceName;
	private String localDirectory;

	private static Logger logger = Logger.getLogger(MaterialDownloader.class);

	public MaterialDownloader() {
		super();
	}

	public MaterialDownloader(ConverterComponent component,
			ServiceEndpoint endpoint) {
		super(component, endpoint);
	}

	/**
	 * @return the webDAVServer
	 */
	public String getWebDAVServer() {
		return webDAVServer;
	}

	/**
	 * @param webDAVServer
	 *            the webDAVServer to set
	 */
	public void setWebDAVServer(String webDAVServer) {
		this.webDAVServer = webDAVServer;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username
	 *            the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the workspaceName
	 */
	public String getWorkspaceName() {
		return workspaceName;
	}

	/**
	 * @param workspaceName
	 *            the workspaceName to set
	 */
	public void setWorkspaceName(String workspaceName) {
		this.workspaceName = workspaceName;
	}

	/**
	 * @return the localDirectory
	 */
	public String getLocalDirectory() {
		return localDirectory;
	}

	/**
	 * @param localDirectory
	 *            the localDirectory to set
	 */
	public void setLocalDirectory(String localDirectory) {
		this.localDirectory = localDirectory;
	}

	@Override
	/**
	 * processes the message
	 */
	public void processInOut(MessageExchange exchange, NormalizedMessage in,
			NormalizedMessage out) {
		ConverterMessage msg = new ConverterMessage(
				"material-downloader-message");
		
		try {
			msg.loadUUID(in);
			logger.info("[Material-Downloader] " + msg.toString());
		} catch (Exception e) {
			logger.error("[Material-Downloader] Error by loading message from activemq");
			e.printStackTrace();
		}
		createDirs(msg);
		logger.info("[Material-Downloader] Downloading material ...");
		downloadMaterial(msg);
		msg.setOriginalTime("" + System.currentTimeMillis());
		msg.setServiceUnit("material-downloader");
		try {
			exchange.getMessage("out").setContent(
					new DOMSource(msg.getMessage()));
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * creates temporary and upload folders
	 * 
	 * @param msg
	 */
	private void createDirs(ConverterMessage msg) {
		File downloadDest = new File(localDirectory, msg.getmaterialUUID());
		logger.info("Creating folders for converting process");
		try {
			FileUtils.forceMkdir(downloadDest);
		} catch (IOException e) {

			e.printStackTrace();
		}

		msg.setLocalFolder(downloadDest.getAbsolutePath());
		File uploadFolder = new File(msg.getLocalFolder(),
				msg.getmaterialUUID() + "-" + FileExtractor.getDateTime() + "-"
						+ "upload");
		File tempFolder = new File(msg.getLocalFolder(), msg.getmaterialUUID()
				+ "-" + FileExtractor.getDateTime() + "-" + "temp");
		File red5Folder = new File(uploadFolder, "red5");
		msg.setUploadFolder(uploadFolder.getAbsolutePath());
		msg.setTempFolder(tempFolder.getAbsolutePath());
		uploadFolder.mkdirs();
		tempFolder.mkdirs();
		red5Folder.mkdirs();
	}

	/**
	 * downloads the material in the defined destination
	 * 
	 * @param msg
	 */
	private void downloadMaterial(ConverterMessage msg) {
		WebDAVClient jcr = null;
		File downloadDest = new File(localDirectory, msg.getmaterialUUID());
		try {
			jcr = new WebDAVClient(username, password, webDAVServer,
					workspaceName);
			String splittedUUID = UUIDGenerator
					.splitUUID(msg.getmaterialUUID());
			String contents = jcr.getListFile(splittedUUID);
			logger.info("Material contents: " + contents);
			msg.setContents(contents);
			msg.setOriginalTime("" + System.currentTimeMillis());
			logger.info("Downloading material with uuid "
					+ msg.getmaterialUUID());
			if (jcr.isEmpty(splittedUUID)) {
				logger.warn("The folder with uuid " + msg.getmaterialUUID()
						+ " is empty.");
			}
			jcr.downloadFolder(splittedUUID, downloadDest);
			msg.setLog(
					StandardMessage.getDownloaderSuccessMessage(
							msg.getmaterialUUID(), msg.getLocalFolder()),
					StandardStatus.SUCCESSFUL);
		} catch (SardineException e) {			
			logger.error("The material with uuid " + msg.getmaterialUUID()
					+ " cannot be downloaded.");
			logger.error(e.getMessage());
			msg.setLog(
					StandardMessage.getDownloaderErrorMessage(
							msg.getmaterialUUID(), e.getMessage()),
					StandardStatus.UNTOLERACED_ERROR);
		} catch (IOException e) {
			logger.error(e.getMessage());
			msg.setLog(
					StandardMessage.getDownloaderErrorMessage(
							msg.getmaterialUUID(), e.getMessage()),
					StandardStatus.UNTOLERACED_ERROR);
		}
	}

}
