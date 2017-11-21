package sgi.javaMacros.controller;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog.ModalityType;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.WindowConstants;

import sgi.generic.serialization.AbstractMemoryRoot;
import sgi.generic.ui.ConfigFrame;
import sgi.generic.ui.GUIMemory;
import sgi.generic.ui.IMessages;
import sgi.javaMacros.model.JavaMacrosLuaMacrosConfiguration;
import sgi.javaMacros.model.JavaMacrosMemory;
import sgi.javaMacros.model.internal.Device;
import sgi.javaMacros.model.lists.DeviceSet;
import sgi.javaMacros.msgs.Messages;
import sgi.javaMacros.os.windows.RawInputs;
import sgi.javaMacros.ui.dialogs.ApplicationListDialog;
import sgi.javaMacros.ui.dialogs.DeviceInputDialog;
import sgi.javaMacros.ui.dialogs.DeviceSetFrame;
import sgi.javaMacros.ui.dialogs.NoButtonsDeviceDialog;
import sgi.javaMacros.ui.tray.JavaMacros_System_Tray;
import sgi.javaMacros.ui.tray.TrayEvent;
import sgi.javaMacros.ui.tray.TrayEventListener;

public class JavaMacrosController {

	private JavaMacrosLuaMacrosConfiguration luaMacrosCfgBase;
	JavaMacrosMemory memory;

	public static class JMAcrosLuaMacrosConfigurator extends ConfigFrame {
		/**
		 * 
		 */
		private static final long serialVersionUID = 7697988091L;

		public JMAcrosLuaMacrosConfigurator(AbstractMemoryRoot configuration, IMessages msgs) throws HeadlessException {
			super(configuration, msgs);
			setDefaultCloseOperation(HIDE_ON_CLOSE);
		}

		@Override
		protected void closeDialog(ActionEvent e) {
			setVisible(false);
		}

		@Override
		protected String[] noFields() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public JComponent createLongEditor(Object obj, Field field) throws IllegalAccessException {
			JComponent editor = super.createLongEditor(obj, field);

			if ("serverPort".equalsIgnoreCase(field.getName()) && editor instanceof Container) {
				hideLabels((Container) editor);
			}

			return editor;
		}

		protected void hideLabels(Container p) {
			Component[] components = p.getComponents();
			for (Component component : components) {
				if (component instanceof JLabel) {
					((JLabel) component).setVisible(false);

				}
				if (component instanceof Container) {
					hideLabels((Container) component);

				}
			}
		}

	}

	public class TrayEventsHandler implements TrayEventListener {

		@Override
		public boolean handleTrayEvent(TrayEvent ev) {
			switch (ev.getType()) {
			case DIE:
				System.exit(0);
				break;
			case OPEN_GUI:
				open_gui();
				break;
			case CHANGELUACONFIG:
				changeLuaConfig();
				break;
			case FORCE_LUA_RELAUNCH:
				launchLuaMacros();
				break;
			case EDIT_APPLICATIONS:
				editApplications();
				break;
			case EDIT_DEVICES:
				editDevices();

				break;

			}

			return false;
		}

		protected void editDevices() {
			new DeviceSetFrame(JavaMacrosController.this, memory.getDevices(), Messages.M).threadSafeShow();
		}

	}

	protected void open_gui() {
		// TODO Auto-generated method stub
		system_Tray.displayMessage("Warning", "Unimplemented yet");
	}

	public void editApplications() {

		ApplicationListDialog dialog = new ApplicationListDialog(null, ModalityType.APPLICATION_MODAL);
		dialog.loadApplications();
		dialog.autoPosition();
		dialog.setVisible(true);

	}

	private class DevicesSetObserver implements ActionListener {

		String oDs, nDs = oDs = RawInputs.rawDevicesSnapshot();
		private int counter = 0;
		private static final int multiplier = 4;

		@Override
		public void actionPerformed(ActionEvent e) {

			alignDelay(e);

			if ((++counter) == multiplier) {
				counter = 0;
				if (!oDs.equals(nDs = RawInputs.rawDevicesSnapshot())) {
					system_Tray.displayMessage(Messages.M.getString(this, "DeviceChainChange.title"), //
							Messages.M.getString(this, "DeviceChainChange.body"));

					httpServer.addUpdate(getLuaComposer().getScanUpdate());
					oDs = nDs;
				}
				if (!(!luaMacrosCfgBase.isLuaMacrosKeptAlive() || //
						luaMacrosExecutable == null //
						|| luaMacrosExecutable.isRunning())) {
					launchLuaMacros();
				}
			}

		}

		protected void alignDelay(ActionEvent e) {
			Object source = e.getSource();
			if (source instanceof Timer) {
				((Timer) source).setDelay(getDelay());
			}
		}

		protected int getDelay() {
			int deviceCheckInterval = (int) luaMacrosCfgBase.getDeviceCheckInterval();
			deviceCheckInterval = Math.max(100, deviceCheckInterval / multiplier);
			return deviceCheckInterval;
		}

		public void start() {

			Timer timer = new Timer((int) luaMacrosCfgBase.getDeviceCheckInterval(), this);
			timer.start();
		}

	}

	public void start() {

		loadLuaMacrosCfg();
		memory = JavaMacrosMemory.instance();

		system_Tray = new JavaMacros_System_Tray();
		system_Tray.addTrayEventListener(new TrayEventsHandler());
		launchServer();
		launchLuaMacros();
		launchDevicesObserver();
		addShutdowns();
	}

	protected void addShutdowns() {
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

			@Override
			public void run() {
				if (luaMacrosExecutable != null)
					try {
						luaMacrosExecutable.kill();
					} catch (UnkillableProcessException e) {
						e.printStackTrace();
					}

				memory.storeToFile();
			}
		}));
	}

	protected void launchDevicesObserver() {
		new DevicesSetObserver().start();
	}

	protected void launchLuaMacros() {
		File luaMacrosLocation = luaMacrosCfgBase.getLuaMacrosLocation();

		if (luaMacrosExecutable == null) {
			luaMacrosExecutable = new ExternalProcess(luaMacrosLocation.getName());
		}
		try {
			luaMacrosExecutable.kill();

		} catch (UnkillableProcessException e) {
			e.printStackTrace();
		}

		try {
			luaMacrosExecutable.launchApp(luaMacrosLocation.getPath()//
					, "-r"//
					, composeLuaConfiguration().getPath());
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	private File composeLuaConfiguration() throws IOException {

		File file = luaMacrosCfgBase.getFile("jMacros_LuaMacroscfg.lua");
		String Content = getLuaComposer().compose();
		String onFile = getLuaComposer().readFile(file);

		if (!Content.equals(onFile)) {
			FileWriter writer = new FileWriter(file);
			writer.write(Content);
			writer.close();
		}
		return file;
	}

	public LUaCfgComposer getLuaComposer() {
		if (luaComposer == null) {
			luaComposer = new LUaCfgComposer(luaMacrosCfgBase, memory.getDeviceSet());
		}
		return luaComposer;
	}

	protected void loadLuaMacrosCfg() {
		luaMacrosCfgBase = JavaMacrosLuaMacrosConfiguration.instance();

		if (luaMacrosCfgBase.isInconherent())
			changeLuaConfig();
	}

	protected void changeLuaConfig() {
		{
			final ConfigFrame cffDlg = new JMAcrosLuaMacrosConfigurator(luaMacrosCfgBase, Messages.M);
			cffDlg.autoPosition();
			cffDlg.setVisible(true);

			cffDlg.addComponentListener(new ComponentAdapter() {
				@Override
				public void componentHidden(ComponentEvent e) {
					if (luaMacrosCfgBase.isInconherent()) {
						JOptionPane.showMessageDialog(cffDlg,
								Messages.M.getString(this, "luaMacrosCfgBase.isInconherent"));
						System.exit(1);
					}
				}
			});

		}
	}

	public void launchServer() {
		httpServer = new HTTPServer(this);
		httpServer.run();
	}

	public int getHttpServerPort() {
		return (int) luaMacrosCfgBase.getServerPort();
	}

	private ExternalProcess luaMacrosExecutable;
	private JavaMacros_System_Tray system_Tray;
	private HTTPServer httpServer;
	private LUaCfgComposer luaComposer;

	public String processDeviceEvent(Object source, Object scanCode, Object direction, Object deviceType) {
		// final Application activeWindow = ApplicationsRetriever.getActiveWindow();

		try {
			final LuaEvent event = new LuaEvent(//
					(String) source, //
					Integer.parseInt("" + scanCode), //
					Integer.parseInt("" + direction) == 1, //
					Integer.parseInt("" + deviceType));

	
			DeviceSet devices = memory.getDeviceSet();
			String luaId = event.getLuaId();
			Device device = devices.find(luaId);
			event.setDevice(device);
			
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					processLuaEvent(event);
				}
			});
			thread.setName("Macros Thread Decoupler");
			thread.start();


			if (device == null)
				return "UNKNOWN";

			if (device.isAutonomousNumLock() && event.getScanCode() == 144)
				checkForDevicesUpdate();

			if (device.isIgnored()) {

				return "PASS";
			}

			return "OK";

		} catch (Throwable e) {
			String crlf = LuaUpdater.crlf;

			String errorcode = "Malformed data for JavaMacros:" + crlf //
					+ " source = '" + source + "'," + crlf//
					+ " scanCode = '" + scanCode + "'," + crlf//
					+ " direction= '" + direction + "'," + crlf//
					+ " deviceType= '" + deviceType + "'" + crlf//
					+ crlf;
			System.err.println(errorcode);
			e.printStackTrace(System.err);
			return errorcode;
		}

	}

	private static String lastUnknowdeviceId;

	protected void processLuaEvent(LuaEvent event) {
		DeviceSet devices = memory.getDeviceSet();
		Device device = event.getDevice();
		String luaId = event.getLuaId();
		if (device == null) {
			
			if (event.isDown())
				return;


			if (luaId.equals(lastUnknowdeviceId))
				return;


			lastUnknowdeviceId = luaId;

			DeviceInputDialog dialog = new DeviceInputDialog(null, ModalityType.TOOLKIT_MODAL);
			dialog.setDeviceSet(devices);
			device = new Device(luaId);
			dialog.build(device, Messages.M);
			dialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
			dialog.threadSafeShow();

			do {
				sleep(100);
			} while (dialog.isVisible());

			if (dialog.isSaved()) {
				devices.add(device);
				memory.storeToFile();
				if (device.isIgnored() || device.isAutonomousNumLock()) {
					httpServer.addUpdates(getLuaComposer().getUpdates());
				}
			} else
				device = null;
			lastUnknowdeviceId = "";

		}

		if (device != null) {
			event.setDeviceName(device.getName());
			dispatchToMacros(event);
		}
	}

	private void sleep(int i) {
		try {
			Thread.sleep(i);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void dispatchToMacros(LuaEvent event) {

	}

	public JavaMacrosMemory getMemory() {
		return memory;
	}

	public ArrayList<String> getLuaInitializationCode() {
		return getLuaComposer().getInitInstructions();
	}

	public void checkForDevicesUpdate() {

		ArrayList<String> updates = getLuaComposer().getUpdates();
		boolean needscan = false;
		for (String string : updates) {
			needscan |= (string.startsWith(UpdateCommands.ALLOW + "(")
			// || string.startsWith(UpdateCommands.AVOID+"(" )
			);
		}
		if (needscan)
			updates.add(getLuaComposer().getScanUpdate());
		httpServer.addUpdates(updates);

	}

}
