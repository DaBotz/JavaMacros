package sgi.javaMacros.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import sgi.javaMacros.os.windows.ProcessInfo;
import sgi.javaMacros.os.windows.WindowsInterface;

public class ExternalProcess {

	private static final int NO_PID = -1;
	private static final long SHORT_DELAY = 3000;
	private static final long DELAY = 8000;
	private static String localSemaphore = ""; //$NON-NLS-1$

	private String processName;
	private boolean running;
	private long pid = NO_PID;
	private long lastActive;
	private boolean k32Enabled = true;
	private Process spawnedProcess;

	public ExternalProcess(String string) {
		this.processName = string;
	}

	public synchronized void check() {
		if (spawnedProcess != null) {
			try {
				spawnedProcess.hashCode();
				spawnedProcess.exitValue();
			} catch (IllegalThreadStateException e) {
				this.running = true;
				System.out.println("fast look"); //$NON-NLS-1$

				return;
			} catch (Throwable e) {
				e.printStackTrace();
			}

		}
		long currentTimeMillis = System.currentTimeMillis();
		if (currentTimeMillis - lastActive < (running ? DELAY : SHORT_DELAY))
			return;

		this.running = false;
		pid = NO_PID;

		if (!(k32Enabled && _checkKernel32())) {
			_checkWMIC();
		}
		lastActive = currentTimeMillis;

	}

	private boolean _checkKernel32() {

		ProcessInfo[] pinfo = WindowsInterface.getProcessesInfo(processName);
		if (pinfo.length == 0) {
			System.err.println("checkDirect Failed "); //$NON-NLS-1$
			return false;
		} else if (pinfo.length > 1) {
			System.err.println("????? What the fuck? "); //$NON-NLS-1$
		}
		System.out.println("Find at the first!!!!!"); //$NON-NLS-1$
		this.running = true;
		this.pid = pinfo[0].getPid();

		return true;
	}

	private void _checkWMIC() {
		synchronized (localSemaphore) {
			String line;
			try {
				System.out.println("_check WMIC:"); //$NON-NLS-1$
				Process proc = Runtime.getRuntime().exec("wmic.exe"); //$NON-NLS-1$
				BufferedReader input = new BufferedReader(new InputStreamReader(proc.getInputStream()));
				OutputStreamWriter oStream = new OutputStreamWriter(proc.getOutputStream());
				String com = "process where name='" + processName //$NON-NLS-1$
						+ "' GET ProcessId "; //$NON-NLS-1$
				oStream.write(com);
				// Debuggable.debug(com);

				oStream.flush();
				oStream.close();
				while (!running && (line = input.readLine()) != null) {
					System.out.println(line);
					try {
						pid = Long.parseLong(line.trim());
						running = true;
					} catch (NumberFormatException e) {
					}
				}
				input.close();
			} catch (Throwable ioe) {
				ioe.printStackTrace();
			}
		}
	}

	public String[] parseLine(String line) {
		int state = 0;
		ArrayList<String> list = new ArrayList<String>();
		StringBuffer b = new StringBuffer();

		final int SPAZI = 0;
		final int WORD = 1;
		final int VIRGOLETTATO = 2;

		for (int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);
			switch (state) {
			case SPAZI:
				if (c == '\"') {
					state = VIRGOLETTATO;
				} else if (!Character.isWhitespace(c)) {
					state = WORD;
					b.append(c);
				}
				break;
			case WORD:
				if (Character.isWhitespace(c)) {
					list.add(b.toString());
					b = new StringBuffer();
					state = SPAZI;
				} else
					b.append(c);
				break;

			case VIRGOLETTATO:
				if (c == '\"') {
					list.add(b.toString());
					b = new StringBuffer();
					state = SPAZI;
				} else
					b.append(c);
				break;
			}

		}
		return list.toArray(new String[list.size()]);
	}

	public boolean isRunning() {
		this.check();
		return running;
	}

	public void kill() throws UnkillableProcessException {

		// I have an handle, as the observed app was spawned by this
		if (spawnedProcess != null) {
			spawnedProcess.destroy();
			WindowsInterface.killProcess((int) pid);
			spawnedProcess = null;
			check();
			System.out.println("It was a spawn: killed"); //$NON-NLS-1$ //$NON-NLS-2$
			return;
		}
		lastActive = 0;
		running = false;

		if (pid == NO_PID)
			check();

		if (pid > 0) {
			if (!WindowsInterface.killProcess((int) pid)) {
				k32Enabled = false;
				System.err.println("Can't kill damn " + processName + "  pid " + pid); //$NON-NLS-1$ //$NON-NLS-2$
				check();
				if (WindowsInterface.killProcess((int) pid))
					System.out.println("I could kill damn " + processName + ", with  pid " //$NON-NLS-1$ //$NON-NLS-2$
							+ pid);
				else {
					return;
					// throw new UnkillableProcessException( processName , pid);
				}
			} else {
				System.out.println("I well killed " + processName + ", with  pid " //$NON-NLS-1$ //$NON-NLS-2$
						+ pid);
			}
		}

		lastActive = 0;
		running = false;
		pid = NO_PID;
	}

	public String capsulize(String absPath) {
		String command = " \"" + absPath + "\"";
		return command;
	}

	public Process launchExe(File exe) throws IOException {
		String absolutePath = exe.getPath();
		return launchApp(absolutePath);
	}

	public Process launchApp(String... absolutePath) throws IOException {
		ProcessBuilder pb = new ProcessBuilder(absolutePath);
		spawnedProcess = pb.start();
		return spawnedProcess;
	}

}
