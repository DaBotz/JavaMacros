package sgi.javaMacros.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;

import sgi.javaMacros.StringConstants;

public class ButtonConfigurer {

	private String iconExtension = "png";

	public String getIconExtension() {
		return iconExtension;
	}

	public void setIconExtension(String iconExtension) {
		this.iconExtension = iconExtension;
	}

	private transient ArrayList<Component> crawledComponents;

	public void configure(Component comp){
		crawledComponents = new ArrayList<>(); 
		configureAllButtons( comp);
		crawledComponents = null; 
	}
	
	private void configureAllButtons(Component comp) {
		if (comp == null)
			return;

		if (crawledComponents.contains(comp))
			return;

		crawledComponents.add(comp);

		if (comp instanceof AbstractButton) {
			doconfigure((AbstractButton) comp);
		}
		if (comp instanceof Container) {
			Container cnt = ((Container) comp);

			Component[] comps = cnt.getComponents();
			for (int i = 0; i < comps.length; i++) {
				configureAllButtons(comps[i]);
			}
		}
	}

	private String pathToIcons = "ui/icons/";
	private ActionListener listener;

	public void setPathToIcons(String pathToIcons) {
		this.pathToIcons = pathToIcons;
	}

	public String getPathToIcons() {
		return pathToIcons;
	}

	private void doconfigure(AbstractButton btnNewButton) {
		String actName = btnNewButton.getActionCommand();
		if (actName == null || "".equals(actName))
			return;
		if (listener != null)
			btnNewButton.addActionListener(listener);
		String ext = "." + getIconExtension();
		String path = getPathToIcons();

		URL resource = StringConstants.class.getResource(path + actName + ext);

		if (resource != null) {

			btnNewButton.setIcon(new ImageIcon(resource));

			resource = StringConstants.class.getResource(path + actName + "_pressed" + ext);

			if (resource != null) {
				btnNewButton.setPressedIcon(new ImageIcon(resource));

			}

			resource = StringConstants.class.getResource(path + actName + "_disabled" + ext);

			if (resource != null) {
				btnNewButton.setDisabledIcon(new ImageIcon(resource));
			}

		}
	}

	public ButtonConfigurer(ActionListener listener) {
		super();
		this.listener = listener;
	}

}