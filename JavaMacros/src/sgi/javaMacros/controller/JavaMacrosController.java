package sgi.javaMacros.controller;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog.ModalityType;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
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

		dialog.threadSafeShow();

	}

	private class DevicesSetObserver implements Runnable {

		@Override
		public void run() {
			String oDs, nDs = oDs = RawInputs.rawDevicesSnapshot();

			long ctm = System.currentTimeMillis() + 60000;
			while (System.currentTimeMillis() < ctm) {
				try {
					long deviceCheckInterval = Math.max(500, luaMacrosCfgBase.getDeviceCheckInterval());
					Thread.sleep(deviceCheckInterval);
					if (!oDs.equals(nDs = RawInputs.rawDevicesSnapshot())) {
						System.out.println("Something has changed?");
						system_Tray.displayMessage(Messages.M.getString(this, "DeviceChainChange.title"), //
								Messages.M.getString(this, "DeviceChainChange.body"));
						
						httpServer.addUpdate(getLuaComposer().getScanUpdate());
					}
					oDs = nDs;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}

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
		Thread deviceThread = new Thread(new DevicesSetObserver());
		deviceThread.setDaemon(true);
		deviceThread.setName("JavaMacros - Devices Snapshot Observer");
		deviceThread.start();
	}

	protected void launchLuaMacros() {
		File luaMacrosLocation = luaMacrosCfgBase.getLuaMacrosLocation();

		if (luaMacrosExecutable == null) {
			luaMacrosExecutable = new ExternalProcess(luaMacrosLocation.getName());
		}
		try {
			luaMacrosExecutable.kill();

		} catch (UnkillableProcessException e) {e.printStackTrace(); }

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
			ConfigFrame cffDlg = new JMAcrosLuaMacrosConfigurator(luaMacrosCfgBase, Messages.M);
			cffDlg.threadSafeShow();
			do {

				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
				}
			} while (luaMacrosCfgBase.isInconherent() && cffDlg.isVisible());

			if (luaMacrosCfgBase.isInconherent()) {
				JOptionPane.showMessageDialog(cffDlg, Messages.M.getString(this, "luaMacrosCfgBase.isInconherent"));
				System.exit(1);
			}
		}
	}

	public void launchServer() {
		httpServer = new HTTPServer(this);
		httpServer.run();
	}

	public int getHttpServerPort() {
		return (int) luaMacrosCfgBase.getServerPort();
	}

	private String lastUnknowdeviceId;
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

			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					processLuaEvent(event);
				}
			});

			DeviceSet devices = memory.getDeviceSet();
			String luaId = event.getLuaId();
			Device device = devices.find(luaId);
			event.setDevice(device);

			if (device == null)
				return "PASS";

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

	protected void processLuaEvent(LuaEvent event) {
		DeviceSet devices = memory.getDeviceSet();
		Device device = event.getDevice();
		String luaId = event.getLuaId();
		if (device == null) {

			if (!event.isDown() || luaId.equals(lastUnknowdeviceId))

				lastUnknowdeviceId = luaId;

			DeviceInputDialog dialog = new DeviceInputDialog(null, ModalityType.TOOLKIT_MODAL);
			dialog.setDeviceSet(devices);
			Device ndev = new Device(luaId);
			dialog.build(ndev, Messages.M);
			dialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
			GUIMemory.add(dialog);
			dialog.threadSafeShow();

			do {

				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
				}
			} while (!dialog.isSaved() && dialog.isVisible());
			dialog.setVisible(false);

			if (dialog.isSaved()) {
				devices.add(device = ndev);
				memory.storeToFile();
				if (device.isIgnored() || device.isAutonomousNumLock()) {
					httpServer.addUpdates(getLuaComposer().getUpdates());
				}

			}

			lastUnknowdeviceId = "";
		}

		if (device != null) {
			event.setDeviceName(device.getName());
			dispatchToMacros(event);
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
		boolean needscan= false; 
		for (String string : updates) {
			needscan|= (string.startsWith(UpdateCommands.ALLOW+"(" )
					//|| string.startsWith(UpdateCommands.AVOID+"(" )
					);
		}
		if(needscan)updates.add(getLuaComposer().getScanUpdate());
		httpServer.addUpdates(	updates); 
		
	}

}
