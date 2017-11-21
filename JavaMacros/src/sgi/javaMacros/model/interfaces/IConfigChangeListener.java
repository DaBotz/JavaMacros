package sgi.javaMacros.model.interfaces;

import sgi.javaMacros.model.events.ConfigChangeEvent;

public interface IConfigChangeListener {
  public void handleConfigChangeEvent(ConfigChangeEvent event);
}
