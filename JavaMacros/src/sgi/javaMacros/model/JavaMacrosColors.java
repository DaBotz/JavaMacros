package sgi.javaMacros.model;

import java.awt.Color;

import sgi.configuration.ConfigurationAtom;

public class JavaMacrosColors extends ConfigurationAtom {

	private Color _absentColor;
	private Color _disabledColor;
	private Color _modifiedColor;
	private Color _enabledColor;

	private Color groupsColor;
	private Color escapesColor;
	private Color modifiersColor;
	private Color errorsColor;
	private Color normalScriptColor;
	private Color luamacrosEditorCTSmodeBackground;

	protected Color get_absentColor() {
		return _absentColor;
	}



	
	public void setErrorsColor(Color errorsColor) {
		Color oldcolor = this.errorsColor;
		this.errorsColor = errorsColor;
		notifyPropertyChange("errorsColor", oldcolor, errorsColor);
	}

	public void setGroupsColor(Color groupsColor) {
		Color oldcolor = this.groupsColor;
		this.groupsColor = groupsColor;
		notifyPropertyChange("groupsColor", oldcolor, groupsColor);
	}

	public Color getGroupsColor() {
		return groupsColor;
	}

	public Color getEscapesColor() {
		return escapesColor;
	}

	public Color getModifiersColor() {
		return modifiersColor;
	}

	public Color getErrorsColor() {
		return errorsColor;
	}

	public void setModifiersColor(Color modifiersColor) {
		Color oldcolor = this.modifiersColor;
		this.modifiersColor = modifiersColor;
		notifyPropertyChange("modifiersColor", oldcolor, modifiersColor);
	}

	public void setEscapesColor(Color escapesColor) {
		Color oldcolor = this.escapesColor;
		this.escapesColor = escapesColor;
		notifyPropertyChange("escapesColor", oldcolor, escapesColor);
	}

	public void setNormalScriptColor(Color normalScriptColor) {
		Color oldcolor = this.escapesColor;
		this.normalScriptColor = normalScriptColor;
		notifyPropertyChange("normalScriptColor", oldcolor, normalScriptColor);
	}

	public Color get_enabledColor() {
		return _enabledColor;
	}

	public Color get_disabledColor() {
		return _disabledColor;
	}

	public Color get_modifiedColor() {
		return _modifiedColor;
	}

	public void set_enabledColor(Color _enabledColor) {
		Color oldcolor = this._enabledColor;
		this._enabledColor = _enabledColor;
		notifyPropertyChange("enabledColor", oldcolor, _enabledColor);
		setEnabledColor(_enabledColor);
	}

	public void set_disabledColor(Color _disabledColor) {
		Color oldcolor = this._disabledColor;
		this._disabledColor = _disabledColor;
		notifyPropertyChange("disabledColor", oldcolor, _disabledColor);
		setDisabledColor(_disabledColor);

	}
	public void set_absentColor(Color _absentColor) {
		Color oldcolor = this._absentColor;
		this._absentColor = _absentColor;
		notifyPropertyChange("absentColor", oldcolor, _absentColor);
		setAbsentColor(_absentColor);

	}

	public void set_modifiedColor(Color _modifiedColor) {
		Color oldcolor = this._modifiedColor;
		this._modifiedColor = _modifiedColor;
		notifyPropertyChange("modifiedColor", oldcolor, _modifiedColor);
		setModifiedColor(_modifiedColor);
	}

	public void alignStatics() {
		setAbsentColor(_absentColor);
		setDisabledColor(_disabledColor);
		setModifiedColor(_modifiedColor);
	}

	public void loadPreSets() {
		set_enabledColor(new Color(200, 255, 200));
		set_disabledColor(new Color(255, 200, 200));
		set_absentColor(new Color(230,230, 230));
		set_modifiedColor(Color.YELLOW);

		setErrorsColor(Color.RED);
		setModifiersColor(new Color(44, 88, 44));
		setEscapesColor(Color.BLUE);
		setGroupsColor(new Color(44, 44, 0));
		setNormalScriptColor(Color.BLACK);
		setLuamacrosEditorCTSmodeBackground(new Color(255, 255, 222)); 

	}

	protected void setLuamacrosEditorCTSmodeBackground(Color luamacrosEditorCTSmodeBackground) {
		Color oldvalue = this.luamacrosEditorCTSmodeBackground;
		this.luamacrosEditorCTSmodeBackground = luamacrosEditorCTSmodeBackground;

		notifyPropertyChange("luamacrosEditorCTSmodeBackground", oldvalue, luamacrosEditorCTSmodeBackground);
	}

	public Color getNormalScriptColor() {
		return normalScriptColor;
	}

	public Color getLuamacrosEditorCTSmodeBackground() {

		return luamacrosEditorCTSmodeBackground;
	}

}
