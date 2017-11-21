package sgi.javaMacros.os.windows;

import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.INT_PTR;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinUser.MSG;


public class TestStuff {

	public static final int WAIT_OBJECT_0 = 0;
	public static final int PM_REMOVE = 1;
	//private static final long INFINITE = 0xffffffffL;
	/**
	 * @param args
	 */
	public static void main(String[] args) {


		//Create a new native window
		INT_PTR m_hwnd=CoreDll.INSTANCE.CreateWindowEx(0, "STATIC", "", 0, 0, 0, 0, 0, null, 0, new INT_PTR(), new String());
		//defines the method to replace WndProc
		final CallbackProc ptr=new CallbackProc() {
		    @Override
		    public int callback(HWND hWnd, int uMsg, WPARAM wParam,LPARAM lParam) {
		    //    doStuff();
			return CoreDll.INSTANCE.DefWindowProc(hWnd, uMsg, wParam, lParam);
		    }
		};
		//Sets the new WndProc to the new window
		int num=(int)CoreDll.INSTANCE.SetWindowLong(m_hwnd,-4 ,ptr);
		//Sets the handler to the library to send messages to that window
	//	num = (int) theDLL.INSTANCE.RfidSetHwnd(m_hwnd);
		//Starts to send messages
	//	num = (int) theDLL.INSTANCE.triggerProcedure();
		
		System.out.println(num);

		MSG msg = new MSG();
		INT_PTR intermediate_result=CoreDll.INSTANCE.CreateEvent(null, false,false, null);
		final INT_PTR handles[] = { intermediate_result };
		while (true) 
		{
			int result = CoreDll.INSTANCE.MsgWaitForMultipleObjectsEx(handles.length, handles, false,Integer.MAX_VALUE ,InputConstants.QS_ALLINPUT);
			if (result == WAIT_OBJECT_0) {
				CoreDll.INSTANCE.DestroyWindow(m_hwnd);
			    	break;
			}
		    if (result != WAIT_OBJECT_0 + handles.length) {
			// Serious problem, end the thread's run() method!
		    	break;
		    }
		    while (CoreDll.INSTANCE.PeekMessage(msg, null, 0, 0, PM_REMOVE)) {
		    	//This always prints 2634 or 2635 I don't know why
			    System.out.println(msg.message);
			    CoreDll.INSTANCE.TranslateMessage(msg);
		    	CoreDll.INSTANCE.DispatchMessage(msg);
		    	//Also try this but it doesn't work
			    //CoreDll.INSTANCE.SendMessage(m_hwnd, msg.message, new WPARAM(msg.wParam), new LPARAM(msg.lParam));
		    }
		}


	}

}
