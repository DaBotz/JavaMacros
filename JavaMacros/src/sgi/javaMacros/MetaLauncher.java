package sgi.javaMacros;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.JOptionPane;

public class MetaLauncher {

	private static final String JAVA_MACROS_EXE = "JavaMacrosW.exe";

	public static void main(String[] args) throws IOException {
		File f = new File(System.getProperty("user.dir"));

		FileFilter jreFilter = new FileFilter() {

			@Override
			public boolean accept(File f0) {
				return f0.isDirectory() && f0.getName().toLowerCase().startsWith("jre");
			}
		};

		File[] folders = f.listFiles(jreFilter);
		if (folders == null || folders.length == 0)
			folders = f.getParentFile().listFiles(jreFilter);

		if (folders == null || folders.length == 0) {
			JOptionPane.showMessageDialog(null,
					"JavaMacros MetaLauncher needs to be invoked from a directory containing a JRE subDirectory");
			System.exit(1);
		}

		File jre = folders[0];
		jre = new File(jre, "bin");

		File javaWAlias = new File(jre, JAVA_MACROS_EXE);
		if (!javaWAlias.isFile()) {
			if (javaWAlias.isDirectory()) {
				javaWAlias.renameTo(new File(jre, JAVA_MACROS_EXE + "-remnamed"));

			}

			copyStreamToFile(new FileInputStream(new File(jre, "javaw.exe")), javaWAlias);
		}

		ArrayList<String> commands = new ArrayList<>();

		commands.add(javaWAlias.getAbsolutePath());
		commands.add("-DjavaMacros.javawAlias=" + javaWAlias.getName());
		commands.add("-Xms128M");
		commands.add("-Xmx1024M");

		FileFilter jarFinderFilter = new FileFilter() {

			@Override
			public boolean accept(File f0) {
				// TODO Auto-generated method stub
				String lowerCase = f0.getName().toLowerCase();
				return f0.isFile() && lowerCase.startsWith("javamacros") && lowerCase.endsWith(".jar")
						&& (lowerCase.indexOf("launcher") < 0);
			}
		};

		File[] jars = f.listFiles(jarFinderFilter);

		if (jars == null || jars.length == 0) {
			File f2 = new File(f, "bin");
			if (f2.isDirectory()) {
				jars = f2.listFiles(jarFinderFilter);
			}
		}

		if (jars == null || jars.length == 0) {
			File f2 = new File(f, "lib");
			if (f2.isDirectory()) {
				jars = f2.listFiles(jarFinderFilter);
			}
		}

		if (jars == null || jars.length == 0) {
			JOptionPane.showMessageDialog(null,
					"JavaMacros MetaLauncher needs to be invoked from a directory containing at least one javamacros[...].jar file");
			System.exit(1);
		}

		if (jars.length > 1) {
			Arrays.sort(jars, new Comparator<File>() {
				@Override
				public int compare(File o1, File o2) {
					return 0 - o1.getName().compareTo(o2.getName());
				}
			});
		}

		commands.add("-jar");
		commands.add(jars[0].getPath());

		ProcessBuilder processer = new ProcessBuilder(commands);
		processer.environment().putAll(System.getenv());
		processer.redirectError(new File(f, "javamacros.error.log"));
		processer.redirectOutput(new File(f, "javamacros.info.log"));
		processer.start();
		System.exit(0);
	}

	/**
	 * Save a file from an inputstream.
	 * 
	 * @param fio
	 * @param doneFile
	 */
	public static void copyStreamToFile(InputStream fio, File doneFile) {
		FileOutputStream fout = null;
		try {
			fout = new FileOutputStream(doneFile);
			byte[] buffer = new byte[1 << 18];
			int l;
			do {
				l = fio.read(buffer);
				if (l > 0)
					fout.write(buffer, 0, l);
			} while (l >= 0);

		} catch (IOException e) {
		}

		if (fio != null)
			try {
				fio.close();
			} catch (IOException e) {
			}
		if (fout != null)
			try {
				fout.close();
			} catch (IOException e) {
			}
	}

}
