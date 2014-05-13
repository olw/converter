package de.tu_darmstadt.elc.olw.api.webdav;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.Vector;

import org.apache.commons.io.IOUtils;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.log4j.Logger;

import com.googlecode.sardine.DavResource;
import com.googlecode.sardine.Sardine;
import com.googlecode.sardine.SardineFactory;
import com.googlecode.sardine.util.SardineException;

import de.tu_darmstadt.elc.olw.api.misc.UUIDGenerator;

public class WebDAVClient extends Thread {
	private static Logger logger = Logger.getLogger(WebDAVClient.class);
	private final static String PASS_PHRASE = "olw2010";
	private final static String CACERT_PATH = "/opt/olw/conf/olw_material_cacerts";

	private String webDAVServer;
	private String workspaceName;
	private String password;
	private String username;
	private Sardine sardine;
	private SSLSocketFactory sslSocket;

	public String getWebDAVServer() {
		return webDAVServer;
	}

	public void setWebDAVServer(String webDAVServer) {
		this.webDAVServer = webDAVServer;
	}

	public String getWorkspaceName() {
		return workspaceName;
	}

	public void setWorkspaceName(String workspaceName) {
		this.workspaceName = workspaceName;
	}

	public WebDAVClient(String username, String password, String webDAVServer,
			String defaulWorkspace) throws SardineException {
		this.username = username;
		this.password = password;
		this.webDAVServer = webDAVServer;
		this.workspaceName = defaulWorkspace;

		KeyStore ks = null;
		try {
			ks = KeyStore.getInstance(KeyStore.getDefaultType());
		} catch (KeyStoreException e) {
			logger.error(e.getMessage());
		}

		// get user password and file input stream
		char[] keyPhrase = PASS_PHRASE.toCharArray();
		java.io.FileInputStream fis = null;
		try {
			fis = new java.io.FileInputStream(CACERT_PATH);
		} catch (FileNotFoundException e) {
			
			logger.error(e.getMessage());
		}
		try {
			ks.load(fis, keyPhrase);
		} catch (NoSuchAlgorithmException e) {
			
			logger.error(e.getMessage());
		} catch (CertificateException e) {
			
			logger.error(e.getMessage());
		} catch (IOException e) {
			
			logger.error(e.getMessage());
		}
		try {
			fis.close();
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		try {
			sslSocket = new SSLSocketFactory(ks, "olw2010");
		} catch (KeyManagementException e) {
			
			logger.error(e.getMessage());
		} catch (UnrecoverableKeyException e) {
			
			logger.error(e.getMessage());
		} catch (NoSuchAlgorithmException e) {
		
			logger.error(e.getMessage());
		} catch (KeyStoreException e) {
			
			logger.error(e.getMessage());
		}
		sardine = SardineFactory.begin(username, password, sslSocket);
	}

	/**
	 * uploads file to uuid
	 * 
	 * @param file
	 * @param uuid
	 *            23-45-67-ab-...
	 * @param workspace
	 * @throws FileNotFoundException
	 * @throws SardineException
	 */
	public void uploadFile(File file, String uuid)
			throws FileNotFoundException, SardineException {
		
		InputStream fis = null;
		String url = null;

		// Prepar input stream
		fis = new FileInputStream(file);

		// Prepare URL
		url = webDAVServer + this.workspaceName + "/" + uuid.replace("-", "/")
				+ "/";

				sardine = SardineFactory.begin(username, password, sslSocket);
		if (!sardine.exists(url))
			createSubFolderChain(this.workspaceName, uuid);
		url += file.getName();
		sardine = SardineFactory.begin(username, password, sslSocket);
		sardine.put(url, fis);


	}

	public void uploadFolder(File folder, String uuid)
			throws FileNotFoundException, SardineException {
		String url = webDAVServer + this.workspaceName + "/"
				+ uuid.replace("-", "/") + "/";
		sardine = SardineFactory.begin(username, password, sslSocket);
		if (!sardine.exists(url))
			createSubFolderChain(this.workspaceName, uuid);

		File[] files = folder.listFiles();
		for (int i = 0; i < files.length; i++) {

			if (files[i].isDirectory())
				uploadFolder(files[i], uuid + "-" + files[i].getName());
			else
				uploadFile(files[i], uuid);
		}

	}

	/**
	 * 
	 * @param url
	 * @param destDir
	 * @return
	 * @throws IOException
	 */
	public File downloadFile(String fileName, String uuid, File destDir)
			throws IOException {
		String url = webDAVServer + workspaceName + "/"
				+ uuid.replace("-", "/") + "/" + fileName;
		logger.info(" Material url: " + url);
		sardine = SardineFactory.begin(username, password, sslSocket);
		InputStream inputStream = sardine.getInputStream(url);
		
		File resultFile = new File(destDir, fileName);

		OutputStream outputStream = new FileOutputStream(resultFile);
		IOUtils.copy(inputStream, outputStream);
		return resultFile;
	}

	/**
	 * downloads Folder
	 * 
	 * @param uuid
	 * @param destDir
	 * @throws IOException
	 */
	public void downloadFolder(String uuid, File destDir) throws IOException {

		String url = webDAVServer + workspaceName + "/"
				+ uuid.replace("-", "/") + "/";
		sardine = SardineFactory.begin(username, password, sslSocket);
		logger.info("Repository path: " + url);
		List<DavResource> resources = sardine.getResources(url);
		for (int i = 1; i < resources.size(); i++)
			if (!resources.get(i).isCurrentDirectory()) {
				if (resources.get(i).getContentLength() == 0 && !resources.get(i).getName().equals("red5"))
					downloadFolder(uuid + "-" + resources.get(i).getName(),
							destDir);
				else
					downloadFile(resources.get(i).getName(), uuid, destDir);
			}

	}

	public void moveResource(String sourceUUID, String destinationWorkspace)
			throws SardineException {
		String destURL = webDAVServer + destinationWorkspace + "/"
				+ sourceUUID.replace("-", "/") + "/";
		String sourceURL = webDAVServer + workspaceName + "/"
				+ sourceUUID.replace("-", "/") + "/";
		sardine = SardineFactory.begin(username, password, sslSocket);
		if (!sardine.exists(destURL))
			this.createSubFolderChain(destinationWorkspace, sourceUUID);
		List<DavResource> resources = sardine.getResources(sourceURL);
		for (int i = 0; i < resources.size(); i++) {

			if (!resources.get(i).isCurrentDirectory())
				sardine.move(sourceURL + resources.get(i).getName(), destURL
						+ resources.get(i).getName());
		}
		this.removeResource(sourceUUID);

	}

	/**
	 * uploads a stream to a uuid path, with a new file name
	 * 
	 * @param inputStream
	 * @param fileName
	 * @param uuid
	 * @throws SardineException
	 */
	public void uploadStream(InputStream inputStream, String fileName,
			String uuid) throws SardineException {
		String splittedUUID = UUIDGenerator.splitUUID(uuid);

		String url = webDAVServer + this.workspaceName + "/"
				+ splittedUUID.replace("-", "/") + "/";

		sardine = SardineFactory.begin(username, password, sslSocket);
		createSubFolderChain(this.workspaceName, splittedUUID);
		url += fileName;
		
		sardine = SardineFactory.begin(username, password);
		sardine.put(url, inputStream);
	}

	public void createSubFolderChain(String workspaceName, String uuid)
			throws SardineException {
		
		String[] IDs = uuid.split("-");
		String url = webDAVServer + workspaceName + "/";
		for (int i = 0; i < IDs.length; i++) {
			url += IDs[i] + "/";
			this.createSubFolder(url);

		}

	}

	public void createSubFolder(String url) {
		
		try {
			sardine = SardineFactory.begin(username, password, sslSocket);
			sardine.createDirectory(url);
		} catch (SardineException e) {
			
			if (e.getResponsePhrase().contains("Conflict"))
				logger.warn("Directory is already existed.");
			else
				logger.error(e.getMessage());
		}

		

	}

	@SuppressWarnings("unused")
	private int createSubFolderAux(String url, Sardine sardine) {
		try {
			sardine.createDirectory(url);
		} catch (SardineException e) {
			return e.getStatusCode();

		}
		return 0;
	}

	
	public void removeResource(String uuid) throws SardineException {
		String deletedUUID = uuid;
		do {
			removeSubFolder(deletedUUID);
			deletedUUID = UUIDGenerator.getParentUUID(deletedUUID);
		} while (isEmpty(deletedUUID));
	}

	public void removeSubFolder(String uuid) throws SardineException {
		String url = webDAVServer + workspaceName + "/"
				+ uuid.replace("-", "/");
		sardine = SardineFactory.begin(username, password, sslSocket);
		sardine.delete(url);
	}

	public boolean isEmpty(String uuid) throws SardineException {
		String url = webDAVServer + workspaceName + "/"
				+ uuid.replace("-", "/") + "/";
		sardine = SardineFactory.begin(username, password, sslSocket);
		if (!sardine.exists(url))
			return false;
		List<DavResource> resources = sardine.getResources(url);
		if (!uuid.equals(""))
			return (resources.size() < 2); // itself
		return false;
	}

	/**
	 * checks whether the material with given uuid is ready (the upload process
	 * is finished).
	 * 
	 * @param uuid
	 * @return true, if the signal (e.g. "readme.txt") is found
	 * @throws SardineException
	 */
	public boolean isResourceReady(String uuid)
			throws SardineException {
		String url = webDAVServer + workspaceName + "/"
				+ uuid.replace("-", "/") + "/";
		Sardine sardine = SardineFactory.begin(username, password);
		List<DavResource> resources = sardine.getResources(url);
		for (int i = 0; i < resources.size(); i++) {
			logger.info("Name:" +resources.get(i).getName() +     "---Size: " +resources.get(i).getContentLength());
			if (resources.get(i).getContentLength() == 0) 
				return false;
		}
		return true;
	}

	public String getListFile(String uuid) throws SardineException {
		logger.info("Get list file");
		String listFile = "";
		String url = webDAVServer + this.workspaceName + "/"
				+ UUIDGenerator.splitUUID(uuid).replace("-", "/") + "/";

		// Create folder
		Sardine sardine = SardineFactory.begin(username, password, sslSocket);
		if (!sardine.exists(url))
			return null;
		if (this.isEmpty(uuid))
			return null;
		sardine = SardineFactory.begin(username, password, sslSocket);
		List<DavResource> resources = sardine.getResources(url);
		for (int i = 1; i < resources.size() - 1; i++)
			listFile += resources.get(i).getName() + ";";
		listFile += resources.get(resources.size() - 1).getName();
		return listFile;
	}

	public Vector<String> listAll() throws SardineException {
		String url = webDAVServer + workspaceName + "/";
		Sardine sardine = SardineFactory.begin(username, password);
		List<DavResource> resources = sardine.getResources(url);
		Vector<String> rootList = new Vector<String>();
		for (int i = 1; i < resources.size(); i++) {
			DavResource element = resources.get(i);
			
			rootList.add(element.getName());
			
		}
		return listAll(rootList);
	}

	public Vector<String> listAll(Vector<String> rootList)
			throws SardineException {

		Vector<String> listMaterial = new Vector<String>();
		Sardine sardine = SardineFactory.begin(username, password);
		if (rootList.size() == 0)
			return listMaterial;
		String url = "";
		for (String rootUUID : rootList) {

			url = webDAVServer + workspaceName + "/"
					+ rootUUID.replace("-", "/") + "/";
			if (!sardine.exists(url))
				return null;
			List<DavResource> resources = sardine.getResources(url);
			if (resources.size() <= 1)
				listMaterial.add(rootUUID);
			else {
				for (int i = 1; i < resources.size(); i++) {
					// resource is a file

					if (resources.get(i).getContentLength() != 0)
						listMaterial.add(rootUUID);

					else {
						Vector<String> listSubMaterial = new Vector<String>();
						listSubMaterial.add(rootUUID + "-"
								+ resources.get(i).getName());
						Vector<String> subList = listAll(listSubMaterial);
						if (subList.size() == 0)
							listMaterial.add(rootUUID);
						else
							for (String uuid : subList)
								listMaterial.add(uuid);
					}
				}

			}
		}
		return listMaterial;

	}

}
