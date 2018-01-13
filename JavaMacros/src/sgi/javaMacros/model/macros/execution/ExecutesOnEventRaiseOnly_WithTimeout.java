package sgi.javaMacros.model.macros.execution;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

import sgi.javaMacros.controller.LuaEvent;
import sgi.javaMacros.model.internal.Macro;
import sgi.javaMacros.ui.OSD.MacroOSDDisplayer;

public abstract class ExecutesOnEventRaiseOnly_WithTimeout extends Executor implements ActionListener, Runnable {

	private transient long downedAt;
	private transient boolean calledOSD;
	private transient Timer timer;

	@Override
	public int execute(LuaEvent event) {
		if (event.isDown()) {
			downedAt = System.currentTimeMillis();

			Macro macro = getMacro();
			if (!macro.isUsingOSDReminder())
				return PASS;
; 

			if (calledOSD)
				return PASS;

			calledOSD = true;
			if (macro.usesDeferredOSD()) {
				if (timer == null) {
					timer = new Timer(400, this);
					timer.setRepeats(false);
				}
				timer.restart();
				return PASS;
			} else {
				return PASS | REQUIRES_OSD;
			}
		}

		calledOSD = false;
		SwingUtilities.invokeLater(this);

		if (System.currentTimeMillis() - downedAt > 1500) {
			return PASS;
		}

		return executeOnKeyUp(event);
	}

	public ExecutesOnEventRaiseOnly_WithTimeout(boolean deferredOSD) {
		super();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (calledOSD) {
			displayOSD();

		}
	}

	protected void displayOSD() {
		MacroOSDDisplayer.displayOSD(getMacro());
	}

	@Override
	public void run() {
		MacroOSDDisplayer.hideOSD(getMacro());
	}

	protected abstract int executeOnKeyUp(LuaEvent event);

	public void ensureMacroUsesOSD() {
		if (!getMacro().isUsingOSDReminder())
			getMacro().setUsingOSDReminder(true);
	}

}
