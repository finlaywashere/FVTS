package ca.team2706.fvts.modules;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import ca.team2706.fvts.core.Constants;
import ca.team2706.fvts.core.Log;

public class ModuleLoader {
	private Map<String,Module> modules = new HashMap<String,Module>();
	public void load(String[] modulePaths) throws Exception {
		for (String path : modulePaths) {
			load(path);
		}
	}

	public void load(String modulePath) throws Exception {
		if(modules.containsKey(modulePath)) {
			Log.i("Skipping loading module "+modulePath+" because it has been loaded previously", true);
			return;
		}
		File infoPath = new File(modulePath + ".jar.info");
		File moduleFile = new File(modulePath+".jar");
		File resourcesFile = new File(modulePath + ".jar.res.zip");

		if (!infoPath.exists()) {
			Log.e("FATAL: Failed to find info file path for module " + modulePath, true);
			System.exit(1);
		}
		if (!moduleFile.exists()) {
			Log.e("FATAL: Failed to find jar file for module at " + modulePath, true);
			System.exit(1);
		}

		Scanner infoIn = new Scanner(infoPath);
		String mainClass = infoIn.nextLine();
		infoIn.close();

		ClassLoader loader = URLClassLoader.newInstance(new URL[] { moduleFile.toURI().toURL() },
				getClass().getClassLoader());
		Class<?> clazz = Class.forName(mainClass, true, loader);
		Module module = null;
		try {
			Class<? extends Module> modClass = clazz.asSubclass(Module.class);
			// Avoid Class.newInstance, for it is evil.
			Constructor<? extends Module> ctor = modClass.getConstructor();
			module = ctor.newInstance();
		} catch (Exception e) {
			Log.e("FATAL: Failed to load main class for module " + modulePath, true);
			System.exit(1);
		}

		File resourcesFolder = null;
		if (resourcesFile.exists()) {
			// Unpack resources
			resourcesFolder = Files.createTempDirectory(Constants.NAME + "-" + moduleFile.getName() + "-res").toFile();
			unzip(resourcesFile, resourcesFolder);
		}

		// Do stuff

		module.init(resourcesFolder);
		
		modules.put(modulePath, module);
	}

	/**
	 * Un-zips a .zip archive
	 * 
	 * https://www.baeldung.com/java-compress-and-uncompress
	 * 
	 * @author baeldung.com
	 */
	private static void unzip(File zipFile, File directory) throws Exception {
		byte[] buffer = new byte[1024];
		ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
		ZipEntry zipEntry = zis.getNextEntry();
		while (zipEntry != null) {
			File newFile = newFile(directory, zipEntry);
			FileOutputStream fos = new FileOutputStream(newFile);
			int len;
			while ((len = zis.read(buffer)) > 0) {
				fos.write(buffer, 0, len);
			}
			fos.close();
			zipEntry = zis.getNextEntry();
		}
		zis.closeEntry();
		zis.close();
	}

	private static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
		File destFile = new File(destinationDir, zipEntry.getName());

		String destDirPath = destinationDir.getCanonicalPath();
		String destFilePath = destFile.getCanonicalPath();

		if (!destFilePath.startsWith(destDirPath + File.separator)) {
			throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
		}

		return destFile;
	}
}
