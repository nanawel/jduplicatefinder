package nnwl.jduplicatefinder.filesystem;

/**
 * JDuplicateFinder
 *
 * @author Anael Ollier <nanawel NOSPAM [at] gmail [dot] com>
 * @license GPLv3 - See LICENSE
 */
public class FileSystemInterface {
	static AbstractFileSystem iface;

	public static AbstractFileSystem getFileSystemInterface() {
		if (iface == null) {
			iface = new DefaultFileSystem();
		}
		return iface;
	}
}
