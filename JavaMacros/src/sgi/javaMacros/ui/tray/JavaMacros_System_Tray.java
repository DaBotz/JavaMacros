/**
 * 
 */
package sgi.javaMacros.ui.tray;

import java.awt.AWTException;
import java.awt.Font;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import sgi.javaMacros.debug.Debug;
import sgi.javaMacros.msgs.Messages;

public class JavaMacros_System_Tray {

	private static final String FONT_FACE = "Verdana"; //$NON-NLS-1$

	private static final int _TITLE = 13;

	private static final Font BOLD = new Font(FONT_FACE, Font.BOLD, _TITLE).deriveFont((float) 13.1);
	private static final Font NORMAL = new Font(FONT_FACE, Font.PLAIN, _TITLE).deriveFont((float) 13.1);
	private static final Font ITALIC = new Font(FONT_FACE, Font.ITALIC, _TITLE).deriveFont((float) 13.1);

	/**
	 * 
	 */

	private ArrayList<Image> imageCycle = new ArrayList<Image>();
	private ArrayList<TrayEventListener> listeners = new ArrayList<TrayEventListener>();

	public boolean addTrayEventListener(TrayEventListener listener) {
		return listeners.add(listener);
	}

	public boolean removeTrayEventListener(TrayEventListener listener) {
		return listeners.remove(listener);
	}

	private void fireTrayEvent(TrayEvent ev) {
		Iterator<TrayEventListener> it = listeners.iterator();
		while (it.hasNext())
			it.next().handleTrayEvent(ev);
	}

	boolean initialized = false;
	private Iterator<Image> iter;
	private long lastCycleStep = 0;

	private Image mainImage;

	private SystemTray tray;

	TrayIcon trayIcon;

	public void setTooltip(String msg) {
		trayIcon.setToolTip(msg);
	}

	// private Controller controller;

	private MenuItem rollbackOption;

	private Image pingedImage;

	private HashSet<TrayEventType> extCheck;

	public JavaMacros_System_Tray() {

		if (SystemTray.isSupported()) {
			tray = SystemTray.getSystemTray();

			mainImage = getImage("trayIcon.png"); //$NON-NLS-1$
			pingedImage = getImage("trayIcon-pinged.png"); //$NON-NLS-1$

			trayIcon = new TrayIcon(mainImage, "", createPopup()); // $NON-NLS-1$

			trayIcon.setImage(mainImage);
			initialized = true;
			trayIcon.setImageAutoSize(true);

			try {
				tray.add(trayIcon);
			} catch (AWTException e) {
				Debug.err("TrayIcon could not be added."); //$NON-NLS-1$
			}

		} else {

		}

	}

	public void refresh() {
		trayIcon.setPopupMenu(createPopup());
	}

	public void enableRollBacking(boolean roll) {
		rollbackOption.setEnabled(roll);
	}

	private PopupMenu createPopup() {
		PopupMenu popup = new PopupMenu();
		this.extCheck = new HashSet<TrayEventType>();
		popup.add(createOption(TrayEventType.OPEN_GUI, NORMAL));
		popup.add(createOption(TrayEventType.CHANGELUACONFIG, BOLD));
		popup.addSeparator();
		popup.add(createOption(TrayEventType.EDIT_APPLICATIONS, BOLD));
		popup.add(createOption(TrayEventType.EDIT_DEVICES, BOLD));
		popup.add(createOption(TrayEventType.FORCE_LUA_RELAUNCH, BOLD));
		popup.addSeparator();
		popup.add(createOption(TrayEventType.DIE, BOLD));
		

		fillUncoveredOptions(popup);
		return popup;
	}

	protected void fillUncoveredOptions(PopupMenu popup) {
		TrayEventType[] evts = TrayEventType.values();
		for (TrayEventType evt : evts) {
			if (!extCheck.contains(evt))
				popup.add(createOption(evt, NORMAL));

		}
	}

	protected String getEventLabel(TrayEventType evt) {

		return Messages.M.getString(getClass().getCanonicalName() + ".TrayEvent." + evt.name());

	}

	protected MenuItem createOption(final TrayEventType evtype, Font f) {
		extCheck.add(evtype);
		MenuItem defaultItem = new MenuItem(getEventLabel(evtype));
		defaultItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireTrayEvent(new TrayEvent(e.getSource(), evtype));
			}
		});

		defaultItem.setFont(f.deriveFont(Font.BOLD));
		return defaultItem;
	}

	public void trayPopUp(String caption, String text, MessageType messageType) {
		trayIcon.displayMessage(caption, text, messageType);
	}

	public void dispose() {
		if (trayIcon != null && SystemTray.isSupported()) {

			SystemTray tray = SystemTray.getSystemTray();
			tray.remove(trayIcon);
		}

	}

	public static Image getImage(String mainIcon) {
		return Toolkit.getDefaultToolkit().createImage(JavaMacros_System_Tray.class.getResource(mainIcon));
	}

	public boolean isInitialized() {
		return initialized;
	}

	public void resetImage() {
		iter = null;
		trayIcon.setImage(mainImage);
	}

	public void stepImage() {
		long now = System.currentTimeMillis();
		if (now - lastCycleStep > 30) {
			if (iter == null || !iter.hasNext())
				iter = imageCycle.iterator();
			if (iter.hasNext())
				trayIcon.setImage(iter.next());
		}
		lastCycleStep = now;
	}

	public void warn(String string) {
		trayPopUp(("SystemTrayInterface.Warning"), string, MessageType.WARNING); //$NON-NLS-1$
	}

	public void displayMessage(final String caption, final String msg) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				trayIcon.displayMessage(caption, msg, MessageType.INFO);
			}
		}).start();
	}

	public MenuItem getRollbackOption() {
		return rollbackOption;
	}

	public void pingIcon() {
		if (mainImage.equals(trayIcon.getImage())) {
			trayIcon.setImage(pingedImage);
		} else if (pingedImage.equals(trayIcon.getImage())) {
			trayIcon.setImage(mainImage);
		}

	}

}