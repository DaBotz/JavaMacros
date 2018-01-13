package sgi.javaMacros;

import javax.swing.SwingUtilities;

import sgi.generic.debug.Debug;
import sgi.javaMacros.controller.JavaMacrosController;
import sgi.javaMacros.model.BootData;
import sgi.javaMacros.ui.StartupSplashPage;

public class JavaMacrosLauncher {
	public static void main(String[] args) {
		// set the property that "debug" will check to override the automatic
		// dev/release check
		Debug.VERBOSE_PROPERTY_NAME = "javamacros.verbose";
		if (BootData.instance().isStartUpDisplayToBeShown()) {
			launchStartUpDisplay();
		}

		JavaMacrosController.instance().start(args);

	}

	public static void displayStartUpMessage(String message) {
		if (splashPage != null) {
			splashPage.setDisplayText(message);
			//splashPage.setAlwaysOnTop(false);
		}
	}

	public static void closeStartUpDisplay() {
		if (splashPage != null) {
			splashPage.setVisible(false);
		}
	}

	protected static StartupSplashPage splashPage;

	private static void launchStartUpDisplay() {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				splashPage = new StartupSplashPage();
				splashPage.setAlwaysOnTop(true);
				splashPage.setVisible(true);
			}
		});

	}

}
