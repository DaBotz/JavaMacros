package sgi.javaMacros.os.windows;

import com.sun.jna.Callback;
import com.sun.jna.LastErrorException;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinBase.SECURITY_ATTRIBUTES;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.INT_PTR;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinUser.MSG;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

public interface CoreDll extends StdCallLibrary {
	// loads the coredll with unicode options
	CoreDll INSTANCE = (CoreDll) Native.loadLibrary("coredll", CoreDll.class,
			W32APIOptions.UNICODE_OPTIONS);

	// native calls
	INT_PTR CreateWindowEx(int dwExStyle, String lpClassName,
			String lpWindowName, int dwStyle, int x, int y, int width,
			int height, INT_PTR hWndParent, int hMenu, INT_PTR hInstance,
			String lpParam) throws LastErrorException;

	int DefWindowProc(HWND hWnd, int msg, WPARAM wParam, LPARAM lParam);

	long SetWindowLong(INT_PTR hWnd, int nIndex, Callback dwNewLong)
			throws LastErrorException;

	INT_PTR CreateEvent(SECURITY_ATTRIBUTES lpEventAttributes,
			boolean bManualReset, boolean bInitialState, String lpName);

	int MsgWaitForMultipleObjectsEx(int nCount, INT_PTR[] lpHandles,
			boolean fWaitAll, int dwMilliseconds, int dwWakeMask);

	int DestroyWindow(INT_PTR hwnd);

	boolean PeekMessage(MSG lpMsg, INT_PTR hWnd, int wMsgFilterMin,
			int wMsgFilterMax, int wRemoveMsg);

	boolean TranslateMessage(MSG lpMsg) throws LastErrorException;

	int DispatchMessage(MSG lpMsg) throws LastErrorException;

	void SendMessage(INT_PTR hWnd, int message, WPARAM wParam, LPARAM lParam);

}
