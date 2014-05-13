package de.tu_darmstadt.elc.olw.api.misc.execution;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteStreamHandler;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.log4j.Logger;

public class Executer {
	private static Logger logger = Logger.getLogger(Executer.class);
	/**
	 * executes the command line
	 * 
	 * @param line
	 * @throws ExecutionException
	 */
	public static void execute(String line) throws ExecutionException {
		logger.info("Command: " + line);
		ExecuteStreamHandler streamHandler = new PumpStreamHandler(System.out,
				System.err, System.in);

		CommandLine commandLine = CommandLine.parse(line);
		DefaultExecutor executor = new DefaultExecutor();

		executor.setStreamHandler(streamHandler);

		// Execute application
		try {

			executor.setExitValues(new int[] { 0, 1 });
			executor.execute(commandLine);

		} catch (IOException e) {
			logger.error("Errors by execution");
			throw new ExecutionException("Errors by execution");
		}
	}

	/**
	 * executes the command line, and redirects the output into a log file
	 * 
	 * @param line
	 * @param logFile
	 * @throws ExecutionException
	 */
	public static void execute(String line, File logFile)
			throws ExecutionException {
		if (logFile == null) {
			Executer.execute(line);
			return;
		}
		logger.info("Command: " + line);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(logFile, true);
		} catch (FileNotFoundException e1) {
			throw new ExecutionException("Log file not found");
		}

		CommandLine commandLine = CommandLine.parse(line);

		DefaultExecutor executor = new DefaultExecutor();
		PumpStreamHandler psh = new PumpStreamHandler(fos);

		executor.setStreamHandler(psh);

		// Execute application
		try {
			executor.setExitValue(0);
			executor.execute(commandLine);
			fos.close();

		}

		catch (ExecuteException e) {
			logger.error("Errors by execution");
			throw new ExecutionException("Errors by execution");
		} catch (IOException e) {
			logger.error("Errors by execution");
			throw new ExecutionException("Errors by execution");
		}
	}

	public static ByteArrayInputStream executeInfo(String line)
			throws ExecutionException {
		logger.info("Command: " + line);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		CommandLine commandLine = CommandLine.parse(line);

		DefaultExecutor executor = new DefaultExecutor();
		PumpStreamHandler psh = new PumpStreamHandler(baos);

		executor.setStreamHandler(psh);

		// Execute application
		try {
			executor.setExitValues(new int[] { 0, 1 });
			executor.execute(commandLine);
			ByteArrayInputStream bais = new ByteArrayInputStream(
					baos.toByteArray());
			baos.close();
			return bais;

		} catch (ExecuteException e) {
			logger.error("Errors by execution");
			throw new ExecutionException("Errors by execution");
		} catch (IOException e) {
			logger.error("Errors by execution");
			throw new ExecutionException("Errors by execution");
		}
	}

}
