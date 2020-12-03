package com.madana.common.utils;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.LogOutputStream;
import org.apache.commons.exec.PumpStreamHandler;
public class ProcessExecutor {
	private Executor executor;
	public ProcessExecutor() {
		super();
		executor = new DefaultExecutor();
	}
	public ProcessExecutor(String workdir) {
		super();
		executor = new DefaultExecutor();
		executor.setWorkingDirectory(new File(workdir));
	}
	public int executeCommand(final String command) throws ExecuteException, IOException {
		int exitValue = -1;
		// create command line without any arguments
		final CommandLine commandLine =new CommandLine("bash");
		commandLine.addArgument("-c");
		commandLine.addArgument(command,false);

		// create process watchdog with timeout 60000 milliseconds
		ExecuteWatchdog watchdog = new ExecuteWatchdog(60000);
		// set watchdog and starting synchronous child process
		executor.setWatchdog(watchdog);
		exitValue = executor.execute(commandLine);
		return exitValue;
	}
	public String executeCommandWithOutput(final String command) throws ExecuteException, IOException {
		int exitValue = -1;
		// create command line without any arguments
		final CommandLine commandLine =new CommandLine("bash");
		commandLine.addArgument("-c");
		commandLine.addArgument(command,false);
		// create process watchdog with timeout 15min
		ExecuteWatchdog watchdog = new ExecuteWatchdog(900000);
		// set watchdog and starting synchronous child process
		executor.setWatchdog(watchdog);
		// create string output for executor and add as pump handler
		ProcessStringOutput processOutput = new ProcessStringOutput(0);
		ByteArrayOutputStream stderr = new ByteArrayOutputStream();
		executor.setStreamHandler(new PumpStreamHandler(processOutput,stderr));
		try
		{
			exitValue = executor.execute(commandLine);

		}
		catch(ExecuteException ex)
		{
			throw new ExecuteException(stderr.toString(), exitValue);
		}
		//		if (!executor.isFailure(exitValue)) {
		//			return processOutput.getOutput();
		//		}
		return processOutput.getOutput();
	}
	public String executeCommandWithOutput(final String command, final String dir) throws ExecuteException, IOException {
		return executeCommandWithOutput(command,dir,false);
	}
	public String executeCommandWithOutput(final String command, final String dir, boolean ignoreError) throws ExecuteException, IOException {
		int exitValue = -1;
		executor = new DefaultExecutor();
		executor.setWorkingDirectory(new File(dir));
		// create command line without any arguments
		final CommandLine commandLine =new CommandLine("bash");
		commandLine.addArgument("-c");
		commandLine.addArgument(command,false);
		// create process watchdog with timeout 60000 milliseconds
		ExecuteWatchdog watchdog = new ExecuteWatchdog(60000);
		// set watchdog and starting synchronous child process
		executor.setWatchdog(watchdog);
		// create string output for executor and add as pump handler
		ProcessStringOutput processOutput = new ProcessStringOutput(0);
		ByteArrayOutputStream stderr = new ByteArrayOutputStream();
		executor.setStreamHandler(new PumpStreamHandler(processOutput,stderr));
		try
		{
			exitValue = executor.execute(commandLine);

		}
		catch(ExecuteException ex)
		{
			if(!ignoreError)
				throw new ExecuteException(stderr.toString(), exitValue);
		}
		//		if (!executor.isFailure(exitValue)) {
		//			return processOutput.getOutput();
		//		}
		return processOutput.getOutput();
	}

	class ProcessStringOutput extends LogOutputStream {
		private StringBuilder processOutput;
		public ProcessStringOutput(final int level) {
			super(level);
			this.processOutput = new StringBuilder();
		}
		@Override
		protected void processLine(String line, int logLevel) {
			processOutput.append(line);
			processOutput.append("\n");
		}
		public String getOutput() {
			return processOutput.toString();
		}
	}
}