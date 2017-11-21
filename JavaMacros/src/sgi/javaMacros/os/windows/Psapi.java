/* Copyright (c) 2015 Andreas "PAX" L\u00FCck, All Rights Reserved
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.  
 */
package sgi.javaMacros.os.windows;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32Util;
import com.sun.jna.platform.win32.WinDef.HMODULE;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

/**
 * The process status application programming interface (PSAPI) is a helper
 * library that makes it easier for you to obtain information about processes
 * and device drivers.
 * 
 * @author Andreas "PAX" L&uuml;ck, onkelpax-git[at]yahoo.de
 */
public interface Psapi extends StdCallLibrary {
	public static final Psapi INSTANCE = (Psapi) Native.loadLibrary("psapi",
			Psapi.class, W32APIOptions.DEFAULT_OPTIONS);
	
	/**
	 * Retrieves the fully qualified path for the file containing the specified
	 * module.
	 * 
	 * @param process
	 *            A handle to the process that contains the module.
	 * @param module
	 *            A handle to the module. If this parameter is NULL,
	 *            GetModuleFileNameEx returns the path of the executable file of
	 *            the process specified in hProcess.
	 * @param lpFilename
	 *            A pointer to a buffer that receives the fully qualified path
	 *            to the module. If the size of the file name is larger than the
	 *            value of the nSize parameter, the function succeeds but the
	 *            file name is truncated and null-terminated.
	 * @param nSize
	 *            The size of the lpFilename buffer, in characters.
	 * @return If the function succeeds, the return value specifies the length
	 *         of the string copied to the buffer. If the function fails, the
	 *         return value is zero. To get extended error information, call
	 *         {@link Kernel32Util#getLastErrorMessage()}.
	 */
	int GetModuleFileNameExA(final HANDLE process, final HANDLE module,
			final byte[] lpFilename, final int nSize);
	
	/**
	 * Retrieves the fully qualified path for the file containing the specified
	 * module.
	 * 
	 * @param process
	 *            A handle to the process that contains the module.
	 * @param module
	 *            A handle to the module. If this parameter is NULL,
	 *            GetModuleFileNameEx returns the path of the executable file of
	 *            the process specified in hProcess.
	 * @param lpFilename
	 *            A pointer to a buffer that receives the fully qualified path
	 *            to the module. If the size of the file name is larger than the
	 *            value of the nSize parameter, the function succeeds but the
	 *            file name is truncated and null-terminated.
	 * @param nSize
	 *            The size of the lpFilename buffer, in characters.
	 * @return If the function succeeds, the return value specifies the length
	 *         of the string copied to the buffer. If the function fails, the
	 *         return value is zero. To get extended error information, call
	 *         {@link Kernel32Util#getLastErrorMessage()}.
	 */
	public int GetModuleFileNameExW(final HANDLE process, final HANDLE module,
			final char[] lpFilename, final int nSize);

	/**
	 * Retrieves the fully qualified path for the file containing the specified
	 * module.
	 * 
	 * @param process
	 *            A handle to the process that contains the module.
	 * @param module
	 *            A handle to the module. If this parameter is NULL,
	 *            GetModuleFileNameEx returns the path of the executable file of
	 *            the process specified in hProcess.
	 * @param lpFilename
	 *            A pointer to a buffer that receives the fully qualified path
	 *            to the module. If the size of the file name is larger than the
	 *            value of the nSize parameter, the function succeeds but the
	 *            file name is truncated and null-terminated.
	 * @param nSize
	 *            The size of the lpFilename buffer, in characters.
	 * @return If the function succeeds, the return value specifies the length
	 *         of the string copied to the buffer. If the function fails, the
	 *         return value is zero. To get extended error information, call
	 *         {@link Kernel32Util#getLastErrorMessage()}.
	 */
	public  int GetModuleFileNameEx(final HANDLE process, final HANDLE module,
			final Pointer lpFilename, final int nSize);
	
	/**
	 * Retrieves the process identifier for each process object in the system.
	 * 
	 * @param pProcessIds a buffer of integers
	 * @param cb
	 * @param pBytesReturned : number of bytes returned; to use a regressive scan of the buffer for the first NON-Zero value in it; 
	 * @return
	 */
	public  int EnumProcesses(int[] pProcessIds, long cb, IntByReference pBytesReturned);

	/**
	 * Retrieves the base name of the specified module.
	 * 
	 * @param hProcess A handle to the process that contains the module. 
	 * @param hmodule A handle to the module. If this parameter is NULL, this function returns the name of the file used to create the calling process.
	 * @param lpBaseName A pointer to the buffer that receives the base name of the module. If the base name is longer than maximum number of characters specified by the nSize parameter, the base name is truncated.
	 * @param size The size of the lpBaseName buffer, in characters.
	 * @return
	 */
	public int GetModuleBaseNameW(HANDLE hProcess, HMODULE hmodule, char[] lpBaseName, int size);

	
}
