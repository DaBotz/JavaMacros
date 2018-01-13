package sgi.javaMacros.model.macros.execution.executors;

import java.awt.AWTException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.Timer;

import sgi.gui.ConfigPanelCreator;
import sgi.javaMacros.controller.LuaEvent;
import sgi.javaMacros.model.macros.execution.Executor;
import sgi.javaMacros.model.macros.sendkeys.actions.RobotAction;

public class ADOBE_OPACITY extends Executor implements ActionListener {

	private static int percent;
	private int step=3;
	private long delay;
	private transient Timer timer;

	private String pad() {
		percent += step;
		int stp = Math.max(1,  Math.abs(step));
		
		percent = (percent / stp) * stp;

		if (percent <= 0)
			percent = 1;

		if (percent >= 100)
			percent = 100;

		if (percent < 10)
			return "0" + percent;
		if (percent >= 100) {
			return "0";
		}

		return String.valueOf(percent);
	}

	private static void pressKey(char c) {
		int kCode;

		switch (c) {
		case '0':
			kCode = KeyEvent.VK_NUMPAD0;
			break;

		case '1':
			kCode = KeyEvent.VK_NUMPAD1;
			break;

		case '2':
			kCode = KeyEvent.VK_NUMPAD2;
			break;

		case '3':
			kCode = KeyEvent.VK_NUMPAD3;
			break;

		case '4':
			kCode = KeyEvent.VK_NUMPAD4;
			break;

		case '5':
			kCode = KeyEvent.VK_NUMPAD5;
			break;

		case '6':
			kCode = KeyEvent.VK_NUMPAD6;
			break;

		case '7':
			kCode = KeyEvent.VK_NUMPAD7;
			break;

		case '8':
			kCode = KeyEvent.VK_NUMPAD8;
			break;

		case '9':
			kCode = KeyEvent.VK_NUMPAD9;
			break;

		default:
			kCode = KeyEvent.getExtendedKeyCodeForChar(c);
		}

		RobotAction.keyPress(kCode);
		RobotAction.keyRelease(kCode);

	}

	@Override
	public int execute(LuaEvent event) {
		if (event.isDown())
			return PASS;

		try {
			RobotAction.getRobotx();

		} catch (AWTException e) {
			return FAIL;
		}

		if (delay > 0) {
			if (timer == null) {
				timer = new Timer((int) delay, this);
				timer.setRepeats(false);
			}
			timer.restart();

		} else
			actionPerformed(null);
		return COMPLETE;

	}

	@Override
	public Executor copyMe() {
		ADOBE_OPACITY keyStroker = new ADOBE_OPACITY();
		keyStroker.step = step;
		return keyStroker;
	}

	protected ConfigPanelCreator getPanelCreator() {

		ConfigPanelCreator panelCreator = super.getPanelCreator();
		panelCreator.DefaultSetters.setDefaultMinInt(-15);
		panelCreator.DefaultSetters.setDefaultMaxInt(15);
		panelCreator.DefaultSetters.setDefaultIntStep(1);
		panelCreator.DefaultSetters.setDefaultMaxLong(2000L);
		panelCreator.DefaultSetters.setDefaultLongStep(100L);

		return panelCreator;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		char[] cA = pad().toCharArray(); // $NON-NLS-1$
		System.out.println(cA);

		for (int i = 0; i < cA.length; i++) {
			pressKey(cA[i]);
		}

	};
}
