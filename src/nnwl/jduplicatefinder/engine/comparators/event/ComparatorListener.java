package nnwl.jduplicatefinder.engine.comparators.event;

/**
 * JDuplicateFinder
 *
 * @author Anael Ollier <nanawel NOSPAM [at] gmail [dot] com>
 * @license GPLv3 - See LICENSE
 */
public interface ComparatorListener {
	void filesAnalyzeProgressed(ComparatorEvent ev);

	void filesAnalyzeCompleted(ComparatorEvent ev);

	void exceptionCaught(ComparatorExceptionEvent ex);
}
