package sgi.javaMacros.ui.dialogs;

import java.awt.Window;

import javax.swing.JPanel;

import sgi.gui.ConfigPanelCreator;
import sgi.javaMacros.model.internal.UseCase;
import sgi.javaMacros.msgs.Messages;
import sgi.javaMacros.ui.ModalConfigDialog;

public class UseCaseConfigDialog extends ModalConfigDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8659752116946437504L;

	public UseCaseConfigDialog(Window window, ModalityType applicationModal) {
		super(window, applicationModal);
	}

	public void build(UseCase newCase) {
		Messages M = Messages.M;
		setTitle(M._$("UseCaseConfigDialog.title"));
		setIconImage(M.getIcon());
		ConfigPanelCreator creator = new JavaMacrosPanelCreator(M, "UseCaseConfigDialog", this);
		creator.FieldsSettings.setInvisibleFields("creationTime");
		creator.setAddingEndButtons(isUsingEndButtons());
		JPanel panel = creator.createConfigPanel(newCase);
		setContentPane(panel);
		pack();

	}

}