package nnwl.jduplicatefinder.engine.comparators;

import nnwl.jduplicatefinder.engine.ResultsSet;
import nnwl.jduplicatefinder.engine.SimilarityResult;
import nnwl.jduplicatefinder.engine.comparators.event.ComparatorEvent;
import nnwl.jduplicatefinder.engine.comparators.event.ComparatorExceptionEvent;
import nnwl.jduplicatefinder.engine.comparators.event.ComparatorListener;
import nnwl.jduplicatefinder.engine.comparators.exception.ComparatorException;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.Map;

/**
 * JDuplicateFinder
 *
 * @author Anael Ollier <nanawel NOSPAM [at] gmail [dot] com>
 * @license GPLv3 - See LICENSE
 */
abstract public class AbstractDuplicateComparator implements Runnable {
	protected String code;

	protected String label;

	protected ResultsSet results;

	protected float weight = 1;

	protected LinkedList<ComparatorListener> listeners = new LinkedList<ComparatorListener>();

	protected boolean interrupt = false;

	abstract public void configure(Map<String, Object> parameters) throws Exception;

	abstract public void analyze(Path path) throws Exception;

	abstract public void run();

	abstract public String getSimilarityResultDescription(SimilarityResult similarityResult);

	public void interrupt() {
		this.interrupt = true;
	}

	public ResultsSet getResults() {
		return this.results;
	}

	public String getCode() {
		return this.code;
	}

	public String getLabel() {
		return label;
	}

	public float getWeight() {
		return weight;
	}

	public void setWeight(float weight) {
		this.weight = weight;
	}

	public void addListener(ComparatorListener listener) {
		this.listeners.add(listener);
	}

	protected void filesAnalyzeProgressed(AbstractDuplicateComparator comparator, long currentFilesCount, long totalFilesCount) {
		ComparatorEvent ev = new ComparatorEvent(comparator, currentFilesCount, totalFilesCount);
		for (ComparatorListener l : this.listeners) {
			l.filesAnalyzeProgressed(ev);
		}
	}

	protected void filesAnalyzeCompleted(AbstractDuplicateComparator comparator, long currentFilesCount, long totalFilesCount) {
		ComparatorEvent ev = new ComparatorEvent(comparator, currentFilesCount, totalFilesCount);
		for (ComparatorListener l : this.listeners) {
			l.filesAnalyzeCompleted(ev);
		}
	}

	protected void exceptionCaught(AbstractDuplicateComparator comparator, ComparatorException ex) {
		ComparatorExceptionEvent ev = new ComparatorExceptionEvent(comparator, ex);
		for (ComparatorListener l : this.listeners) {
			l.exceptionCaught(ev);
		}
	}
}
