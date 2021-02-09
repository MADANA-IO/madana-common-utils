package com.madana.common.utils.datacollection.questionaires;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.madana.common.utils.datacollection.questionaires.answers.SingleSelectAnswer;

public class QuestionFactory 
{
	public Question createQuestion(String className, String question)
	{
		
		Class<?> c;
		try {
			c = Class.forName("com.madana.core.utils.datacollection.questionaires.questions."+className);
			Constructor<?> cons = c.getConstructor(String.class);
			Question object = (Question) cons.newInstance(question);
			
			//Setting types for serialization
			object.setType(object.getClass().getSimpleName());
			object.getAnswer().setType(object.getAnswerType().getClass().getSimpleName());
	
			
		
			return object;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
		return null;
	}

	public Question createQuestion(String className, String questionText, List<String> answerOptions) {
		Question question = createQuestion(className,questionText);
		if(question.getAnswerType() instanceof SingleSelectAnswer)
		{
			((SingleSelectAnswer) question.getAnswer()).setOptions(answerOptions);
		}
		
		return question;
	}
	public List<Question> getAvailableQuestionTypes()
	{
		List<Question> list = new ArrayList<Question>();
		try
		{
			ArrayList<Class<?>> classes =getClassesForPackage("com.madana.core.utils.datacollection.questionaires.questions");
			if(classes==null)
				return null;
			else
			{
				for(int i=0; i < classes.size(); i++)
				{
					Class<?> currentClass = classes.get(i);
					if (! Modifier.isAbstract( currentClass.getModifiers() ))		 // Only instantiate non abstract classes
					{
					

					
							try {
								Question object =(Question) currentClass.newInstance();
								//Setting types for serialization
								object.setType(object.getClass().getSimpleName());
								object.getAnswer().setType(object.getAnswerType().getClass().getSimpleName());
								list.add(object);
							} catch (Exception e) {
						
							}
					}


				}
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	private static ArrayList<Class<?>> getClassesForPackage(String pckgname)
			throws ClassNotFoundException {
		final ArrayList<Class<?>> classes = new ArrayList<Class<?>>();

		try {
			final ClassLoader cld = Thread.currentThread()
					.getContextClassLoader();

			if (cld == null)
				throw new ClassNotFoundException("Can't get class loader.");

			final Enumeration<URL> resources = cld.getResources(pckgname
					.replace('.', '/'));
			URLConnection connection;

			for (URL url = null; resources.hasMoreElements()
					&& ((url = resources.nextElement()) != null);) {
				try {
					connection = url.openConnection();

					if (connection instanceof JarURLConnection) {
						checkJarFile((JarURLConnection) connection, pckgname,
								classes);
					} else {
						try {
							checkDirectory(
									new File(URLDecoder.decode(url.getPath(),
											"UTF-8")), pckgname, classes);
						} catch (final UnsupportedEncodingException ex) {
							throw new ClassNotFoundException(
									pckgname
									+ " does not appear to be a valid package (Unsupported encoding)",
									ex);
						}
					} 
				} catch (final IOException ioex) {
					throw new ClassNotFoundException(
							"IOException was thrown when trying to get all resources for "
									+ pckgname, ioex);
				}
			}
		} catch (final NullPointerException ex) {
			throw new ClassNotFoundException(
					pckgname
					+ " does not appear to be a valid package (Null pointer exception)",
					ex);
		} catch (final IOException ioex) {
			throw new ClassNotFoundException(
					"IOException was thrown when trying to get all resources for "
							+ pckgname, ioex);
		}

		return classes;
	}
	/**
	 * Private helper method
	 * 
	 * @param directory
	 *            The directory to start with
	 * @param pckgname
	 *            The package name to search for. Will be needed for getting the
	 *            Class object.
	 * @param classes
	 *            if a file isn't loaded but still is in the directory
	 * @throws ClassNotFoundException
	 */
	private static void checkDirectory(File directory, String pckgname,
			ArrayList<Class<?>> classes) throws ClassNotFoundException {
		File tmpDirectory;

		if (directory.exists() && directory.isDirectory()) {
			final String[] files = directory.list();

			for (final String file : files) {
				if (file.endsWith(".class")) {
					try {
						classes.add(Class.forName(pckgname + '.'
								+ file.substring(0, file.length() - 6)));
					} catch (final NoClassDefFoundError e) {
						// do nothing. this class hasn't been found by the
						// loader, and we don't care.
					}
				} else if ((tmpDirectory = new File(directory, file))
						.isDirectory()) {
					checkDirectory(tmpDirectory, pckgname + "." + file, classes);
				}
			}
		}
	}

	/**
	 * Private helper method.
	 * 
	 * @param connection
	 *            the connection to the jar
	 * @param pckgname
	 *            the package name to search for
	 * @param classes
	 *            the current ArrayList of all classes. This method will simply
	 *            add new classes.
	 * @throws ClassNotFoundException
	 *             if a file isn't loaded but still is in the jar file
	 * @throws IOException
	 *             if it can't correctly read from the jar file.
	 */
	private static void checkJarFile(JarURLConnection connection,
			String pckgname, ArrayList<Class<?>> classes)
					throws ClassNotFoundException, IOException {
		final JarFile jarFile = connection.getJarFile();
		final Enumeration<JarEntry> entries = jarFile.entries();
		String name;

		for (JarEntry jarEntry = null; entries.hasMoreElements()
				&& ((jarEntry = entries.nextElement()) != null);) {
			name = jarEntry.getName();

			if (name.contains(".class")) {
				name = name.substring(0, name.length() - 6).replace('/', '.');

				if (name.contains(pckgname)) {
					classes.add(Class.forName(name));
				}
			}
		}
	}

}
