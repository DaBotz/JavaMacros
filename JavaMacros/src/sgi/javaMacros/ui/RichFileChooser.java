package sgi.javaMacros.ui;

import java.io.File;
import java.net.URL;
import java.util.HashMap;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;

public class RichFileChooser extends JFileChooser {

	private int mode = 0;

	public RichFileChooser(int mode) {
		super();
		this.mode = mode;
	}

	/**
	 * 
	 */
	public RichFileChooser() {

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 4757634039871826469L;

	public static final int BIG = 1;

	public static final int SMALL = 0;

	private HashMap<String, Icon> knowntypes = new HashMap<String, Icon>();

	public Icon getIcon(File f) {
		Icon fileIcon = getFileIcon(f, mode);
		if( fileIcon != null )	return fileIcon;
		
		
		if (f.isFile()) {
			Icon storedIcon = getStoredIcon(f);
			if (storedIcon!= null )return storedIcon;
		}
		return super.getIcon(f);
	}

	public static Icon getFileIcon(File f, int mode2) {
		sun.awt.shell.ShellFolder sf;
		try {
			switch (mode2) {
			case BIG:
				sf = sun.awt.shell.ShellFolder.getShellFolder(f);
				return new ImageIcon(sf.getIcon(true));
			case SMALL:
			default:
				return FileSystemView.getFileSystemView().getSystemIcon(f);

			}
		} catch (Throwable e) {
			return null;
		}

	}

	private Icon getStoredIcon(File f) {
		String ext = f.getName().replaceAll("^(.*\\.)", "").toLowerCase();

		if (getKnowntypes().containsKey(ext)) {
			return getKnowntypes().get(ext);
		}

		URL resource = this.getClass().getResource("icons/" + ext + ".gif");
		if (resource == null)
			resource = this.getClass().getResource("icons/default.gif");
		if (resource != null) {
			ImageIcon imageIcon = new ImageIcon(resource);
			getKnowntypes().put(ext, imageIcon);
			return imageIcon;
		}
		return null;
	}

	public final int getMode() {
		return mode;
	}

	public final void setMode(int mode) {
		this.mode = mode;
	}

	private HashMap<String, Icon> getKnowntypes() {
		if (knowntypes == null)
			knowntypes = new HashMap<String, Icon>();
		return knowntypes;
	}
}