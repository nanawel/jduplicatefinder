package nnwl.jduplicatefinder.engine;

import nnwl.jduplicatefinder.engine.comparators.AbstractDuplicateComparator;
import nnwl.jduplicatefinder.engine.comparators.event.ComparatorEvent;
import nnwl.jduplicatefinder.engine.comparators.event.ComparatorExceptionEvent;
import nnwl.jduplicatefinder.engine.comparators.event.ComparatorListener;
import nnwl.jduplicatefinder.engine.event.RunnerEvent;
import nnwl.jduplicatefinder.engine.event.RunnerExceptionEvent;
import nnwl.jduplicatefinder.engine.event.RunnerListener;
import org.apache.log4j.Logger;

import java.io.File;
import java.nio.file.Path;
import java.text.DateFormat;
import java.util.*;

/**
 * JDuplicateFinder
 *
 * @author Anael Ollier <nanawel NOSPAM [at] gmail [dot] com>
 * @license GPLv3 - See LICENSE
 */
public class Runner implements Runnable {
	/**
	 * An event will be fired to listeners every 50 files processed
	 */
	public static final int LISTENERS_FILES_STEP = 50;

	public static final int STATUS_INTERRUPTED = -2;
	public static final int STATUS_UNDEFINED = -1;
	public static final int STATUS_INIT = 0;
	public static final int STATUS_SCANNING_FILES = 1;
	public static final int STATUS_ANALYZING_FILES = 2;
	public static final int STATUS_COMPARING_FILES = 3;
	public static final int STATUS_COMPLETED = 4;

	private static final Logger logger = Logger.getLogger(Runner.class);

	protected Set<Path> files = null;

	protected Set<Path> folders = null;

	protected List<AbstractDuplicateComparator> comparators;

	protected FileFilter[] fileFilters = null;

	protected Set<Path> filteredFiles = null;

	protected boolean recurseSubdirectories = true;

	protected boolean interrupt = false;

	protected Exception runException = null;

	protected ResultsSet globalResults = new ResultsSet();

	protected LinkedList<RunnerListener> listeners = new LinkedList<RunnerListener>();

	protected long currentFileProgressCount = 0;
	protected long totalFileProgressCount = 0;

	protected long startTimestamp;
	protected long endTimestamp;

	protected int status = STATUS_UNDEFINED;
	protected int oldStatus = STATUS_UNDEFINED;

	/**
	 * @param path
	 * @param comparators
	 */
	public Runner(Path path, ArrayList<AbstractDuplicateComparator> comparators) {
		this.folders = new TreeSet<Path>();
		this.folders.add(path);
		this.comparators = comparators;
	}

	/**
	 * @param folders
	 * @param comparators
	 */
	public Runner(ArrayList<Path> folders, ArrayList<AbstractDuplicateComparator> comparators) {
		this.folders = new TreeSet<Path>();
		this.folders.addAll(folders);
		this.comparators = comparators;
	}

	public void run() {
		this.startTimestamp = System.currentTimeMillis();
		try {
			this.interrupt = false;
			this.setStatus(STATUS_INIT);

			logger.info("Runner started on " + DateFormat.getDateTimeInstance().format(new Date()));
			if (this.fileFilters != null && this.fileFilters.length > 0) {
				logger.info("Using filters: " + Arrays.toString(this.fileFilters));
			} else {
				logger.info("No path filters defined");
			}

			logger.info("Browsing folders...");
			this.listFilesRecursively(this.folders);

			logger.info(this.files.size() + " files found.");

			if (this.runException != null) {
				throw this.runException;
			}

			logger.info("Analyzing files...");
			this.feedComparators();
			logger.info("Done");

			if (this.runException != null) {
				throw this.runException;
			}

			logger.info("Analyzing results...");
			this.globalResults = this.runComparators();
			logger.info("Done");

			this.endTimestamp = System.currentTimeMillis();

			if (this.runException != null) {
				throw this.runException;
			}

			this.setStatus(STATUS_COMPLETED);
		} catch (InterruptedException e) {
			// nothing, just stop
		} catch (Exception | Error e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);

			this.fireExceptionCaught(e);
		}
	}

	public void configure(Map<String, Object> parameters) throws Exception {
		if (parameters.containsKey("runner.recurse_subdirectories")) {
			this.recurseSubdirectories = Boolean.valueOf((String) parameters.get("runner.recurse_subdirectories"));
		}
	}

	public Set<Path> listFilesRecursively(Set<Path> folders) throws Exception {
		this.files = new TreeSet<Path>();
		this.filteredFiles = new TreeSet<Path>();
		Queue<Path> dirs = new LinkedList<Path>();

		this.setStatus(STATUS_SCANNING_FILES);
		this.fireFileScanStarted();

		dirs.addAll(folders);
		long totalFiles = 0;
		this.currentFileProgressCount = 0;
		while (!dirs.isEmpty()) {
			Path dir = dirs.poll();
			File dirFile = dir.toFile();
			if (!dirFile.isDirectory()) {
				logger.error(dir.toAbsolutePath() + " is not a valid directory, skipping.");
				continue;
			}
			File[] childrenFiles = dirFile.listFiles();
			if (!dirFile.canRead() || childrenFiles == null) {
				logger.error(dir.toAbsolutePath() + " is not readable, skipping.");
				continue;
			}

			for (File f : childrenFiles) {
				if (this.interrupt) {
					throw new InterruptedException();
				}

				if (f.isDirectory() && this.recurseSubdirectories) {
					if (this.matchesFilter(f.toPath())) {
						logger.debug("Skipping filtered directory: " + f.getAbsolutePath());
					} else {
						dirs.add(f.toPath());
					}
				} else if (f.isFile()) {
					totalFiles++;
					if (this.matchesFilter(f.toPath())) {
						logger.debug("Skipping filtered path: " + f.getAbsolutePath());
						this.filteredFiles.add(f.toPath());
					} else {
						this.files.add(f.toPath());
						this.currentFileProgressCount++;
					}

					if (totalFiles % LISTENERS_FILES_STEP == 0) {
						this.fireFileScanProgressed();
					}
				}
			}
		}

		this.fireFileScanProgressed();
		this.fireFileScanCompleted();

		return this.files;
	}

	protected void feedComparators() throws InterruptedException {
		try {
			this.currentFileProgressCount = 0;
			this.totalFileProgressCount = this.files.size();

			this.setStatus(STATUS_ANALYZING_FILES);
			this.fireComparatorsFileAnalyzeStarted();

			for (Path path : this.files) {
				if (this.interrupt) {
					throw new InterruptedException();
				}

				this.feedComparators(path);

				this.currentFileProgressCount++;
				if (this.currentFileProgressCount % LISTENERS_FILES_STEP == 0) {
					this.fireComparatorsFilesAnalyzeProgressed();
				}
			}

			this.fireComparatorsFilesAnalyzeCompleted();
		} catch (InterruptedException e) {
			throw e;
		} catch (Exception | Error e) {
			e.printStackTrace();
		}
	}

	protected void feedComparators(Path path) {
		for (AbstractDuplicateComparator comparator : this.comparators) {
			try {
				comparator.analyze(path);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Cannot analyze path " + path.toAbsolutePath() + ", skipping. (" + e.getClass().getName() + " / " + e.getMessage() + ")");
			}
		}
	}

	protected ResultsSet runComparators() throws InterruptedException {
		this.currentFileProgressCount = 0;
		this.totalFileProgressCount = this.files.size();

		this.setStatus(STATUS_COMPARING_FILES);
		this.fireComparatorFilesComparisonStarted();

		final ResultsSet results = new ResultsSet();
		for (final AbstractDuplicateComparator comparator : this.comparators) {
			// Handle interruption
			if (this.interrupt) {
				throw new InterruptedException();
			}

			// Initialize current comparator and assign listener to track its progression
			final String comparatorLabel = comparator.getLabel();
			comparator.addListener(new ComparatorListener() {
				@Override
				public void filesAnalyzeProgressed(ComparatorEvent cev) {
					if (Runner.this.interrupt) {
						cev.getComparator().interrupt();
						return;
					}

					Runner.this.currentFileProgressCount = cev.getCurrentFilesCount();
					Runner.this.totalFileProgressCount = cev.getTotalFilesCount();
					if (cev.getCurrentFilesCount() % LISTENERS_FILES_STEP == 0) {
						Runner.this.fireComparatorFilesComparisonProgressed();
					}
				}

				@Override
				public void filesAnalyzeCompleted(ComparatorEvent cev) {
					for (Map.Entry<Path, FileResult> fr : comparator.getResults().entrySet()) {
						if (Runner.this.interrupt) {
							return;
						}

						if (results.containsKey(fr.getKey())) {
							results.get(fr.getKey()).appendSimilarityResults(fr.getValue().getSimilarityResults());
						} else {
							results.put(fr.getKey(), fr.getValue());
						}
					}
				}

				@Override
				public void exceptionCaught(ComparatorExceptionEvent ex) {
					Runner.this.runException = ex.getException();
					Runner.this.fireExceptionCaught(ex.getException());
				}
			});

			// Handle interruption
			if (this.interrupt) {
				throw new InterruptedException();
			}

			// Run comparator in a dedicated thread
			Thread comparatorThread = new Thread(comparator, comparatorLabel + "-Comparator");
			comparatorThread.start();
			comparatorThread.join();
		}

		if (this.interrupt) {
			throw new InterruptedException();
		}

		this.fireComparatorFilesComparisonProgressed();
		this.fireComparatorFilesComparisonCompleted();

		return results;
	}

	protected boolean matchesFilter(Path p) {
		if (this.fileFilters != null) {
			for (FileFilter ff : this.fileFilters) {
				if (ff.matches(p)) {
					return true;
				}
			}
		}
		return false;
	}

	protected RunnerEvent getNewEvent() {
		return new RunnerEvent(this, this.oldStatus, this.status, this.currentFileProgressCount, this.totalFileProgressCount);
	}

	protected void fireStatusChanged() {
		RunnerEvent ev = this.getNewEvent();
		for (RunnerListener rl : this.listeners) {
			rl.statusChanged(ev);
		}
	}

	protected void fireFileScanStarted() {
		RunnerEvent ev = this.getNewEvent();
		for (RunnerListener rl : this.listeners) {
			rl.fileScanStarted(ev);
		}
	}

	protected void fireFileScanProgressed() {
		RunnerEvent ev = this.getNewEvent();
		for (RunnerListener rl : this.listeners) {
			rl.fileScanProgressed(ev);
		}
	}

	protected void fireFileScanCompleted() {
		RunnerEvent ev = this.getNewEvent();
		for (RunnerListener rl : this.listeners) {
			rl.fileScanCompleted(ev);
		}
	}

	protected void fireComparatorsFileAnalyzeStarted() {
		RunnerEvent ev = this.getNewEvent();
		for (RunnerListener rl : this.listeners) {
			rl.comparatorsFilesAnalyzeStarted(ev);
		}
	}

	protected void fireComparatorsFilesAnalyzeProgressed() {
		RunnerEvent ev = this.getNewEvent();
		for (RunnerListener rl : this.listeners) {
			rl.comparatorsFilesAnalyzeProgressed(ev);
		}
	}

	protected void fireComparatorsFilesAnalyzeCompleted() {
		RunnerEvent ev = this.getNewEvent();
		for (RunnerListener rl : this.listeners) {
			rl.comparatorsFilesAnalyzeCompleted(ev);
		}
	}

	protected void fireComparatorFilesComparisonStarted() {
		RunnerEvent ev = this.getNewEvent();
		for (RunnerListener rl : this.listeners) {
			rl.comparatorFilesComparisonStarted(ev);
		}
	}

	protected void fireComparatorFilesComparisonProgressed() {
		RunnerEvent ev = this.getNewEvent();
		for (RunnerListener rl : this.listeners) {
			rl.comparatorFilesComparisonProgressed(ev);
		}
	}

	protected void fireComparatorFilesComparisonCompleted() {
		RunnerEvent ev = this.getNewEvent();
		for (RunnerListener rl : this.listeners) {
			rl.comparatorFilesComparisonCompleted(ev);
		}
	}

	protected void fireExceptionCaught(Throwable ex) {
		RunnerExceptionEvent ev = new RunnerExceptionEvent(this, ex);
		for (RunnerListener rl : this.listeners) {
			rl.exceptionCaught(ev);
		}
	}

	protected void setStatus(int status) {
		this.oldStatus = this.status;
		this.status = status;
		this.fireStatusChanged();
	}

	public ResultsSet getResults() {
		return this.globalResults;
	}

	public void addListener(RunnerListener listener) {
		this.listeners.add(listener);
	}

	public Set<Path> getFiles() {
		return files;
	}

	public Set<Path> getFolders() {
		return folders;
	}

	public List<AbstractDuplicateComparator> getComparators() {
		return comparators;
	}

	public FileFilter[] getFileFilters() {
		return fileFilters;
	}

	public void setFileFilters(FileFilter[] fileFilters) {
		this.fileFilters = fileFilters;
	}

	public void interrupt() {
		this.interrupt = true;
		this.setStatus(STATUS_INTERRUPTED);
	}

	public long getStartTimestamp() {
		return startTimestamp;
	}

	public void setStartTimestamp(long startTimestamp) {
		this.startTimestamp = startTimestamp;
	}

	public long getEndTimestamp() {
		return endTimestamp;
	}

	public void setEndTimestamp(long endTimestamp) {
		this.endTimestamp = endTimestamp;
	}
}
