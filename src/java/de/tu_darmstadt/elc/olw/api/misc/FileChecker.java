package de.tu_darmstadt.elc.olw.api.misc;

import java.io.File;
import java.io.FileNotFoundException;

public class FileChecker {
	
	public static  boolean isFileCompleted(File inputFile) {
		if (inputFile.exists() && inputFile.length() > 1)
			return true;
		return false;
	}
	
	public static boolean isFolderCompleted(File inputFolder) {
		if (!inputFolder.exists() || inputFolder.length() <= 0)
			return false;
		File[] listFile = inputFolder.listFiles();
		for (File file : listFile)
			if (!isFileCompleted(file))
				return false;
		return true;
	}
	
	public static void checkFileExist(File inputFile) throws FileNotFoundException {
		if (inputFile == null)
			throw new FileNotFoundException();
	}
}
