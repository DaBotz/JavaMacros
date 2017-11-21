package sgi.javaMacros.os.windows;

public interface InputConstants {

/**
 * An input, WM_TIMER, WM_PAINT, WM_HOTKEY, or posted message is in the queue.
 * This value is a combination of QS_INPUT, QS_POSTMESSAGE, QS_TIMER, QS_PAINT, and QS_HOTKEY.
 * */
public static final int QS_ALLEVENTS =0x04BF;

	
/*
 * Any message is in the queue.
 * This value is a combination of QS_INPUT, QS_POSTMESSAGE, QS_TIMER, QS_PAINT, QS_HOTKEY, and QS_SENDMESSAGE.
 * */

public static final int QS_ALLINPUT = 0x04FF;
	


/**
 * A posted message is in the queue.
 * This value is cleared when you call GetMessage or PeekMessage without filtering messages.
 */

public static final int  QS_ALLPOSTMESSAGE = 0x0100; 

	
/**
 * A WM_HOTKEY message is in the queue.
 */
public static final int QS_HOTKEY = 0x0080; 

	

/**
 * An input message is in the queue.
 * This value is a combination of QS_MOUSE, QS_KEY, and QS_RAWINPUT.
 * */
public static final int QS_INPUT = 0x0407;

	

/***
 * A WM_KEYUP, WM_KEYDOWN, WM_SYSKEYUP, or WM_SYSKEYDOWN message is in the queue.
 */
public static final int QS_KEY = 0x0001; 

	

/**
 * A WM_MOUSEMOVE message or mouse-button message (WM_LBUTTONUP, WM_RBUTTONDOWN, and so on).
 * This value is a combination of QS_MOUSEMOVE and QS_MOUSEBUTTON.
 */
public static final int QS_MOUSE = 0x0006; 

	

/**
 * A mouse-button message (WM_LBUTTONUP, WM_RBUTTONDOWN, and so on).

 */
public static final int QS_MOUSEBUTTON = 0x0004; 

	

/**
 * A WM_MOUSEMOVE message is in the queue.
 */
public static final int QS_MOUSEMOVE = 0x0002; 

	


/***
 * 
A WM_PAINT message is in the queue.
 */
public static final int QS_PAINT  = 0x0020; ; 

	


/**
 * A posted message is in the queue.
This value is cleared when you call GetMessage or PeekMessage, whether or not you are filtering messages.

 */
public static final int QS_POSTMESSAGE  = 0x0008; ;


/**
 * A raw input message is in the queue. For more information, see Raw Input.
 */
public static final int QS_RAWINPUT
 = 0x0400; ;

	


/**
 * A message sent by another thread or application is in the queue.
 */
public static final int QS_SENDMESSAGE
 = 0x0040; ;

	


/***
 * A WM_TIMER message is in the queue.
 */
public static final int QS_TIMER
 = 0x0010; ; 

}
