package sgi.javaMacros.debug;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import sgi.javaMacros.StringConstants;

public class Debug implements StringConstants {

	private static final String CRLF = new String(new char[] { 13, 10 });
	private static boolean isDevelopment;
	private static boolean isVerbose;
	private static File logFile;

	public static boolean isDev() {
		return isDevelopment;
	}

	static {
		isDevelopment = false;
		try {
			Object isIt = Class.forName("sgi.javaMacros.debug.IsDevelopment").newInstance();
			isDevelopment = (isIt != null);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {

			isDevelopment = false;
			System.err.println("AQpplication is in release mode");
		}

		String property = System.getProperty(VERBOSE_PROPERTY);

		isVerbose = YES.equalsIgnoreCase(property) //
				|| TRUE.equalsIgnoreCase(property);

	}
	
	public static void verbose(Object ...elements ){
		if(isVerbose){
			
		}
	}

	public Debug() {
		super();
	}

	public static void $(String string) {
		if (isDev())
			System.out.println(string);
		
	}

	public static void err(String string) {
		if (isDev())
			System.err.println(string);
	}

	public static void $(int v) {
		$(StringConstants.NULL_STRING + v);
	}

	public static void $(boolean v) {
		$(StringConstants.NULL_STRING + v);
	}

	public static void pStack(Throwable e) {
		if (isDev())
			e.printStackTrace();
	}

	public static void log(Object... elements) throws IOException {

		FileWriter fw = new FileWriter(getLogFile(), true);
		fw.write("-------------------------------------------------------------------"); //$NON-NLS-1$
		fw.write(CRLF);
		for (int j = 0; j < elements.length; j++) {
			String string = elements[j].toString();
			fw.write(string);
			fw.write(CRLF);
			if( isDev()) System.out.println(string);
		}
		fw.close();
	}

	private static File getLogFile() {
		if (logFile == null) {
			logFile = new File(new File(System.getProperty(StringConstants.USER_HOME)),
					Debug.class.getPackage().getName().replace(StringConstants.DOT, File.separatorChar));
			logFile.mkdirs();
			logFile = new File(logFile, "log.txt");
		}
		return logFile; // $NON-NLS-1$
	}

	public static void openLog() {
		try {
			Runtime.getRuntime().exec("notepad \"" + getLogFile().getAbsolutePath() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

}
