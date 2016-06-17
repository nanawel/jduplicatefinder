package nnwl.jduplicatefinder.filesystem;

import java.io.File;

/**
 * JDuplicateFinder
 *
 * @author Anael Ollier <nanawel NOSPAM [at] gmail [dot] com>
 * @license GPLv3 - See LICENSE
 */
public interface AbstractFileSystem {
	boolean delete(File file, boolean recursive);

	boolean deleteFile(File file);

	boolean deleteFolder(File folder);

	boolean deleteFolder(File folder, boolean recursive);
}
