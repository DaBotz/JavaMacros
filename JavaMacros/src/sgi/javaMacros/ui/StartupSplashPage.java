package sgi.javaMacros.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.Timer;
import javax.swing.border.LineBorder;

import sgi.generic.screenutilities.ScreenBoundariesChecker;
import sgi.javaMacros.msgs.Messages;
import sgi.javaMacros.ui.icons.Icons;

public class StartupSplashPage extends JWindow implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1325996480312626553L;
	private JPanel contentPane;
	private JLabel messagesDisplay;

	Object lock = "";

	String displayText;

	public String getDisplayText() {
		synchronized (lock) {
			String displayText2 = displayText;
			displayText = null;
			return displayText2;
		}
	}

	public void setDisplayText(String displayText) {
		synchronized (lock) {
			this.displayText = displayText;
		}
	}

	@Override
	public void setOpacity(float opacity) {
		opacity = Math.max(0, Math.min(maxOpacity, opacity));
		super.setOpacity(opacity);
	}

	public void setMaxOpacity(float opacity) {
		maxOpacity = Math.max(0.4f, Math.min(1, opacity));
	}

	private transient Timer timer;
	private float step;
	private float maxOpacity = 0.96f;

	@Override
	public void actionPerformed(ActionEvent e) {

		String displayText2 = getDisplayText();
		if (displayText2 != null) {
			messagesDisplay.setText(displayText2);
		}
		synchronized (lock) {
			setOpacity(getOpacity() + step);
		}

		if (getOpacity() == 0) {
			super.setVisible(false);
			timer.stop();
			dispose();
		}
	}

	@Override
	public void setVisible(boolean b) {
		if (b && !isVisible()) {

			setOpacity(0.01f);
			if (timer == null) {
				timer = new Timer(40, this);
				timer.start();
			}
			super.setVisible(b);
			step = maxOpacity / 25;
		}

		if (!b)
			synchronized (lock) {
				step = 0 - maxOpacity / 30;
			}
	}

	/**
	 * Create the frame.
	 */
	public StartupSplashPage() {
		ImageIcon icon = Icons.getIcon("splashpage");
		final Image image = icon.getImage();
		int width2 = image.getWidth(null);
		int height2 = image.getHeight(null);

		contentPane = new JPanel(new BorderLayout()) {
			/**
			 * 
			 */
			private static final long serialVersionUID = -1710187512128169901L;

			@Override
			public void paint(Graphics g) {

				if ((getWidth() <= 0) || (getHeight() <= 0)) {
					return;
				}
				int width3 = image.getWidth(null);
				int height3 = image.getHeight(null);

				Graphics componentGraphics = getComponentGraphics(g);
				Graphics co = componentGraphics.create();
				try {

					co.drawImage(image, 0, 0, width3, height3, 0, 0, width3, height3, null);

					printChildren(co);

				} catch (Throwable e) {
				} finally {
					co.dispose();
				}

			}
		};

		Color bg = new Color(246, 244, 210);
		contentPane.setLayout(new BorderLayout(0, 0));

		contentPane.setBackground(bg);

		contentPane.setMinimumSize(new Dimension(width2, height2));
		contentPane.setPreferredSize(new Dimension(width2, height2));
		// JLabel comp = new JLabel(icon);
		// comp.setMinimumSize(new Dimension(width2, height2));

		// contentPane.add(comp, BorderLayout.CENTER);

		messagesDisplay = new JLabel(" ");
		Color darken = darken(bg, 0.6);
		messagesDisplay.setForeground(darken);
		Font deriveFont = messagesDisplay.getFont().deriveFont(Font.ITALIC).deriveFont(16f);
		messagesDisplay.setFont(deriveFont);
		messagesDisplay.setHorizontalTextPosition(JLabel.CENTER);
		messagesDisplay.setHorizontalAlignment(JLabel.CENTER);

		messagesDisplay.setMinimumSize(new Dimension(width2, 20));
		contentPane.add(messagesDisplay, BorderLayout.SOUTH);
		// JPanel middle = new JPanel(new GridLayout(1, 2, 4, 20));
		// middle.setBackground(null);
		// middle.setOpaque(false);
		//
		// middle.add(new JLabel());
		JLabel lNote = new JLabel();
		// middle.add(lNote);

		InputStream resourceAsStream = Messages.class.getResourceAsStream("copyleft-note");
		if (resourceAsStream != null) {
			lNote.setForeground(darken);
			lNote.setFont(deriveFont.deriveFont(11f));

			try {
				StringBuffer b = new StringBuffer();
				BufferedReader bReader = new BufferedReader(new InputStreamReader(resourceAsStream));
				String line = bReader.readLine();
				while (line != null) {
					b.append(line);
					b.append("\n");

					line = bReader.readLine();
				}
				if (b.indexOf("<html") < 0) {
					lNote.setText(Messages.htmlSwing(b.toString()));
				} else
					lNote.setText((b.toString()));

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		contentPane.add(lNote, BorderLayout.EAST);

		JPanel outer = new JPanel(new BorderLayout());
		outer.setBorder(new LineBorder(darken(bg, 0.90), 3));
		outer.add(contentPane, BorderLayout.CENTER);
		setContentPane(outer);

		pack();
		int x = 80, y = 80;
		Rectangle[] monitors = ScreenBoundariesChecker.getInstance().getAllScreenBoundaries();
		for (int i = 0; i < monitors.length; i++) {
			Rectangle rectangle = monitors[i];
			if (rectangle.x == 0 && rectangle.y == 0) {
				x = (rectangle.width - getWidth()) / 2;
				y = (rectangle.height - getHeight()) / 2;
			}
		}
		setLocation(x, y);
	}

	private Color darken(Color bg, double d) {
		int red = (int) (bg.getRed() * d);
		int green = (int) (bg.getGreen() * d);
		int blue = (int) (bg.getBlue() * d);

		// TODO Auto-generated method stub
		return new Color(red, green, blue);
	}

	public void printMessage(String msg) {
		messagesDisplay.setText(msg);
	}

}
