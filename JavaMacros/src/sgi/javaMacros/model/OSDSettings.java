package sgi.javaMacros.model;

import java.awt.Color;

import sgi.configuration.ConfigurationAtom;
import sgi.javaMacros.model.enums.OSDMode;

public class OSDSettings extends ConfigurationAtom {
	public static class DisplaySettings {
		private boolean positionRelativeToMouse;
		private int x;
		private int y;
		private Color foreground; 
		private Color background; 
		private Color border; 
		
		public Color getBorder() {
			return border;
		}

		public Color getForeground() {
			return foreground;
		}

		public Color getBackground() {
			return background;
		}


		public boolean isPositionRelativeToMouse() {
			return positionRelativeToMouse;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		private DisplaySettings(OSDSettings systemSettings) {
			this.positionRelativeToMouse = systemSettings.positionRelativeToMouse;
			this.x = systemSettings.x;
			this.y = systemSettings.y;
			this.foreground = systemSettings.foreground;
			this.border = systemSettings.border;
			this.background = new Color(
					systemSettings.background.getRed(),
					systemSettings.background.getGreen(),
					systemSettings.background.getBlue(),
					systemSettings.backGroundAlpha*255/100
					
					);
			this.y = systemSettings.y;

		}

	}

	private int backGroundAlpha=100; 
	private Color background=new Color(30, 30, 200);  
	private Color foreground= Color.WHITE; 
	private Color border= Color.YELLOW; 
	private OSDMode oSDMode = OSDMode.ALLOWED_TO_MACRO;
	private boolean positionRelativeToMouse = true;
	private int x;
	private int y;
	private int iconSize = 32;
	
	public int getBackGroundAlpha() {
		return backGroundAlpha;
	}

	public Color getBorder() {
		return border;
	}

	public Color getForeground() {
		return foreground;
	}

	public Color getBackground() {
		return background;
	}

	public OSDMode getoSDMode() {
		return oSDMode;
	}

	public void setX(int x) {
		int oldX = this.x;
		this.x = x;
		notifyPropertyChange("x", oldX, this.x);
	}

	public void setY(int y) {
		int oldY = this.y;
		this.y = y;
		notifyPropertyChange("y", oldY, this.y);
	}

	public boolean isPositionRelativeToMouse() {
		return positionRelativeToMouse;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public boolean isOsdRequired(boolean macroWantsIt, int executorRV) {
		if (oSDMode == null)
			oSDMode = OSDMode.FORCED_ON_FAILS;
		return oSDMode.isOSDrequired(macroWantsIt, executorRV);
	}

	public DisplaySettings getDisplaySettings() {

		return new DisplaySettings(this);
	}

	public int getIconSize() {
		return iconSize;
	}
}
