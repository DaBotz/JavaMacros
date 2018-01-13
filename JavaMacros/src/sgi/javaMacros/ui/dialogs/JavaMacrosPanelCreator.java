package sgi.javaMacros.ui.dialogs;

import java.awt.Window;
import java.io.File;
import java.lang.reflect.Field;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import sgi.gui.ConfigPanelCreator;
import sgi.gui.RichFileIconer;
import sgi.localization.IMessages;

public class JavaMacrosPanelCreator extends ConfigPanelCreator {

	public JavaMacrosPanelCreator(IMessages msgs) {
		super(msgs);
	}

	public JavaMacrosPanelCreator(IMessages msgs, String msgsPrefix) {
		super(msgs, msgsPrefix);
	}

	public JavaMacrosPanelCreator(IMessages msgs, String msgsPrefix, Window targetWindow) {
		super(msgs, msgsPrefix, targetWindow);
		initCreator();
	}

	public void initCreator() {
		setUseFieldSeparators(true);		
	}

	protected void configureFileChooser(JFileChooser chooser, File file, Field field, Object object) {
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setFileFilter(new FileFilter() {
	
			@Override
			public String getDescription() {
				return "Only .gif, .png  and .jpg files";
			}
	
			@Override
			public boolean accept(File f) {
				if (f.isDirectory())
					return true;
				String ext = f.getName().replaceAll(".*(\\.(\\w{3,4}))", "$2").toLowerCase();
				if ("png".equals(ext))
					return true;
				if ("jpg".equals(ext))
					return true;
				if ("jpeg".equals(ext))
					return true;
				if ("gif".equals(ext))
					return true;
	
				return false;
			}
		});
	
		chooser.setFileView(new RichFileIconer(RichFileIconer.BIG));
	
	}

}