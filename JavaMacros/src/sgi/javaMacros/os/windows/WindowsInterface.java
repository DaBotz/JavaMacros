package sgi.javaMacros.os.windows;

import static sgi.javaMacros.os.windows.WindowsInterface.Kernel32.CloseHandle;
import static sgi.javaMacros.os.windows.WindowsInterface.Kernel32.GetProcessId;
import static sgi.javaMacros.os.windows.WindowsInterface.Kernel32.OpenProcess;
import static sgi.javaMacros.os.windows.WindowsInterface.Kernel32.PROCESS_QUERY_INFORMATION;
import static sgi.javaMacros.os.windows.WindowsInterface.Kernel32.PROCESS_TERMINATE;
import static sgi.javaMacros.os.windows.WindowsInterface.Kernel32.PROCESS_VM_READ;
import static sgi.javaMacros.os.windows.WindowsInterface.Kernel32.TerminateProcess;
import static sgi.javaMacros.os.windows.WindowsInterface.Psapi.EnumProcesses;
import static sgi.javaMacros.os.windows.WindowsInterface.Psapi.GetModuleBaseNameW;
import static sgi.javaMacros.os.windows.WindowsInterface.User32DLL.GetForegroundWindow;
import static sgi.javaMacros.os.windows.WindowsInterface.User32DLL.GetWindowThreadProcessId;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LONGByReference;
import com.sun.jna.platform.win32.WinDef.UINT;
import com.sun.jna.ptr.PointerByReference;

import sgi.javaMacros.model.interfaces.Strings;

public class WindowsInterface {
	static class Kernel32 {
		static {
			Native.register("kernel32"); //$NON-NLS-1$
		}
		public static final int PROCESS_QUERY_INFORMATION = 0x0400;
		public static final int PROCESS_VM_READ = 0x0010;
		public static final int PROCESS_TERMINATE = 0x0001;
		public static final int PROCESS_VM_OPERATION = 0x0008;
		public static final int PROCESS_VM_WRITE = 0x0020;

		public static final int MEM_COMMIT = 0x1000;
		public static final int MEM_RELEASE = 0x8000;
		public static final int PAGE_READWRITE = 0x0004;

		public static native int GetLastError();

		public static native Pointer OpenProcess(int dwDesiredAccess,
				boolean bInheritHandle, Pointer pointer);

		public static native int TerminateProcess(Pointer hProcess,
				UINT uExitCode);

		public static native int CloseHandle(Pointer hObject);

		public static native Pointer VirtualAllocEx(Pointer hProcess,
				int address, int size, int allocationType, int protection);

		public static native boolean VirtualFreeEx(Pointer hProcess,
				Pointer address, int size, int freeType);

		public static native long GetProcessId(Pointer hProcess);

		// public static native boolean WriteProcessMemory(Pointer hProcess,
		// Pointer otherAddress, Pointer localAddress, int size,
		// IntByReference bytesWritten);
		//
		// public static native boolean ReadProcessMemory(Pointer hProcess,
		// Pointer otherAddress, Pointer localAddress, int size,
		// IntByReference bytesRead);
		//
		// public static native boolean ReadProcessMemory(Pointer hProcess,
		// Pointer otherAddress, char[] localAddress, int size,
		// IntByReference bytesRead);

	}

	static class Psapi {
		static {
			Native.register("psapi"); //$NON-NLS-1$
		}

		public static native int EnumProcesses(long[] pProcessIds, long cb,
				LONGByReference pBytesReturned);

		public static native int GetModuleBaseNameW(Pointer hProcess,
				Pointer hmodule, char[] lpBaseName, int size);

	
	}

	static class User32DLL {
		static {
			Native.register("user32"); //$NON-NLS-1$
		}

		public static native HWND GetForegroundWindow();

		public static native int GetWindowTextW(HWND hWnd, char[] lpString,
				int nMaxCount);

		public static native int GetWindowThreadProcessId(HWND hWnd,
				PointerByReference pref);
	}

	private static final int MAX_TITLE_LENGTH = 1024;
	static String lastWindowProcessName;

	static {
		lastWindowProcessName = Strings.NULL_STRING; //$NON-NLS-1$
	}

	public static ProcessInfo isOpen(long pid) {
		long[] idBuffer = getAllProcessIds();
		ProcessInfo pinfo = null;
		for (int i = 0; pinfo == null && i < idBuffer.length; i++) {
			long x = idBuffer[i];
			String pName = getProcessName(new Pointer(x)).toLowerCase();
			if (x == pid) {
				pinfo = new ProcessInfo(pName, x);
			}
		}
		return pinfo;
	}

	public static long[] getAllProcessIds() {
		int bflen = 9192;
		long[] idBuffer = new long[bflen];
		LONGByReference pBytesReturned = new LONGByReference();
		EnumProcesses(idBuffer, bflen, pBytesReturned);
		int i = 0;
		for (; i < idBuffer.length && idBuffer[i]> 0; i++)
			;
		long[] out = new long[i];
		System.arraycopy(idBuffer, 0, out, 0, i);
		return out;
	}

	public static ProcessInfo[] getProcessesInfo(String appname) {
		long[] idBuffer = getAllProcessIds();
		ProcessInfo[] buffer = new ProcessInfo[idBuffer.length];
		int i = 0, j = 0;
		for (; i < idBuffer.length; i++) {
			long x = idBuffer[i];
			Pointer pval = new Pointer(x);
			String pName = getProcessName(pval).trim();

			if (appname == null) {
				if (!pName.equals(Strings.NULL_STRING)) { //$NON-NLS-1$
					j = _____upd(buffer, j, pval, pName);
				}
			}  else {
				if (pName.equalsIgnoreCase(appname)) {
					j = _____upd(buffer, j, pval, pName);
				}
			}
		}
		ProcessInfo[] outval = new ProcessInfo[j];
		System.arraycopy(buffer, 0, outval, 0, j);
		return outval;
	}

	private static int _____upd(ProcessInfo[] buffer, int j, Pointer pval,
			String pName) {
		buffer[j] = new ProcessInfo(pName, GetProcessId(pval));
		return j + 1;
	}

	public static String getActiveWindowProcessName() {

		HWND fgw = GetForegroundWindow();

		PointerByReference pointer = new PointerByReference();

		GetWindowThreadProcessId(fgw, pointer);

		Pointer pval = pointer.getValue();

		if (pointer != null && pval != null) {
			String processName = getProcessName(pval);
			lastWindowProcessName = processName;
		}
		CloseHandle(pval);
		return lastWindowProcessName;
	}

	public static String getProcessName(Pointer pval) {
		char[] buffer = new char[MAX_TITLE_LENGTH * 2];
		Pointer process = OpenProcess(PROCESS_QUERY_INFORMATION
				| PROCESS_VM_READ, false, pval);
		if (GetModuleBaseNameW(process, null, buffer, MAX_TITLE_LENGTH) != 0) {
			// Debuggable.debug("Error: "+GetLastError());
		}

		CloseHandle(pval);
		String processName = Native.toString(buffer);
		return processName;
	}

	public static boolean killProcess(long pid) {
		Pointer processHandle = OpenProcess(PROCESS_TERMINATE, false, new Pointer(pid));
		boolean b = TerminateProcess(processHandle, new UINT(0)) != 0;
		CloseHandle(processHandle);
		return b;
	}

	public static ProcessInfo[] getProcessesInfo() {
		return getProcessesInfo(null);
	}
}