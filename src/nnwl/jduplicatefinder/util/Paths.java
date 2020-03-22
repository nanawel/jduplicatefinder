package nnwl.jduplicatefinder.util;

import org.apache.log4j.Logger;

import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.Map.Entry;

/**
 * JDuplicateFinder
 *
 * @author Anael Ollier <nanawel NOSPAM [at] gmail [dot] com>
 * @license GPLv3 - See LICENSE
 */
public class Paths {
	private static final Logger logger = Logger.getLogger(Paths.class);

	public static Path[] getRoots(File... files) {
		Path[] paths = new Path[files.length];
		for (int i = 0; i < files.length; i++) {
			paths[i] = files[i].toPath();
		}
		return getRoots(paths);
	}

	public static Path[] getRoots(Path... paths) {
		Set<Path> roots = new HashSet<>();
		roots.add(paths[0].getRoot());
		for (int i = 1; i < paths.length; i++) {
			if (!roots.contains(paths[i].getRoot())) {
				roots.add(paths[i].getRoot());
			}
		}
		return (Path[]) roots.toArray();
	}

	public static Path[] commonPathsByRoot(File... files) {
		Path[] paths = new Path[files.length];
		for (int i = 0; i < files.length; i++) {
			paths[i] = files[i].toPath();
		}
		return commonPathsByRoot(paths);
	}

	public static Path[] commonPathsByRoot(Path... paths) {
		Map<Path, ArrayList<Path>> pathsByRoot = new HashMap<Path, ArrayList<Path>>();
		for (int i = 0; i < paths.length; i++) {
			ArrayList<Path> pathsForCurrentRoot = pathsByRoot.get(paths[i].getRoot());
			if (pathsForCurrentRoot == null) {
				pathsForCurrentRoot = new ArrayList<Path>();
				pathsByRoot.put(paths[i].getRoot(), pathsForCurrentRoot);
			}
			pathsForCurrentRoot.add(paths[i]);
		}

		Path[] commonPaths = new Path[pathsByRoot.size()];
		int i = 0;
		for (Entry<Path, ArrayList<Path>> entry : pathsByRoot.entrySet()) {
			commonPaths[i++] = commonPath((Path[]) entry.getValue().toArray(new Path[entry.getValue().size()]));
		}

		return commonPaths;
	}

	public static String[] pathsToStrings(Path... paths) {
		String[] strings = new String[paths.length];
		for (int i = 0; i < paths.length; i++) {
			strings[i] = paths[i].toString();
		}
		return strings;
	}

	/**
	 * Returns the common path of the given paths, or NULL if their roots
	 * do not match.
	 *
	 * @param paths
	 * @return The common path or NULL if none found
	 */
	public static Path commonPath(Path... paths) {
		logger.debug("Searching common path to " + Arrays.toString(paths));
		Path baseFolder = paths[0];
		Path commonPath;

		if (paths.length == 1) {
			commonPath = baseFolder;
		} else {
			int commonPathLength = 0;
			mainLoop:
			//I don't specially like that but it's quite convenient
			for (; commonPathLength < baseFolder.getNameCount(); commonPathLength++) {
				for (int i = 1; i < paths.length; i++) {
					if (!baseFolder.getRoot().equals(paths[i].getRoot())) {
						logger.debug("Roots do not match, exiting.");
						return null;
					}
					if (!baseFolder.subpath(0, commonPathLength + 1).equals(paths[i].subpath(0, commonPathLength + 1))) {
						break mainLoop;
					}
				}
			}
			if (commonPathLength == 0) {
				commonPath = baseFolder.getRoot();
			} else {
				commonPath = baseFolder.getRoot().resolve(baseFolder.subpath(0, commonPathLength));
			}
		}
		logger.debug("Found: " + commonPath);
		return commonPath;
	}
}
