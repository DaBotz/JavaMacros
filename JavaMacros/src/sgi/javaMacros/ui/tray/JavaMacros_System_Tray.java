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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import sgi.generic.debug.Debug;
import sgi.javaMacros.model.JavaMacrosMemory;
import sgi.javaMacros.model.internal.ApplicationForMacros;
import sgi.javaMacros.model.internal.UseCase;
import sgi.javaMacros.model.internal.UseCases;
import sgi.javaMacros.msgs.Messages;

public class JavaMacros_System_Tray {

	private class UseCaseSelectionListener implements ActionListener {
		private ApplicationForMacros app;
		private UseCase useCase;

		public UseCaseSelectionListener(ApplicationForMacros application, UseCase useCase) {
			this.app = application;
			this.useCase = useCase;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			app.set___currentUseCase(useCase);
			JavaMacros_System_Tray.this.setCurrentApplication(app);
		}
	}

	private static final MenuItem MY_1st_SEPARATOR = new MenuItem("-");
	private static final MenuItem MY_2nd_SEPARATOR = new MenuItem("-");

	private static final String FONT_FACE = "Verdana"; //$NON-NLS-1$

	private static final int _TITLE = 13;

	private static final Font BOLD = new Font(FONT_FACE, Font.BOLD, _TITLE).deriveFont((float) 13.1);
	private static final Font NORMAL = new Font(FONT_FACE, Font.PLAIN, _TITLE).deriveFont((float) 13.1);
	@SuppressWarnings("unused")
	private static final Font ITALIC = new Font(FONT_FACE, Font.ITALIC, _TITLE).deriveFont((float) 13.1);
	private ApplicationForMacros currentApplication;
	private PropertyChangeListener applicationsListener;

	public ApplicationForMacros getCurrentApplication() {
		return currentApplication;
	}

	public void setCurrentApplication(ApplicationForMacros app) {
		this.currentApplication = app;
		if( applicationsListener== null) {
			applicationsListener= new PropertyChangeListener() {

				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					Object source = evt.getSource();
					
					if (source instanceof UseCases) {
						source= ((UseCases) source).getParent();
					}else
					if (source instanceof UseCase) {
						source= ((UseCase) source).getParent().getParent();
					}
					if( source == currentApplication) {
						refresh(); 
					}
				}
				
				
			};
			JavaMacrosMemory.instance().getApplications().addPropertyChangeListener(applicationsListener);
			
		}
		refresh();
	}

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
				Debug.print(this, "TrayIcon could not be added."); //$NON-NLS-1$
			}

		} else {

		}

	}

	public void refresh() {

		try {
			if (trayIcon != null)
				trayIcon.setPopupMenu(createPopup());
		} catch (Throwable e) {
			Debug.info(e);
		}
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
		popup.add(MY_1st_SEPARATOR);
		addCurrentAppCalls(popup, NORMAL);

		popup.add(createOption(TrayEventType.DIE, BOLD));

		fillUncoveredOptions(popup);
		return popup;
	}

	private void addCurrentAppCalls(PopupMenu popup, Font f) {
		if (currentApplication != null) {
			ArrayList<UseCase> manuals = currentApplication.getUseCases().getManuals();
			if (manuals.size() > 0) {
				MenuItem applicationItem = new MenuItem(currentApplication.getName());
				boolean bGiven = false;

				for (UseCase useCase : manuals) {

					MenuItem useCaseItem = null;
					if (useCase.isBasic()) {
						useCaseItem = applicationItem;
					} else {
						useCaseItem = new MenuItem("      " + useCase.getName());

					}
					useCaseItem.addActionListener(new UseCaseSelectionListener(currentApplication, useCase));
					if (useCase == currentApplication.get___currentUseCase()) {
						useCaseItem.setFont(f.deriveFont(Font.BOLD));
						bGiven = true;
					}
					popup.add(useCaseItem);

				}
				if (!bGiven)
					applicationItem.setFont(f.deriveFont(Font.BOLD));

				popup.add(MY_2nd_SEPARATOR);
			}
		}

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