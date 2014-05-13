package de.tu_darmstadt.elc.olw.api.misc.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Deque;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Vector;
import java.util.zip.Adler32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.commons.compress.archivers.zip.UnrecognizedExtraField;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipLong;
import org.apache.commons.compress.archivers.zip.ZipShort;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import de.tu_darmstadt.elc.olw.api.misc.execution.Executer;
import de.tu_darmstadt.elc.olw.api.misc.execution.ExecutionException;


public class FileExtractor {
	public static final int BUFFER = 2048;

	/**
	 * returns the extension of the given file
	 * 
	 * @param fileName
	 * @return
	 */

	public static String getFileExtension(String fileName) {
		int extPos = fileName.lastIndexOf(".");
		return fileName.substring(extPos + 1);
	}

	/**
	 * returns the name of the file without the extension
	 * 
	 * @param fileName
	 * @return
	 */

	public static String getFileName(String fileName) {
		int extPos = fileName.lastIndexOf(".");
		return fileName.substring(0, extPos);
	}

	public static String removeSpace(String fileName) {
		return fileName.replace(' ', '_');
	}

	public static String getDateTime() {
		DateFormat dateFormat = new SimpleDateFormat("HHmmddMMyyyy");
		Date date = new Date();
		return dateFormat.format(date);
	}

	/**
	 * compresses the given zip folder into the zip file
	 * 
	 * @param destDir
	 *            input
	 * @param zipFile
	 *            output
	 * @throws IOException
	 */
	public static void zipFile(File destDir, File zipFile) throws IOException {

		URI base = destDir.toURI();
		Deque<File> queue = new LinkedList<File>();
		queue.push(destDir);
		BufferedInputStream origin = null;
		FileOutputStream fos = new FileOutputStream(zipFile);
		ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(fos));
		// out.setMethod(ZipOutputStream.DEFLATED);
		byte data[] = new byte[BUFFER];
		// get a list of files from current directory

		while (!queue.isEmpty()) {
			File dirEntry = queue.pop();
			for (File fileEntry : dirEntry.listFiles()) {
				String name = base.relativize(fileEntry.toURI()).getPath();
				if (!fileEntry.isDirectory()) {
					System.out.println("Adding: " + fileEntry);
					FileInputStream fi = new FileInputStream(fileEntry);
					origin = new BufferedInputStream(fi, BUFFER);
					out.putNextEntry(new ZipEntry(name));
					int count;
					while ((count = origin.read(data, 0, BUFFER)) != -1) {
						out.write(data, 0, count);
					}
					origin.close();
				} else { // directory
					queue.push(fileEntry);
					name = name.endsWith("/") ? name : name + "/";
					out.putNextEntry(new ZipEntry(name));
				}

			}
		}
		out.close();
	}

	/**
	 * decompresses the zip file
	 * 
	 * @param zipFile
	 * @param destDir
	 * @throws ZipException
	 * @throws IOException
	 */
	public static void unzip(File zipFile, File destDir) throws ZipException,
			IOException {
		@SuppressWarnings("resource")
		ZipFile zFile = new ZipFile(zipFile);
		Enumeration<? extends ZipEntry> entries = zFile.entries();
		while (entries.hasMoreElements()) {
			ZipEntry entry = entries.nextElement();
			
			File file = new File(destDir, entry.getName().replace(' ','_'));
			
			if (entry.isDirectory()) {
				file.mkdirs();
			} else {
				file.getParentFile().mkdirs();
				InputStream in = zFile.getInputStream(entry);
				try {
//					System.out.println("Decompress: " + entry.getName());
					@SuppressWarnings("resource")
					OutputStream out = new FileOutputStream(file);
					int count;
					byte[] buffer = new byte[BUFFER];
					while ((count = in.read(buffer)) != -1) {
						out.write(buffer, 0, count);
					}

				} finally {
					in.close();
				}
			}
		}

	}

	public static byte[] getBytesFromStrean(InputStream is, long length,
			String name) throws IOException {

		// Create the byte array to hold the data
		byte[] bytes = new byte[(int) length];

		// Read in the bytes
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length
				&& (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}
		// Ensure all the bytes have been read in
		if (offset < bytes.length) {
			throw new IOException("Could not completely read from stream "
					+ name);
		}

		// Close the input stream and return bytes
		is.close();
		return bytes;
	}

	/**
	 * zip the folder with adler32 checksum for fzip
	 * 
	 * @param destDir
	 * @param zipFile
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static void zipWithChecksum(File destDir, File zipFile)
			throws IOException {
		File tempZip = new File(destDir.getParentFile(), "temp.zip");
		zipFile(destDir, tempZip);

		ZipFile zippedIn = new ZipFile(tempZip);
		ZipArchiveOutputStream zippedOut = new ZipArchiveOutputStream(zipFile);
		zippedOut.setMethod(ZipArchiveOutputStream.DEFLATED);
		Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) zippedIn
				.entries();
		Adler32 adlerChecksum = new Adler32();
		while (entries.hasMoreElements()) {
			ZipArchiveEntry entry = new ZipArchiveEntry(entries.nextElement());
			InputStream input = zippedIn.getInputStream(entry);
			byte[] content = getBytesFromStrean(input, entry.getSize(),
					entry.getName());
			adlerChecksum.reset();
			// checksum is calculated on the uncompressed bytes of the file in
			// question
			adlerChecksum.update(content);

			// adds the adler32 checksum to the zip file
			UnrecognizedExtraField adlerField = new UnrecognizedExtraField();
			adlerField.setHeaderId(new ZipShort(0xdada));
			adlerField.setLocalFileDataData(new ZipLong(adlerChecksum
					.getValue()).getBytes());
			entry.addExtraField(adlerField);

			zippedOut.putArchiveEntry(entry);
			zippedOut.write(content);
			zippedOut.closeArchiveEntry();
		}
		zippedIn.close();
		zippedOut.close();
		System.out.println("Converted " + tempZip + " to " + zipFile);
	}

	/**
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static Element importXMLFile(File file) throws IOException {

		FileInputStream inFile = new FileInputStream(file);
		BufferedInputStream buffer = new BufferedInputStream(inFile);
		SAXBuilder inputStream = new SAXBuilder();
		// inputStream.setIgnoringBoundaryWhitespace(true);

		try {
			Document doc = inputStream.build(buffer);
			Element root = doc.getRootElement();
			Element copyRoot = (Element) root.clone();
			return copyRoot;
		} catch (JDOMException e) {
			throw new IOException(e);
		}

	}

	public static void writeXML(File xmlFile, Document doc) {

		try {
			XMLOutputter serializer = new XMLOutputter();
			FileWriter writer = new FileWriter(xmlFile);
			Format format = serializer.getFormat();
			format = Format.getPrettyFormat();
			serializer.setFormat(format);
			serializer.output(doc, writer);
		} catch (IOException e) {

		}
	}

	public static void writeXMLwithEncoding(File xmlFile, Document doc,
			String encoding) {
		try {
			XMLOutputter serializer = new XMLOutputter();

			FileWriter writer = new FileWriter(xmlFile);
			Format format = serializer.getFormat();
			format = Format.getPrettyFormat();
			format.setEncoding(encoding);
			serializer.setFormat(format);
			serializer.output(doc, writer);
		} catch (IOException e) {

		}
	}

	/**
	 * searches the file in the given folder with the specified suffix
	 * 
	 * @param folder
	 * @param suffix
	 * @return
	 */
	public static File findFileWithSuffix(File folder, String suffix) {
		if (folder != null & folder.isDirectory()) {
			File[] listFile = folder.listFiles();
			for (int i = 0; i < listFile.length; i++) {
				String fileName = listFile[i].getName().toLowerCase();
				if (fileName.endsWith(suffix))
					return listFile[i];
			}
		}
		return null;

	}

	public static HashMap<String, Integer> loadStartTime(String EVQFileName)
			throws IOException {

		HashMap<String, Integer> startTimeTable = new HashMap<String, Integer>();

		FileReader fr = new FileReader(EVQFileName);
		@SuppressWarnings("resource")
		BufferedReader in = new BufferedReader(fr);
		int id = 1;
		String line;

		// Zeile einlesen
		while ((line = in.readLine()) != null) {
			String startTime = line.split(" ")[0]; // first element
			startTimeTable.put(startTime, id);
			id++;
		}
		return startTimeTable;
	}

	/**
	 * copy file
	 * 
	 * @param src
	 * @param dst
	 * @throws IOException
	 */
	public static void copy(File src, File dst) throws IOException {
		InputStream in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dst);
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}

	public static String getDuration(File mediaFile) throws ExecutionException {
		String duration = "";
		String line = "/opt/olw/olw-ffmpeg/bin/ffmpeg -i "
				+ mediaFile.getAbsolutePath();
		ByteArrayInputStream stream = Executer.executeInfo(line);
		Vector<Integer> buffer = new Vector<Integer>();
		int letter = 0;
		while ((letter = stream.read()) != -1)
			buffer.add(letter);
		for (int i = 0; i < buffer.size(); i++)
			if (buffer.get(i) == ((int) 'D')) {

				String token = "";
				int j = i;
				while (buffer.get(j) != ((int) ' ')) {
					token += (char) buffer.get(j).intValue();
					j++;
				}

				if (token.contains("Duration")) {
					j++;
					while (buffer.get(j) != ((int) ',')) {
						duration += (char) buffer.get(j).intValue();
						j++;
					}
					return duration;
				}

			}

		return duration;

	}

}
