package sgi.javaMacros.model.macros.execution.executors;

import java.util.ArrayList;

import javax.swing.SwingUtilities;

import sgi.gui.ConfigPanelCreator;
import sgi.gui.RenderingType;
import sgi.javaMacros.controller.LuaEvent;
import sgi.javaMacros.model.JavaMacrosMemory;
import sgi.javaMacros.model.enums.executors.ChangeUseCaseDirection;
import sgi.javaMacros.model.internal.ApplicationForMacros;
import sgi.javaMacros.model.internal.Macro;
import sgi.javaMacros.model.internal.UseCase;
import sgi.javaMacros.model.internal.UseCases;
import sgi.javaMacros.model.macros.execution.ExecutesOnEventRaiseOnly_WithTimeout;
import sgi.javaMacros.model.macros.execution.Executor;

public class CHANGE_USE_CASE extends ExecutesOnEventRaiseOnly_WithTimeout {

	@Override
	public void mount() {
		try {

			Macro mommy = getMacro();
			mommy.setUsingOSDReminder(true);
			mommy.getApplication().addCaselessMacro(mommy);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void unmount() {
		try {
			Macro mommy = getMacro();
			mommy.getApplication().removeCaselessMacro(mommy);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public CHANGE_USE_CASE() {
		super(true);
	}

	private transient String transientExeFile;
	private transient ApplicationForMacros targetApp;
	private transient boolean OSDshown;
	private ChangeUseCaseDirection direction = ChangeUseCaseDirection.NEXT;

	@Override
	public int execute(LuaEvent event) {

		try {
			transientExeFile = event.getActiveWindow().getExeFile();
		} catch (Exception e) {
			e.printStackTrace();
			return FAIL;
		}

		ensureMacroUsesOSD();
		return super.execute(event);
	}

	@Override
	protected void displayOSD() {
		avvisaUtente(true);
	}

	public void avvisaUtente(boolean preview) {
		OSDshown = preview;

		UseCase candidate = nextUseCase();
		String header;

		if (preview)
			header = "<center><h4><font color='#00ff00'>NEXT</font></h4> use case:</center>";//// $NON-NLS-N$
		else
			header = "<center><h4><font color='#00ffff'>NEW</font></h4> use case:</center>";//// $NON-NLS-N$

		getMacro().setOSDReminderDetails(header + "<center>" //// $NON-NLS-N$
				+ candidate.getName() + "</center>\n\n");
		super.displayOSD();
	}

	@Override
	public int executeOnKeyUp(LuaEvent event) {
		UseCase candidate = nextUseCase();
		if (candidate != null) {
			targetApp.set___currentUseCase(candidate);
			if( ! OSDshown )
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					avvisaUtente(false);
				}
			});
			// Debug.print(targetApp.getCurrentUseCase(), 0, 3);
			return COMPLETE;
		}
		
		OSDshown = false;
		return PASS;
	}

	public UseCase nextUseCase() {

		targetApp = getMacro().getApplication();
		if (!targetApp.isReal()) {
			targetApp = JavaMacrosMemory.instance().getApplicationSet().fastFind(transientExeFile);
		}
		// Note: if invoke this on a app with no data, it is a bust!!!!
		if (targetApp == null)
			return null;

		UseCases useCases = targetApp.getUseCases();
		UseCase[] uses = doubleDeck(useCases);

		UseCase currentUseCase = targetApp.get___currentUseCase();

		if (currentUseCase == null)
			targetApp.set___currentUseCase(currentUseCase = useCases.getBasicUseCase());

		boolean found = false;
		int i, end, step;
		end = uses.length;

		if (direction == ChangeUseCaseDirection.NEXT) {
			i = 0;
			step = 1;
		} else {
			i = end - 1;
			step = -1;
		}
		UseCase candidate = null;
		for (; candidate == null && i < end && i >= 0; i += step) {
			UseCase useCase = uses[i];

			if (!found && useCase.equals(currentUseCase))
				found = true;
			else if (found && useCase.isManual() && useCase.isEnabled()) {
				candidate = useCase;
			}
		}
		return candidate;
	}

	public UseCase[] doubleDeck(UseCases useCases) {
		ArrayList<UseCase> l = useCases.asOrderedList();
		UseCase[] uses0 = new UseCase[l.size()];
		l.toArray(uses0);
		UseCase[] uses = new UseCase[l.size() + l.size()];
		System.arraycopy(uses0, 0, uses, 0, uses0.length);
		System.arraycopy(uses0, 0, uses, uses0.length, uses0.length);
		return uses;
	}

	@Override
	protected ConfigPanelCreator getPanelCreator() {
		ConfigPanelCreator panelCreator = super.getPanelCreator();
		panelCreator.DefaultSetters.setDefaultEnumRenderingType(RenderingType.HORIZONTAL_RADIOBUTTONS);
		return panelCreator;
	}

	@Override
	public String toString() {
		return direction == ChangeUseCaseDirection.NEXT ? "Switches to the next Manual use case available"
				: "Switches to the previous Manual use case available";
	}

	@Override
	public Executor copyMe() {
		CHANGE_USE_CASE change_USE_CASE = new CHANGE_USE_CASE();
		change_USE_CASE.direction = this.direction;
		return change_USE_CASE;
	}
}
