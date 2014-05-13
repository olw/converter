package de.tu_darmstadt.elc.olw.jbi.component.producer;

import java.io.File;
import java.io.FileNotFoundException;
import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.MessagingException;
import javax.jbi.messaging.NormalizedMessage;
import javax.jbi.servicedesc.ServiceEndpoint;
import javax.xml.transform.dom.DOMSource;

import org.apache.log4j.Logger;
import org.apache.servicemix.common.endpoints.ProviderEndpoint;
import com.googlecode.sardine.util.SardineException;

import de.tu_darmstadt.elc.olw.api.misc.UUIDGenerator;
import de.tu_darmstadt.elc.olw.api.webdav.WebDAVClient;
import de.tu_darmstadt.elc.olw.jbi.component.main.ConverterComponent;
import de.tu_darmstadt.elc.olw.jbi.messages.ConverterMessage;
import de.tu_darmstadt.elc.olw.jbi.messages.StandardMessage;
import de.tu_darmstadt.elc.olw.jbi.messages.StandardStatus;

/**
 * 
 * @author Hung Tu
 * @org.apache.xbean.XBean element="material-upload-provider"
 */
public class MaterialUploader extends ProviderEndpoint {
	private static Logger logger = Logger.getLogger(MaterialUploader.class);
	private String webDAVServer;
	private String workspaceName;
	private String username;
	private String password;

	public MaterialUploader() {

	}

	public MaterialUploader(ConverterComponent component,
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

	@Override
	public void processInOut(MessageExchange exchange, NormalizedMessage in,
			NormalizedMessage out) {
		ConverterMessage msg = new ConverterMessage("material-uploader-message");
		try {
			msg.loadNodeInfo(in);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		if (!msg.isUntoleratedError()) {
			logger.info("Upload the end products");
			uploadMaterial(msg);
		}

		msg.setServiceUnit("material-uploader");
		try {
			exchange.getMessage("out").setContent(
					new DOMSource(msg.getMessage()));
		} catch (MessagingException e) {
			e.printStackTrace();
		}

	}

	private void uploadMaterial(ConverterMessage msg) {
		WebDAVClient jcr = null;
		String splittedUUID = UUIDGenerator.splitUUID(msg.getmaterialUUID());
		try {
			logger.info("Uploading end products");
			jcr = new WebDAVClient(username, password, webDAVServer,
					workspaceName);

			jcr.uploadFolder(new File(msg.getUploadFolder()), splittedUUID);
			msg.setRepositoryPath(webDAVServer + workspaceName + "/"
					+ splittedUUID.replace("-", "/"));
			msg.setLog(
					StandardMessage.getUploaderSuccessMessage(
							msg.getmaterialUUID(), msg.getRepositoryPath()),
					StandardStatus.SUCCESSFUL);
			if (jcr.isResourceReady(splittedUUID))
				logger.info("End products are uploaded.");
			else {
				logger.error("End products are not uploaded. Try to upload again");
				jcr.uploadFolder(new File(msg.getUploadFolder()), splittedUUID);
			}

				
		} catch (SardineException e) {
			if (e.getStatusCode() >= 500) 
			msg.setLog(
					StandardMessage.getUploaderErrorMessage(
							msg.getmaterialUUID(), e.getResponsePhrase()),
					StandardStatus.UNTOLERACED_ERROR);
			logger.error("By converting the material " + msg.getmaterialUUID()
					+ ", an exception has occurred: " + e.getMessage());
		} catch (FileNotFoundException e) {
			logger.error("By converting the material " + msg.getmaterialUUID()
					+ ", an exception has occurred: " + e.getMessage());
			msg.setLog(
					StandardMessage.getUploaderErrorMessage(
							msg.getmaterialUUID(), e.getMessage()),
					StandardStatus.UNTOLERACED_ERROR);
		}

	}
}
