package nnwl.jduplicatefinder.filesystem;

import org.apache.log4j.Logger;

import java.io.File;

/**
 * JDuplicateFinder
 * <p>
 * Centralize filesystem-related calls
 * (ATM only calls to File.delete() have been moved here)
 *
 * @author Anael Ollier <nanawel NOSPAM [at] gmail [dot] com>
 * @license GPLv3 - See LICENSE
 */
public class DefaultFileSystem implements AbstractFileSystem {
	private final Logger logger = Logger.getLogger(DefaultFileSystem.class);

	public boolean delete(File file, boolean recursive) {
		if (file.isDirectory()) {
			return this.deleteFolder(file, recursive);
		}
		return this.deleteFile(file);
	}

	public boolean deleteFile(File file) {
		if (!file.exists()) {
			logger.error("File does not exist, skipping deletion: " + file.getAbsolutePath());
			return true;
		}
		if (!file.isFile()) {
			this.logger.error(file.getAbsolutePath() + " is not a regular path. Canceling deletion.");
			return false;
		}
		if (file.delete()) {
			logger.info("File deleted: " + file.getAbsolutePath());
			return true;
		}
		logger.error("Cannot delete path: " + file.getAbsolutePath());
		return false;
	}

	public boolean deleteFolder(File folder) {
		return this.deleteFolder(folder, false);
	}

	public boolean deleteFolder(File folder, boolean recursive) {
		if (!folder.isDirectory()) {
			this.logger.error(folder.getAbsolutePath() + " is not a folder. Canceling deletion.");
			return false;
		}

		if (recursive) {
			File[] files = folder.listFiles();
			if (files == null) {
				this.logger.error("Cannot get content of folder " + folder.getAbsolutePath() + ", skipping.");
				return false;
			}

			for (File f : files) {
				if (f.isDirectory()) {
					this.deleteFolder(f, true);
				} else {
					this.deleteFile(f);
				}
			}
		}

		if (folder.delete()) {
			logger.info("Folder deleted: " + folder.getAbsolutePath());
			return true;
		}
		logger.error("Cannot delete folder: " + folder.getAbsolutePath());
		return false;
	}
}
