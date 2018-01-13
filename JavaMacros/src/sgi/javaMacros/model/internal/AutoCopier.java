package sgi.javaMacros.model.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import sgi.gui.configuration.IAwareOfChanges;

public interface AutoCopier<T> extends IAwareOfChanges {

	int NOCOPY = Modifier.STATIC | Modifier.TRANSIENT;

	@SuppressWarnings("unchecked")
	default public T copy(boolean preserveTransient) throws InstantiationException, IllegalAccessException {
		AutoCopier<?> copy = getClass().newInstance();
		Field[] fields = getFields(getClass());
		for (Field f : fields) {
			f.setAccessible(true);
			if ((f.getModifiers() & (preserveTransient ? Modifier.STATIC : NOCOPY)) == 0) {
				Class<?> type = f.getType();
				Object value = f.get(this);
				if (value == null) {
					f.set(copy, value);
				} else if (AutoCopier.class.isAssignableFrom(type)) {
					f.set(copy, ((AutoCopier<?>) value).copy(preserveTransient));
				} else {
					f.set(copy, value);
				}
			}
			if (this instanceof Collection) {
				Collection<Object> coll = (Collection<Object>) this;
				Collection<Object> dest = (Collection<Object>) copy;
				for (Object object : coll) {

					dest.add(copyThis(object, preserveTransient));
				}
			}

			if (this instanceof Map) {
				Map<Object, Object> coll = (Map<Object, Object>) this;
				Map<Object, Object> dest = (Map<Object, Object>) copy;
				Set<Entry<Object, Object>> entrySet = coll.entrySet();
				for (Entry<Object, Object> entry : entrySet) {
					dest.put( //
							copyThis(entry.getKey(), preserveTransient), //
							copyThis(entry.getValue(), preserveTransient)//
					);

				}
			}

		}

		return (T) copy;
	}

	public default Object copyThis(Object object, boolean preserveTransient)
			throws InstantiationException, IllegalAccessException {
		Object aCopyOfMe = object;
		if (object instanceof AutoCopier<?>) {
			aCopyOfMe = ((AutoCopier<?>) object).copy(preserveTransient);
		}
		return aCopyOfMe;
	}

}
