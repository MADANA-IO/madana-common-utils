/*******************************************************************************
 * Copyright (C) 2018 MADANA
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * @organization:MADANA
 * @author:Jean-Fabian Wenisch
 * @contact:dev@madana.io
 ******************************************************************************/
package com.madana.common.utils.handler;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO: Auto-generated Javadoc
/**
 * The Class FileHandler.
 */
public class FileHandler {

	/**
	 * Read the file and returns the byte array.
	 *
	 * @param file the file
	 * @return the bytes of the file
	 * @author J.-Fabian Wenisch
	 */
	private byte[] readFile(String file) {
		ByteArrayOutputStream bos = null;
		try {
			File f = new File(file);
			FileInputStream fis = new FileInputStream(f);
			byte[] buffer = new byte[1024];
			bos = new ByteArrayOutputStream();
			for (int len; (len = fis.read(buffer)) != -1;) {
				bos.write(buffer, 0, len);
			}
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
		} catch (IOException e2) {
			System.err.println(e2.getMessage());
		}
		return bos != null ? bos.toByteArray() : null;
	}

	/**
	 * Copy file.
	 *
	 * @param in  the in
	 * @param out the out
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @author J.-Fabian Wenisch
	 */
	public static void copyFile(File in, File out) throws IOException {
		FileChannel inChannel = null;
		FileChannel outChannel = null;
		try {
			inChannel = new FileInputStream(in).getChannel();
			outChannel = new FileOutputStream(out).getChannel();
			inChannel.transferTo(0, inChannel.size(), outChannel);
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				if (inChannel != null)
					inChannel.close();
				if (outChannel != null)
					outChannel.close();
			} catch (IOException e) {
			}
		}
	}

	/**
	 * Write to file.
	 *
	 * @param path the path
	 * @param key  the key
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @author J.-Fabian Wenisch
	 */
	public static void writeToFile(String path, byte[] key) throws IOException {
		File f = new File(path);
		f.getParentFile().mkdirs();

		FileOutputStream fos = new FileOutputStream(f);
		fos.write(key);
		fos.flush();
		fos.close();
	}
	/**
	 * 
	 * @param path
	 * @param inputStream
	 * @author J.-Fabian Wenisch
	 */
	public static void writeToFile(String path, InputStream inputStream) {

		try {
			File file = new File(path);
			file.getParentFile().mkdirs();
			OutputStream out = new FileOutputStream(new File(path));
			int read = 0;
			byte[] bytes = new byte[1024];

			out = new FileOutputStream(new File(path));
			while ((read = inputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
			out.close();
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	/**
	 * Write to file.
	 *
	 * @param path the path
	 * @param text the text
	 * @author J.-Fabian Wenisch
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void writeToFile(String path, String text) throws IOException {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(path));
			writer.write(text);

		} finally {
			try {
				if (writer != null)
					writer.close();
			} catch (IOException e) {
			}
		}
	}
	
	public static List<String> getFolderContent(String parentPath) throws IOException
	{
		 try (Stream<Path> paths = Files.walk(Paths.get(parentPath))) {
		        List<String> list = paths
		                .map(path -> Files.isDirectory(path) ? path.toString() + '/' : path.toString())
		                .collect(Collectors.toList());
		        return list;
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		 return new ArrayList<String>();
	}
	
	
}
