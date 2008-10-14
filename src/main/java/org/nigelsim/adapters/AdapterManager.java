package org.nigelsim.adapters;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import sun.reflect.annotation.AnnotationParser;

/**
 * This adapter manager is inspired by the Zope 3 component architecture
 * and http://martinaspeli.net/articles/a-java-component-architecture
 * @author nigel
 *
 */
public class AdapterManager {
	private static AdapterManager adapterManager = new AdapterManager();
	private static Map<Class, Map<Class, Class>> interfaceMap;
	
	private AdapterManager() {
		interfaceMap = new HashMap<Class, Map<Class, Class>>();
	}
	
	/**
	 * Register the class adapter.
	 * @param iface
	 * @param obj
	 * @param factory
	 */
	public void register(Class iface, Class obj, Class factory) throws NotAnAdapterException {
		Map<Class, Class> factories;
		if (!interfaceMap.containsKey(iface)) {
			factories = new HashMap<Class, Class>();
			interfaceMap.put(iface, factories);
		} else {
			factories = interfaceMap.get(iface);
		}
		factories.put(obj, factory);
	}
	
	/**
	 * Register the class adapter.
	 * @param iface
	 * @param obj
	 * @param factory
	 */
	public void register(Class factory) throws NotAnAdapterException {
		Adapter annotation = (Adapter)factory.getAnnotation(Adapter.class);
		if (annotation != null) {
			Class obj = annotation.forClass();
			
			for (Class iface: factory.getInterfaces()) {
				register(iface, obj, factory);
			}
			
		} else {
			throw new NotAnAdapterException("Annotation not present");
		}
	}
	
	/**
	 * Get an adapter to the given interface for the object.
	 * @param iface
	 * @param obj
	 * @return
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Object adapt(Class iface, Object obj) throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		Map<Class, Class> factories = interfaceMap.get(iface);
		if (factories != null) {
			Class factory = factories.get(obj.getClass());
			Constructor constructor = factory.getConstructor(obj.getClass());
			return constructor.newInstance(obj);
		} else {
			return null;
		}
	}
	
	public static AdapterManager getInstance() {
		return adapterManager;
	}
}
