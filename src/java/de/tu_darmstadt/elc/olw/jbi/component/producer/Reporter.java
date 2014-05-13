package de.tu_darmstadt.elc.olw.jbi.component.producer;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.NormalizedMessage;
import javax.jbi.servicedesc.ServiceEndpoint;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.xml.transform.dom.DOMSource;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.servicemix.common.endpoints.ProviderEndpoint;
import org.jdom.Element;

import de.tu_darmstadt.elc.olw.api.constant.MaterialType;
import de.tu_darmstadt.elc.olw.api.misc.UUIDGenerator;
import de.tu_darmstadt.elc.olw.api.misc.io.FileExtractor;
import de.tu_darmstadt.elc.olw.api.restdb.RestClient;
import de.tu_darmstadt.elc.olw.jbi.component.main.ConverterComponent;
import de.tu_darmstadt.elc.olw.jbi.messages.ConverterMessage;
import de.tu_darmstadt.elc.olw.jbi.messages.Report;
import de.tu_darmstadt.elc.olw.jbi.messages.StandardMessage;

/**
 * 
 * @author hungtu
 * @org.apache.xbean.XBean element="reporter-provider"
 */
public class Reporter extends ProviderEndpoint {
	private static final String HTML_BREAK="<br>";
	private static Logger logger = Logger.getLogger(Reporter.class);

	private String smtpHostName;
	private String smtpHostPort;
	private String adminAddress;
	private String repositoryRoh;
	private String repositoryKonv;
	private String archiveWorkspace;
	private String materialWorkspace;

	// for restdb client
	private String fakeLogIn;
	private String apiURL;

	// for emails
	private String reporterAddress;
	private String reporterMessageTemplate;

	public Reporter() {
		super();
	}

	public Reporter(ConverterComponent component, ServiceEndpoint endpoint) {
		super(component, endpoint);
		
	}

	/**
	 * @return the smtpHostName
	 */
	public String getSmtpHostName() {
		return smtpHostName;
	}

	/**
	 * @param smtpHostName
	 *            the smtpHostName to set
	 */
	public void setSmtpHostName(String smtpHostName) {
		this.smtpHostName = smtpHostName;
	}

	/**
	 * @return the smtpHostPort
	 */
	public String getSmtpHostPort() {
		return smtpHostPort;
	}

	/**
	 * @param smtpHostPort
	 *            the smtpHostPort to set
	 */
	public void setSmtpHostPort(String smtpHostPort) {
		this.smtpHostPort = smtpHostPort;
	}

	
	
	/**
	 * @return the repositoryRoh
	 */
	public String getRepositoryRoh() {
		return repositoryRoh;
	}

	/**
	 * @param repositoryRoh
	 *            the repositoryRoh to set
	 */
	public void setRepositoryRoh(String repositoryRoh) {
		this.repositoryRoh = repositoryRoh;
	}

	/**
	 * @return the repositoryKonv
	 */
	public String getRepositoryKonv() {
		return repositoryKonv;
	}

	/**
	 * @param repositoryKonv
	 *            the repositoryKonv to set
	 */
	public void setRepositoryKonv(String repositoryKonv) {
		this.repositoryKonv = repositoryKonv;
	}

	/**
	 * @return the archiveWorkspace
	 */
	public String getArchiveWorkspace() {
		return archiveWorkspace;
	}

	/**
	 * @param archiveWorkspace
	 *            the archiveWorkspace to set
	 */
	public void setArchiveWorkspace(String archiveWorkspace) {
		this.archiveWorkspace = archiveWorkspace;
	}

	/**
	 * @return the materialWorkspace
	 */
	public String getMaterialWorkspace() {
		return materialWorkspace;
	}

	/**
	 * @param materialWorkspace
	 *            the materialWorkspace to set
	 */
	public void setMaterialWorkspace(String materialWorkspace) {
		this.materialWorkspace = materialWorkspace;
	}

	/**
	 * @return the fakeLogIn
	 */
	public String getFakeLogIn() {
		return fakeLogIn;
	}

	/**
	 * @param fakeLogIn
	 *            the fakeLogIn to set
	 */
	public void setFakeLogIn(String fakeLogIn) {
		this.fakeLogIn = fakeLogIn;
	}

	/**
	 * @return the apiURL
	 */
	public String getApiURL() {
		return apiURL;
	}

	/**
	 * @param apiURL
	 *            the apiURL to set
	 */
	public void setApiURL(String apiURL) {
		this.apiURL = apiURL;
	}

	/**
	 * @return the reporterAddress
	 */
	public String getReporterAddress() {
		return reporterAddress;
	}

	/**
	 * @param reporterAddress
	 *            the reporterAddress to set
	 */
	public void setReporterAddress(String reporterAddress) {
		this.reporterAddress = reporterAddress;
	}

	/**
	 * @return the adminAddress
	 */
	public String getAdminAddress() {
		return adminAddress;
	}

	/**
	 * @param adminAddress
	 *            the adminAddress to set
	 */
	public void setAdminAddress(String adminAddress) {
		this.adminAddress = adminAddress;
	}

	/**
	 * @return the reporterMessageTemplate
	 */
	public String getReporterMessageTemplate() {
		return reporterMessageTemplate;
	}

	/**
	 * @param reporterMessageTemplate
	 *            the reporterMessageTemplate to set
	 */
	public void setReporterMessageTemplate(String reporterMessageTemplate) {
		this.reporterMessageTemplate = reporterMessageTemplate;
	}

	private String getAdminSubject(ConverterMessage msg, String reportSubject) {
		if (msg.isUntoleratedError())
			return "[CRITICAL-ERROR][" + msg.getmaterialUUID()
					+ "] End-products cannot be created.";
		else
			return reportSubject;
	}

	private String getAdminContent(ConverterMessage msg, String reportContent) {

		String uuidPath = UUIDGenerator.getPathFromUUID(msg.getmaterialUUID());
		if (msg.isUntoleratedError())
			return StandardMessage.getMailCriticalErrorContent(
					msg.getmaterialUUID(), repositoryRoh + archiveWorkspace
							+ "/" + uuidPath, repositoryKonv
							+ materialWorkspace + "/" + uuidPath);
		if (reportContent.equals(""))
			return StandardMessage.getMailSuccessfulContent(
					msg.getmaterialUUID(), repositoryKonv + materialWorkspace
							+ "/" + uuidPath);
		else
			return StandardMessage.getMailErrorContent(msg.getmaterialUUID(),
					repositoryKonv + materialWorkspace + "/" + uuidPath,
					reportContent);
	}

	/**
	 * main process
	 */
	@Override
	public void processInOut(MessageExchange exchange, NormalizedMessage in,
			NormalizedMessage out) {
		ConverterMessage msg = new ConverterMessage("reporter-message");
		logger.info("Sending report ...");
		try {
			msg.loadNodeInfo(in);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			updateDatabase(msg);
		} catch (Exception e) {
			logger.error("Error by updating database: " + e.getMessage());
		}
		try {
			this.sendReportToAdmin(msg);
			this.sendReportToUploader(msg);
		} catch (NumberFormatException e) {
			logger.error("Error by sending email" + e.getMessage());
		} catch (MessagingException e) {
			logger.error("Error by sending email" + e.getMessage());
		} catch (IOException e) {
			logger.error("Template for email cannot be found " + e.getMessage());
		}
		logger.info("Removing temporary folder ...");
		try {
			FileUtils.deleteDirectory(new File(msg.getLocalFolder()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		msg.setServiceUnit("reporter");
		try {
			exchange.getMessage("out").setContent(
					new DOMSource(msg.getMessage()));
		} catch (javax.jbi.messaging.MessagingException e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * inform the authors that their material is published
	 * @param msg
	 * @throws NumberFormatException
	 * @throws MessagingException
	 * @throws IOException
	 */
	private void sendReportToUploader(ConverterMessage msg)
			throws NumberFormatException, MessagingException, IOException {
		String[] emails = msg.getUploaderEmails().split(";");
		if (emails[0].equals("")) {
			emails[0] = adminAddress;
		}
		
		File xmlTemplateFile = new File(reporterMessageTemplate);
		Element mainNode = FileExtractor.importXMLFile(xmlTemplateFile);
		RestClient client = new RestClient(fakeLogIn, apiURL);
		

		String materialName = client.queryDB(msg.getmaterialUUID(), "name");
		String reportSubject = mainNode.getChildText("subject").replace("[Material_Name]", materialName);
		String reportContent = mainNode.getChildText("content").replace("[Material_Name]", materialName);
		
		for (String email : emails)
			this.sendMail(
					reportSubject,					
					reportContent.replace("[new_line]", HTML_BREAK),
					email);
	}
	
	private void sendReportToAdmin(ConverterMessage msg)
			throws NumberFormatException, MessagingException {
		Report report = new Report();
		String subject = "";
		String content = "";
		try {
			report.generateReport(new File(msg.getUploadFolder()),
					MaterialType.toMaterialType(msg.getMimeType()),
					msg.getmaterialUUID());
		} catch (Exception e) {
			subject = "[CRITICAL-ERROR][" + msg.getmaterialUUID() + "]";
			content = StandardMessage.getMailCriticalErrorContent(
					msg.getmaterialUUID(), archiveWorkspace, materialWorkspace);
		}
		subject = this.getAdminSubject(msg, report.getSubject());
		content = this.getAdminContent(msg, report.getContent());
		this.sendMail(subject, content, adminAddress);
	}
	
	/**
	 * publish the material after conversion
	 * @param msg
	 */
	private void updateDatabase(ConverterMessage msg) {
		logger.info("Updating database");
		String uuid = msg.getmaterialUUID();
	
		RestClient client = new RestClient(fakeLogIn, apiURL);

		client.updateDB(uuid, "open", "true");
		if (client.queryDB(uuid, "open").contains("true"))
			logger.info("Material is published");
		else {
			logger.error("Material is not published");
			client.updateDB(uuid, "open", "true");
		}
		client.updateDB(uuid, "characteristic",
				MaterialType.getMaterialTypeCode(msg.getMimeType()));
		logger.info("Characteristic: "
				+ MaterialType.getMaterialTypeCode(msg.getMimeType()));
	}

	/**
	 * send email
	 * 
	 * @param subject
	 * @param content
	 * @throws NumberFormatException
	 * @throws MessagingException
	 */
	private void sendMail(String subject, String content, String receiverAddress)
			throws NumberFormatException, MessagingException {
		Properties props = new Properties();

		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.host", smtpHostName);
		props.put("mail.smtp.auth", "false");

		props.put("mail.smtp.timeout", "60000");
		props.put("mail.smtp.connectiontimeout", "60000");

		Session mailSession = Session.getDefaultInstance(props);
		mailSession.setDebug(true);
		Transport transport = mailSession.getTransport();

		MimeMessage message = new MimeMessage(mailSession);
		 Address[] address = new InternetAddress[1];
		 address[0] = new InternetAddress(reporterAddress);
		 message.addFrom(address);
		message.setSubject(subject);
		message.setContent(content, "text/html;charset=UTF-8");

		message.addRecipient(Message.RecipientType.TO, new InternetAddress(
				receiverAddress));

		transport.connect(smtpHostName, Integer.parseInt(smtpHostPort),
				reporterAddress, "");

		transport.sendMessage(message,
				message.getRecipients(Message.RecipientType.TO));
		transport.close();
	}
	
	
}
