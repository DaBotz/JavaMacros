package sgi.javaMacros.os.windows;

import com.sun.jna.Callback;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.win32.StdCall;

public interface CallbackProc extends Callback, StdCall {
    int callback(HWND hWnd, int uMsg, WPARAM uParam, LPARAM lParam);
}




