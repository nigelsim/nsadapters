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
import java.util.Enumeration;
import java.util.HashMap;
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
	private static Map<Class, Map<Class, Class>> interfaceMap;

	private AdapterManager() {
		interfaceMap = new HashMap<Class, Map<Class, Class>>();
	}

	/**
	 * Register the class adapter.
	 * 
	 * @param iface
	 * @param obj
	 * @param factory
	 */
	public void register(Class iface, Class obj, Class factory)
			throws NotAnAdapterException {
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
	 * 
	 * @param iface
	 * @param obj
	 * @param factory
	 */
	public void register(Class factory) throws NotAnAdapterException {
		Adapter annotation = (Adapter) factory.getAnnotation(Adapter.class);
		if (annotation != null) {
			Class obj = annotation.forClass();

			for (Class iface : factory.getInterfaces()) {
				register(iface, obj, factory);
			}

		} else {
			throw new NotAnAdapterException("Annotation not present");
		}
	}

	/**
	 * Scans for adapters on the classpath.
	 */
	public void scan(boolean inJars) {
		String packageName = this.getClass().getPackage().getName();
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
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Object adapt(Class iface, Object obj) throws SecurityException,
			NoSuchMethodException, IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
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
