package sgi.javaMacros.ui.dialogs;

import java.awt.Dimension;
import java.awt.HeadlessException;

import javax.swing.JPanel;

import sgi.gui.ConfigPanelCreator;
import sgi.javaMacros.model.JavaMacrosConfiguration;
import sgi.javaMacros.model.JavaMacrosMemory;
import sgi.javaMacros.model.OSDSettings;
import sgi.javaMacros.msgs.Messages;
import sgi.javaMacros.ui.ConfigFrame;

public class JavaMAcrosCoreConfigurationDlg extends ConfigFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7697988091L;
	private JavaMacrosConfiguration mainConfiguration;

	public JavaMAcrosCoreConfigurationDlg(JavaMacrosConfiguration mainConfiguration) throws HeadlessException {
		super();
		this.mainConfiguration = mainConfiguration;
		Messages m = Messages.M;
		setTitle(m._$("JavaMAcrosCoreConfigurationDlg.title"));
		setIconImage(m.getIcon());
		ConfigPanelCreator creator = new ConfigPanelCreator(m, "JavaMAcrosCoreConfigurationDlg");
		creator.setMainPageColumnWidths(10, 40, 2, 80, 10);
		creator.setTargetWindow(this);
		creator.FieldsSettings.setInvisibleFields("listeners");
		creator.FieldsSettings.setMaxValue(JavaMacrosMemory.class, "serverPort", 65536L);
		creator.FieldsSettings.setMinValue(JavaMacrosMemory.class, "serverPort", 5000L);
		
		creator.FieldsSettings.setMaxValue(OSDSettings.class, "x", 10_000);
		creator.FieldsSettings.setMinValue(OSDSettings.class, "x",-10_000);
		creator.FieldsSettings.setMaxValue(OSDSettings.class, "y", 10_000);
		creator.FieldsSettings.setMinValue(OSDSettings.class, "y", -10_000);
		creator.FieldsSettings.setMinValue(OSDSettings.class, "backGroundAlpha", 0);
		creator.FieldsSettings.setMaxValue(OSDSettings.class, "backGroundAlpha", 100);
		
		
		creator.setUseFieldSeparators(true);
		creator.setUseScrollPanel(true);
		creator.setAddingEndButtons(false);

		
		creator.DefaultSetters.setDefaultComboBoxWidth(150);
		creator.DefaultSetters.setDefaultCompositeFieldViewSize(new Dimension(200, 70));

		creator.setSpacerHeight(1);

		JPanel mainPanel = creator.createConfigPanel(mainConfiguration);
		setContentPane(mainPanel);
		pack();
	}

	@Override
	public boolean setSaved(boolean saved) {
		if (true)
			mainConfiguration.storeToFile();

		return super.setSaved(saved);
	}
}