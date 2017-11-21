package sgi.javaMacros.os.windows;
//

//import static sgi.javaMacros.os.windows.ApplicationsRetriever.Psapi.EnumProcesses;
//import static sgi.javaMacros.os.windows.ApplicationsRetriever.Psapi.GetModuleBaseNameW;
//import static sgi.javaMacros.os.windows.ApplicationsRetriever.Psapi.GetModuleFileNameExA;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;

import sgi.javaMacros.model.StringUtils;
import sgi.javaMacros.model.interfaces.Strings;
import sgi.javaMacros.model.internal.Application;
import sgi.javaMacros.model.lists.ApplicationSet;

public class ApplicationsRetriever {
	public static final int PROCESS_QUERY_INFORMATION = 0x0400;
	public static final int PROCESS_VM_READ = 0x0010;
	public static final int PROCESS_TERMINATE = 0x0001;
	public static final int PROCESS_VM_OPERATION = 0x0008;
	public static final int PROCESS_VM_WRITE = 0x0020;

	public static final int MEM_COMMIT = 0x1000;
	public static final int MEM_RELEASE = 0x8000;
	public static final int PAGE_READWRITE = 0x0004;

	private static final int MAX_TITLE_LENGTH = 1024;
	static String lastWindowProcessName;

	static {
		lastWindowProcessName = Strings.NULL_STRING; // $NON-NLS-1$
	}

	public static ProcessInfo isOpen(int pid) {
		int[] idBuffer = getAllProcessIds();
		ProcessInfo pinfo = null;
		for (int i = 0; pinfo == null && i < idBuffer.length; i++) {
			int PID = idBuffer[i];
			String[] pName = getProcessNameAndPath(PID);
			if (PID == pid) {
				pinfo = new ProcessInfo(pName[0], pName[1], PID);
			}
		}
		return pinfo;
	}

	public static int[] getAllProcessIds() {
		int bflen = 9192;
		int[] idBuffer = new int[bflen];
		IntByReference pBytesReturned = new IntByReference();
		Psapi.INSTANCE.EnumProcesses(idBuffer, bflen, pBytesReturned);
		int i = idBuffer.length - 1;
		for (; i > 0 && idBuffer[i] == 0; i--)
			;
		int[] out = new int[i];

		System.arraycopy(idBuffer, 0, out, 0, i);
		return out;
	}

	public static ProcessInfo[] getProcessesInfo(String appname) {

		int[] idBuffer = getAllProcessIds();

		ProcessInfo[] buffer = new ProcessInfo[idBuffer.length];

		int i = 0, j = 0;
		for (; i < idBuffer.length; i++) {
			int processID = idBuffer[i];
			String[] pName = getProcessNameAndPath(processID);

			if (appname == null) {
				if (!pName.equals(Strings.NULL_STRING)) { // $NON-NLS-1$
					j = _____upd(buffer, j, processID, pName[0], pName[1]);
				}
			} else {
				if (pName[0].equalsIgnoreCase(appname)) {
					j = _____upd(buffer, j, processID, pName[0], pName[1]);
				}
			}
		}
		ProcessInfo[] outval = new ProcessInfo[j];
		System.arraycopy(buffer, 0, outval, 0, j);
		return outval;
	}

	private static int _____upd(ProcessInfo[] buffer, int j, int processID, String pName, String path) {
		buffer[j] = new ProcessInfo(pName, path, processID);
		return j + 1;
	}

	public static List<String> getAllWindowNames() {
		final List<String> windowNames = new ArrayList<String>();
		final User32 user32 = User32.INSTANCE;
		user32.EnumWindows(new User32.WNDENUMPROC() {

			@Override
			public boolean callback(HWND hWnd, Pointer arg) {
				char[] windowText = new char[512];
				user32.GetWindowText(hWnd, windowText, 512);
				String wText = Native.toString(windowText).trim();
				if (!wText.isEmpty()) {
					windowNames.add(wText);
				}
				return true;
			}

		}, null);

		return windowNames;
	}

	@SuppressWarnings("deprecation")
	public static ApplicationSet getAllWindows() {
		// final List<Application> allWindows = new ArrayList<Application>();
		final User32 user32 = User32.INSTANCE;
		final TreeMap<String, Application> noDoubles = new TreeMap<>();

		user32.EnumWindows(new User32.WNDENUMPROC() {

			@Override
			public boolean callback(HWND fgw, Pointer arg) {
				char[] windowText = new char[512];
				// user32.GetWindowText(fgw, windowText, 512);
				// String wText = Native.toString(windowText).trim();

				if (User32.INSTANCE.IsWindowVisible(fgw)) {
					user32.GetClassName(fgw, windowText, 512);
					String wClass = Native.toString(windowText).trim();

					IntByReference pointer = new IntByReference();

					User32.INSTANCE.GetWindowThreadProcessId(fgw, pointer);

					if (pointer != null && pointer.getValue() > 0) {

						String[] processName = getProcessNameAndPath(pointer.getValue());
						String name = StringUtils.decamelizeExe(processName[0], true);
						Application app = noDoubles.get(name);
						if (app == null) {

							noDoubles.put(name, app = new Application());
						}

						app.getWindowClasses().add(wClass);
						app.setName(name);
						String exeFile = processName[1];
						app.setExeFile(exeFile);
						// allWindows.add(app);
					}
				}
				return true;
			}

		}, null);
		ApplicationSet applications = new ApplicationSet();
		Collection<Application> vals = noDoubles.values();
		for (Iterator<Application> iterator = vals.iterator(); iterator.hasNext();) {
			applications.add(iterator.next());
		}
		return applications;

	}

	public static String getActiveWindowProcessName() {
		HWND fgw = User32.INSTANCE.GetForegroundWindow();

		IntByReference pointer = new IntByReference();

		User32.INSTANCE.GetWindowThreadProcessId(fgw, pointer);

		if (pointer != null && pointer.getValue() > 0) {
			String[] processName = getProcessNameAndPath(pointer.getValue());
			lastWindowProcessName = processName[0];
		}
		Kernel32.INSTANCE.CloseHandle(fgw);

		return lastWindowProcessName;
	}

	public static String[] getProcessNameAndPath(int PID) {
		String[] rv = new String[2];

		byte[] buffer = new byte[MAX_TITLE_LENGTH * 2];
		HANDLE process = Kernel32.INSTANCE.OpenProcess(PROCESS_QUERY_INFORMATION | PROCESS_VM_READ, false, PID);

		if (Psapi.INSTANCE.GetModuleFileNameExA(process, null, buffer, MAX_TITLE_LENGTH) != 0) {
			// System.out.println("GetModuleFileNameExW failed");
			// System.out.println("Native Error: "+Native.getLastError());
		}
		String string = Native.toString(buffer);

		rv[1] = string;

		char[] buffer2 = new char[MAX_TITLE_LENGTH];
		if (Psapi.INSTANCE.GetModuleBaseNameW(process, null, buffer2, MAX_TITLE_LENGTH) != 0) {
			// Debuggable.debug("Error: "+GetLastError());
		}
		rv[0] = Native.toString(buffer2);

		Kernel32.INSTANCE.CloseHandle(process);
		return rv;
	}

	public static boolean killProcess(int pid) {
		HANDLE processHandle = Kernel32.INSTANCE.OpenProcess(Kernel32.PROCESS_TERMINATE, false, (int) pid);
		boolean b = Kernel32.INSTANCE.TerminateProcess(processHandle, 0);
		Kernel32.INSTANCE.CloseHandle(processHandle);
		return b;
	}

	public static ProcessInfo[] getProcessesInfo() {
		return getProcessesInfo(null);
	}
}