package com.citi.logparser.parser;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.citi.logparser.filters.CommandFilter;
import com.citi.logparser.output.OutputWriter;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

public class LogParser {

	private Map<String, Class<CommandFilter>> commandFilters = Collections.emptyMap();
	private Map<String, Class<OutputWriter>> outputWriters = Collections.emptyMap();

	public void importCommandFilters(Collection<String> importSpecs) {
		if (commandFilters == Collections.EMPTY_MAP) { // intentionally
														// effective no more
														// than once
			commandFilters = new HashMap<String, Class<CommandFilter>>();
			Collection<Class<CommandFilter>> builderClasses = getTopLevelClasses(
					importSpecs, CommandFilter.class);
			for (Class<CommandFilter> builderClass : builderClasses) {
				try {
					CommandFilter builder = builderClass.newInstance();
					String builderName = builder.getName();
					commandFilters.put(builderName, builderClass);

				} catch (Exception e) {
				}
			}
		}
	}

	public void importOutputWriters(Collection<String> importSpecs) {
		if (outputWriters == Collections.EMPTY_MAP) { // intentionally
														// effective no more
														// than once
			outputWriters = new HashMap<String, Class<OutputWriter>>();
			Collection<Class<OutputWriter>> builderClasses = getTopLevelClasses(
					importSpecs, OutputWriter.class);
			for (Class<OutputWriter> builderClass : builderClasses) {
				try {
					OutputWriter builder = builderClass.newInstance();
					String builderName = builder.getName();
					outputWriters.put(builderName, builderClass);

				} catch (Exception e) {
				}
			}
		}
	}

	public Class<CommandFilter> getCommandBuilder(String builderName) {
		return commandFilters.get(builderName);
	}

	<T> Collection<Class<T>> getTopLevelClasses(Iterable<String> importSpecs,
			Class<T> iface) {
		HashMap<String, Class<T>> classes = new LinkedHashMap();
		for (ClassLoader loader : getClassLoaders()) {
			ClassPath classPath;
			try {
				classPath = ClassPath.from(loader);
			} catch (IOException e) {
				continue;
			}
			for (String importSpec : importSpecs) {
				Set<ClassInfo> classInfos = null;
				if (importSpec.endsWith(".**")) {
					String packageName = importSpec.substring(0,
							importSpec.length() - ".**".length());
					classInfos = classPath
							.getTopLevelClassesRecursive(packageName);
				} else if (importSpec.endsWith(".*")) {
					String packageName = importSpec.substring(0,
							importSpec.length() - ".*".length());
					classInfos = classPath.getTopLevelClasses(packageName);
				} else { // importSpec is assumed to be a fully qualified class
							// name
					Class clazz;
					try {
						// clazz = Class.forName(importSpec, true, loader);
						clazz = loader.loadClass(importSpec);
					} catch (ClassNotFoundException e) {
						continue;
					}
					addClass(clazz, classes, iface);
					continue;
				}

				for (ClassInfo info : classInfos) {
					Class clazz;
					try {
						clazz = info.load();
						// clazz = Class.forName(info.getName());
					} catch (NoClassDefFoundError e) {
						continue;
					} catch (ExceptionInInitializerError e) {
						continue;
					} catch (UnsatisfiedLinkError e) {
						continue;
					}
					addClass(clazz, classes, iface);
				}
			}
		}
		return classes.values();
	}

	private <T> void addClass(Class clazz, HashMap<String, Class<T>> classes,
			Class<T> iface) {
		if (!classes.containsKey(clazz.getName())
				&& iface.isAssignableFrom(clazz) && !clazz.isInterface()
				&& !Modifier.isAbstract(clazz.getModifiers())) {
			for (Constructor ctor : clazz.getConstructors()) { // all public
																// ctors
				if (ctor.getParameterTypes().length == 0) { // is public
															// zero-arg ctor?
					classes.put(clazz.getName(), clazz);
				}
			}
		}
	}

	private ClassLoader[] getClassLoaders() {
		ClassLoader contextLoader = Thread.currentThread()
				.getContextClassLoader();
		ClassLoader myLoader = getClass().getClassLoader();
		if (contextLoader == null) {
			return new ClassLoader[] { myLoader };
		} else if (contextLoader == myLoader || myLoader == null) {
			return new ClassLoader[] { contextLoader };
		} else {
			return new ClassLoader[] { contextLoader, myLoader };
		}
	}

	public static void main(String[] args) {
		LogParser obj = new LogParser();
		List<String> importFilters = new ArrayList<String>();
		importFilters.add("com.citi.logparser.filters.**");
		List<String> importOutputWriters = new ArrayList<String>();
		importOutputWriters.add("com.citi.logparser.output.**");
		obj.importCommandFilters(importFilters);
		obj.importOutputWriters(importOutputWriters);
	}

}
