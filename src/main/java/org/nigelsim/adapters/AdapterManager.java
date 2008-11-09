/**
 *  This file is part of nsAdapters.
 *
 *  nsAdapters is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  nsAdapters is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with nsAdapters.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.nigelsim.adapters;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * This adapter manager is inspired by the Zope 3 component architecture and
 * http://martinaspeli.net/articles/a-java-component-architecture
 * 
 * @author nigel
 * 
 */
public class AdapterManager {
	private static AdapterManager adapterManager = new AdapterManager();
	private static Map<Class, Map<String, List<Class>>> interfaceMap;

	private AdapterManager() {
		interfaceMap = new HashMap<Class, Map<String, List<Class>>>();
	}

	/**
	 * Register the class adapter.
	 * 
	 * @param iface
	 * @param obj
	 * @param factory
	 * @throws NotAnAdapterException 
	 */
	public void register(Class iface, Class factory) throws NotAnAdapterException {
		register(iface, "", factory);
	}
	
	public void register(Class iface, String name, Class factory)
			throws NotAnAdapterException {
		Map<String, List<Class>> factories;
		if (!interfaceMap.containsKey(iface)) {
			factories = new HashMap<String, List<Class>>();
			interfaceMap.put(iface, factories);
		} else {
			factories = interfaceMap.get(iface);
		}
		List<Class> namedFactories;
		if (factories.containsKey(name)) {
			namedFactories = factories.get(name);
		} else {
			namedFactories = new ArrayList<Class>();
			factories.put(name, namedFactories);
		}
		namedFactories.add(factory);
	}

	/**
	 * Register the class adapter.
	 * 
	 * @param iface
	 * @param obj
	 * @param factory
	 */
	public void register(Class factory) throws NotAnAdapterException {
		Adapter annotation = (Adapter) factory.getAnnotation(Adapter.class);
		if (annotation != null) {
			String name = annotation.name();

			for (Class iface : factory.getInterfaces()) {
				register(iface, name, factory);
			}

		} else {
			throw new NotAnAdapterException("Annotation not present");
		}
	}

	/**
	 * Scans for adapters on the classpath.
	 */
	public void scan(String packageName, boolean inJars) {
		String packagePath = packageName.replace('.', '/');
		URL[] classpath = ((URLClassLoader) ClassLoader.getSystemClassLoader())
				.getURLs();

		for (URL url : classpath) {
			File file;
			try {
				file = new File(url.toURI());
				if (file.getPath().endsWith(".jar") && inJars) {
					JarFile jarFile = new JarFile(file);
					for (Enumeration<JarEntry> entries = jarFile.entries(); entries
							.hasMoreElements();) {
						try {
							String entryName = (entries.nextElement())
									.getName();
							if (entryName
									.matches(packagePath + "/\\w*\\.class")) { // get
								// only
								// class
								// files
								// in
								// package
								// dir
								ClassLoader classLoader = new URLClassLoader(
										new URL[] { url });
								String className = entryName.replace('/', '.')
										.substring(0,
												entryName.lastIndexOf('.'));
								Class clazz = classLoader.loadClass(className);

								if (clazz.isAnnotationPresent(Adapter.class)) {
									register(clazz);
								}
							}
						} catch (ClassNotFoundException e) {
						} catch (NotAnAdapterException e) {
						}
					}
				} else { // directory
					File packageDirectory = new File(file.getPath() + "/"
							+ packagePath);
					if (packageDirectory.exists()) {
					for (File f : packageDirectory.listFiles()) {
						try {
							if (f.getPath().endsWith(".class")) {
								String className = packageName
										+ "."
										+ f.getName().substring(0,
												f.getName().lastIndexOf('.'));
								ClassLoader classLoader = new URLClassLoader(
										new URL[] { url });
								Class clazz = classLoader.loadClass(className);
								if (clazz.isAnnotationPresent(Adapter.class)) {
									register(clazz);
								}
							}
						} catch (ClassNotFoundException e) {
						} catch (NotAnAdapterException e) {
						}
					}
				}
				}

			} catch (URISyntaxException e1) {
			} catch (IOException e) {
			}
		}
	}

	/**
	 * Get an adapter to the given interface for the object.
	 * 
	 * @param iface
	 * @param obj
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws IllegalArgumentException 
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Object adapt(Class iface, Object ... arguments) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		return adapt(iface, "", arguments);
	}
	
	public Object adapt(Class iface, String name, Object ... arguments) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		Map<String, List<Class>> factories = interfaceMap.get(iface);
		if  (factories == null) 
			return null;
		List<Class> namedFactories = factories.get(name);
		if (namedFactories == null)
			return null;
		
		// Copy the signature out of the arguments
		Class[] signature = new Class[arguments.length];
		for (int i = 0 ; i < arguments.length ; i++) {
			signature[i] = arguments[i].getClass();
		}
		
		if (factories != null) {
			for (Class factory: namedFactories) {
				try {
					Constructor constructor = factory.getConstructor(signature);
					return constructor.newInstance(arguments);
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {}
				
			}
		}
		return null;
	}

	public static AdapterManager getInstance() {
		return adapterManager;
	}
}
