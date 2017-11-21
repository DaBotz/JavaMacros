package sgi.javaMacros.model.interfaces;

public interface IConfigAtom{
	public IConfigAtom getParent() ;
	public void setParent(IConfigAtom parent);
	public void addConfigChangeListener(IConfigChangeListener listener);
}