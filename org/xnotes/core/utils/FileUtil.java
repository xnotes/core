/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xnotes.core.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.tika.Tika;

/**
 *
 * @author sopheap
 */
public final class FileUtil {

	public static final class PathFileComponents {

		public final String path;
		public final String file;
		public final String filePath;

		public PathFileComponents(String filePath, String defaultFilePath) {
			filePath = filePath != null ? filePath.trim() : (defaultFilePath != null ? defaultFilePath.trim() : null);
			if (filePath == null) {
				this.path = getPath("~");
				this.file = "";
				this.filePath = this.path + this.file;
			} else {
				int i = filePath.lastIndexOf(File.separator);
				if (i > -1) {
					this.path = getPath(filePath.substring(0, i));
					this.file = filePath.substring(i + 1).trim();
					this.filePath = filePath;
				} else {
					this.path = getPath("~");
					this.file = filePath;
					this.filePath = this.path + this.file;
				}
			}
		}

		@Override
		public String toString() {
			return filePath;
		}

	}

	public static final PathFileComponents getPathFileComponents(String filePath, String defaultFilePath) {
		return new PathFileComponents(filePath, defaultFilePath);
	}

	public static final String getPath(String path) {
		if (path == null) {
			path = "~";
		}
		if (!path.trim().endsWith(File.separator)) {
			return path.trim() + File.separator;
		} else {
			return path.trim();
		}
	}

	public static final String getActualPath(String path) {
		if (path == null) {
			path = System.getProperty("user.home");
		} else {
			PathFileComponents pfc = getPathFileComponents(path, null);
			path = pfc.path.replaceAll("(?<!\\\\)(?:(\\\\\\\\)*)\\~", System.getProperty("user.home")) + pfc.file;
		}
		return path.replaceAll("(?<!\\\\)(?:(\\\\\\\\)*)\\/", File.separator);
	}

	public static final String createPathIfNotExist(String path) {
		path = FileUtil.getActualPath(FileUtil.getPath(path));
		File directory = new File(path);
		if (!directory.exists()) {
			if (directory.mkdirs()) {
				return path;
			} else {
				return null;
			}
		} else {
			return path;
		}
	}

	public static String readFileAsString(String filePath) throws IOException {
		StringBuilder sb = new StringBuilder();
		try (Stream<String> stream = Files.lines(Paths.get(getActualPath(filePath)))) {
			stream.forEach(sb::append);
		}
		return sb.toString();
	}

	public static String readFileAsString(File file) throws IOException {
		file = new File(getActualPath(file.getPath()));
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();
			while (line != null) {
				sb.append(line);
				line = br.readLine();
			}
			return sb.toString();
		}
	}

	public static String readFileAsString(URL url) throws IOException {
		try {
			return readFileAsString(new File(url.toURI()));
		} catch (URISyntaxException ex) {
			throw new IOException(ex);
		}
	}

	public static byte[] readFileAsBytes(String filePath) throws IOException {
		return Files.readAllBytes(Paths.get(getActualPath(filePath)));
	}

	public static byte[] readFileAsBytes(File file) throws IOException {
		return Files.readAllBytes(Paths.get(file.toURI()));
	}

	public static byte[] readFileAsBytes(URL url) throws IOException {
		try {
			return readFileAsBytes(new File(url.toURI()));
		} catch (URISyntaxException ex) {
			throw new IOException(ex);
		}
	}

	public static String getResourceAsString(Class<?> cls, String fileName) throws IOException {
		try (BufferedReader buffer = new BufferedReader(new InputStreamReader(getResourceAsStream(cls, fileName)))) {
			return buffer.lines().collect(Collectors.joining());
		}
	}

	public static InputStream getResourceAsStream(Class<?> cls, String fileName) throws IOException {
		ClassLoader classLoader = cls.getClassLoader();
		String resourcePath = cls.getPackage().getName().replaceAll("\\.", File.separator);
		String filePath = resourcePath + File.separator + fileName;
		InputStream is = classLoader.getResourceAsStream(filePath);
		if (is != null) {
			return is;
		} else {
			throw new IOException("Resource file '" + filePath + "' does not exist.");
		}
	}

	public static final void writeToFile(String filePath, String content) throws IOException {
		writeToFile(filePath, content.getBytes());
	}

	public static final void writeToFile(String filePath, byte[] content) throws IOException {
		FileUtil.PathFileComponents pfc = FileUtil.getPathFileComponents(filePath, filePath);
		filePath = FileUtil.getActualPath(filePath);
		if (Files.notExists(Paths.get(filePath))) {
			if (!pfc.path.equals(FileUtil.getPath("~"))) {
				FileUtil.createPathIfNotExist(pfc.path);
			}
		}
		if (content == null) {
			content = new byte[0];
		}
		Path path = Paths.get(filePath);
		try {
			Files.write(path, content);
		} catch (IOException ex) {
			Files.createFile(path);
			Files.write(path, content);
		}
	}

	private static final Tika _TIKA = new Tika();

	public static String detectMIMEType(String path) {
		try {
			return _TIKA.detect(new File(path));
		} catch (IOException ex) {
			return null;
		}
	}

	public static String detectMIMEType(byte[] prefix) {
		return _TIKA.detect(prefix);
	}

}
