package de.tu_darmstadt.elc.olw.api.document;


import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;


import de.tu_darmstadt.elc.olw.api.misc.execution.Executer;
import de.tu_darmstadt.elc.olw.api.misc.execution.ExecutionException;


public class PDFConverter {
	private static Logger logger = Logger.getLogger(PDFConverter.class);
	private final static String RESOLUTION = "854x560";	
	private File pdfFile;
	private String pdfBoxPath;

	/**
	 * @param pdfBoxPath
	 */
	public PDFConverter(File pdfFile,String pdfBoxPath) {
		super();
		this.pdfBoxPath = pdfBoxPath;
		this.pdfFile = pdfFile;
	}

	/**
	 * @return the pdfFile
	 */
	public File getPdfFile() {
		return pdfFile;
	}

	/**
	 * @param pdfFile
	 *            the pdfFile to set
	 */
	public void setPdfFile(File pdfFile) {
		this.pdfFile = pdfFile;
	}

	/**
	 * @return the pdfBoxPath
	 */
	public String getPdfBoxPath() {
		return pdfBoxPath;
	}

	/**
	 * @param pdfBoxPath
	 *            the pdfBoxPath to set
	 */
	public void setPdfBoxPath(String pdfBoxPath) {
		this.pdfBoxPath = pdfBoxPath;
	}

	
	/*
	 * http://pdfbox.apache.org/commandline/
	 * PDFToImage: -endPage	Integer.MAX_INT	The last page to convert, one based.
	 * -endPage 1
	 */
	private void exportPDFtoImage(File pdfFile, String outputPrefix, File logFile)
			 {
		String exportCommand = "java -jar " + pdfBoxPath + "pdfbox-app.jar"
				+ " PDFToImage -imageType png -endPage 1 -outputPrefix " + outputPrefix
				+ " " + pdfFile.getAbsolutePath();
		try {
			Executer.execute(exportCommand,logFile);
		} catch (ExecutionException e) {
			logger.error(e.getMessage());
		}
	}

	@SuppressWarnings("rawtypes")
	private int getRotation(File pdfFile) throws IOException {
		PDDocument document = null;
		PDPage page = null;
		List pageList = null;
		document = PDDocument.load(pdfFile.getAbsolutePath());
		pageList = document.getDocumentCatalog().getAllPages();
		page = (PDPage) pageList.get(0);
		document.close();
		return page.findRotation();
	}

	@SuppressWarnings("unused")
	private void rotateImage(File imgFolder, String rotatedAngle)
			throws ExecutionException {
		String rotateCommand = "mogrify -rotate " + rotatedAngle + " "
				+ imgFolder.getAbsolutePath() + "/*.png";
		Executer.execute(rotateCommand);
	}

	private void resizeImage(File imgFolder, String newResolution, File logFile)
			{
		String resizeCommand = "mogrify -resize " + newResolution + " "
				+ imgFolder.getAbsolutePath() + "/*.png";
		try {
			Executer.execute(resizeCommand, logFile);
		} catch (ExecutionException e) {
			logger.error(e.getMessage());
		}
	}

	private void createThumbnail(File imgFolder, File logFile) {
		String thumbCommand = "convert " + imgFolder.getAbsolutePath()
				+ "/slide1.png" + " -resize 152x114" + " "
				+ imgFolder.getParentFile().getAbsolutePath()
				+ "/thumbnail.jpg";
		try {
			Executer.execute(thumbCommand,logFile);
		} catch (ExecutionException e) {
			logger.error(e.getMessage());
		}
	}

	/**
	 * converts PDF to image and resizes
	 * @param outputFolder
	 * @throws ExecutionException
	 * @throws IOException
	 */
	public void convertPDF(File outputFolder, File logFile) throws 
			IOException {
		if (!outputFolder.exists())
			outputFolder.mkdirs();
		
		String outputPrefix = outputFolder.getAbsolutePath() + "/slide";
		exportPDFtoImage(pdfFile, outputPrefix, logFile);
		int rotatedAngle = getRotation(pdfFile);
		logger.info("Rotation: " + rotatedAngle);
		resizeImage(outputFolder, RESOLUTION,logFile);
		createThumbnail(outputFolder,logFile);
	}
	
	

}
