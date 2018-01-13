package sgi.javaMacros.model.macros.execution;

import java.lang.reflect.Field;

import javax.swing.text.JTextComponent;

import sgi.gui.configuration.IAwareOfChanges;
import sgi.gui.configuration.TextSaver;

public class NonDecoratingTextSaver extends TextSaver {
	public NonDecoratingTextSaver(IAwareOfChanges updatable, Field field, JTextComponent component) {
		super(updatable, field, component);
	}

	@Override
	protected void decorateComponent() {

	}
}