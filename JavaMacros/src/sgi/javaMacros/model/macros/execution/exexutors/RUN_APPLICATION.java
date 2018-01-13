package sgi.javaMacros.model.macros.execution.executors;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JComponent;

import sgi.gui.ConfigPanelCreator;
import sgi.gui.RenderingType;
import sgi.gui.configuration.IAwareOfChanges;
import sgi.javaMacros.controller.LuaEvent;
import sgi.javaMacros.model.macros.execution.ErrorCode;
import sgi.javaMacros.model.macros.execution.ExecutesOnEventRaiseOnly_WithTimeout;
import sgi.javaMacros.model.macros.execution.Executor;
import sgi.javaMacros.model.macros.execution.IScanner;
import sgi.javaMacros.model.macros.execution.NonDecoratingTextSaver;
import sgi.javaMacros.model.macros.execution.RunModes;
import sgi.javaMacros.model.macros.execution.ScriptSegment;
import sgi.javaMacros.msgs.Messages;
import sgi.javaMacros.ui.StyledTextPane;
import sgi.os.ExecuteAsSystemShell;

public class RUN_APPLICATION extends ExecutesOnEventRaiseOnly_WithTimeout implements IScanner {

	public RUN_APPLICATION() {
		super(true);
	}

	private static final int ERROR = 0;

	public File getTargetFile() {
		return targetFile;
	}

	private File targetFile;
	private String runCommand;
	private RunModes runMode = RunModes.WINDOWS_SHELL;

	private transient ArrayList<ErrorCode> errorCodes = new ArrayList<>();
	private transient String[] parsedCommand;
	private transient int[] statusCodes;
	private static String[] splitPathExts;
	private static transient String[] splitPaths;

	protected String getRunCommand() {
		return runCommand;
	}

	public void setRunCommand(String runCommand) {
		String oldCommand = this.runCommand;
		this.runCommand = runCommand;
		parsedCommand = null;
		notifyPropertyChange("runCommand", oldCommand, runCommand);
	}

	@Override
	protected int executeOnKeyUp(LuaEvent event) {

		switch (runMode) {

		case JAVA:
			return javaExec();

		case WINDOWS_SHELL:
		default:
			return execAsWindowsShell();

		}

	}

	private int javaExec() {
		ProcessBuilder builder = new ProcessBuilder(parseArgs());
		try {
			builder.start();
			return COMPLETE;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return FAIL;
	}

	public int execAsWindowsShell() {
		try {
			ExecuteAsSystemShell.execute(parseArgs());

			return COMPLETE;
		} catch (IOException e) {

			e.printStackTrace();
		}

		return FAIL;
	}

	public String[] parseArgs() {
		return parseArgs(false);
	}

	private String[] parseArgs(boolean force) {
		if (!(force || parsedCommand == null))
			return parsedCommand;

		errorCodes.clear();
		runCommand = runCommand.replaceAll("^(\\s+)", "");
		StringBuffer b = new StringBuffer(runCommand.length());

		char[] charArray = runCommand.toCharArray();
		this.statusCodes = new int[charArray.length];

		ArrayList<String> l = new ArrayList<>();
		boolean in = false;
		ErrorCode potentialError = null;

		for (int i = 0; i < charArray.length; i++) {
			statusCodes[i] = 1;
			char c = charArray[i];

			if (c == '"') {
				if (in) {
					l.add(b.toString());
					b = new StringBuffer();
					potentialError = null;

				} else {
					potentialError = new ErrorCode("Unclosed String", i, charArray.length - 1);
				}
				in = !in;
			} else if (!in && Character.isWhitespace(c)) {

				if (b.length() > 0) {
					l.add(b.toString());
					b = new StringBuffer();
				}
			} else
				b.append(c);
		}
		if (b.length() > 0)
			l.add(b.toString());
		if (l.size() == 0)
			return new String[] {};
		String[] parsed = new String[l.size()];
		l.toArray(parsed);

		String exeFile = parsed[0];
		int indexOf = runCommand.indexOf(exeFile);
		if (!isExecutableFile(exeFile)) {
			errorCodes.add(new ErrorCode("Unknown executable file", indexOf, indexOf + exeFile.length()));
		} else {

			for (int q = indexOf + exeFile.length(); indexOf < q && indexOf < statusCodes.length; indexOf++)
				statusCodes[indexOf] = 4;

		}

		if (potentialError != null)
			errorCodes.add(potentialError);

		return (parsedCommand = parsed);
	}

	private boolean isExecutableFile(String exeFile) {
		File f = new File(exeFile);
		if (f.isFile())
			return true;

		String pathExts = System.getenv("PATHEXT");
		if (splitPathExts == null) {
			if (pathExts != null) {
				ArrayList<String> splitPathExtList = new ArrayList<>();
				splitPathExtList.add("");
				splitPathExtList.add(".lnk");
				splitPathExtList.addAll(Arrays.asList(pathExts.split(File.pathSeparator)));
				splitPathExts = splitPathExtList.toArray(new String[splitPathExtList.size()]);
			}
		}

		if (splitPaths == null) {
			String pathV = System.getenv("PATH");
			splitPaths = pathV.split(File.pathSeparator);
		}

		for (String path : splitPaths) {
			for(String ext: splitPathExts) {
				if (new File(path, exeFile +ext).isFile())
					return true;
			}
		}

		return false;
	}

	protected ArrayList<ErrorCode> getErrorCodes() {
		return errorCodes;
	}

	public ArrayList<ScriptSegment> getSegmentation() {

		ArrayList<ScriptSegment> segments = new ArrayList<>();
		if (statusCodes.length == 0)
			return segments;
		// char[] charArray = LastScanned.toCharArray();
		// printStatus(charArray);
		ArrayList<ErrorCode> errorcodes2 = getErrorCodes();
		for (ErrorCode eC : errorcodes2) {
			for (int k = eC.getStart(); k < eC.getEnd() && k < statusCodes.length; k++) {
				statusCodes[k] = ERROR;
			}
		}
		// printStatus(charArray);

		int b, a = b = statusCodes[0];
		int cut, i = cut = 0;

		for (; i < statusCodes.length; i++) {
			b = statusCodes[i];
			if (b != a) {
				segments.add(new ScriptSegment(runCommand.substring(cut, i), a, cut, i));
				cut = i;
				a = b;
			}
		}
		segments.add(new ScriptSegment(runCommand.substring(cut, i), b, cut, i));

		// for (ScriptSegment seg : segments) {
		// System.out.print(seg.getPart() + "::" + seg.getStatus());
		// }
		// System.out.println();
		return segments;

	}

	@Override
	public void scan(String text) {
		setRunCommand(text);
		parseArgs(true);
	}

	private transient Component inputUI = null;

	@Override
	public Component getInputGUI() {

		if (inputUI != null)
			return inputUI;

		final RUN_APPLICATION scanner = this;

		ConfigPanelCreator creator = new ConfigPanelCreator(Messages.M, getClass().getCanonicalName()) {

			@Override
			protected JComponent createTextField(IAwareOfChanges obj, Field field)
					throws IllegalArgumentException, IllegalAccessException {
				final StyledTextPane styledTextPane = new StyledTextPane(scanner);
				new NonDecoratingTextSaver(obj, field, styledTextPane).updateComponent();

				Dimension preferredSize = new Dimension(getComboSize().width, 4 * getOccupiedRowHeight());
				styledTextPane.setPreferredSize(preferredSize);
				styledTextPane.setMinimumSize(preferredSize);
				styledTextPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));
				return styledTextPane;
			}

		};
		creator.DefaultSetters.setDefaultEnumRenderingType(RenderingType.HORIZONTAL_RADIOBUTTONS);
		creator.setAddingEndButtons(false);
		creator.setUseFieldSeparators(false);

		addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if ("targetFile".equals(evt.getPropertyName()) && evt.getNewValue() != null) {

					setRunCommand((runCommand == null ? "" : runCommand + " ") + "\"" + evt.getNewValue() + "\"");
				}

			}
		});

		return inputUI = creator.createConfigPanel(this);

	}

	@Override
	public String toString() {
		return "Run " + getRunCommand();
	}

	@Override
	public Executor copyMe() {

		RUN_APPLICATION runApp = new RUN_APPLICATION();
		runApp.runCommand = runCommand;
		runApp.runMode = runMode;

		return runApp;
	}

}
