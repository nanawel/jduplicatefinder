package nnwl.jduplicatefinder.engine.comparators.event;

import nnwl.jduplicatefinder.engine.comparators.AbstractDuplicateComparator;
import nnwl.jduplicatefinder.engine.comparators.exception.ComparatorException;

/**
 * JDuplicateFinder
 *
 * @author Anael Ollier <nanawel NOSPAM [at] gmail [dot] com>
 * @license GPLv3 - See LICENSE
 */
public class ComparatorExceptionEvent extends ComparatorEvent {
	protected ComparatorException cause;

	public ComparatorExceptionEvent(AbstractDuplicateComparator c, ComparatorException t) {
		super(c);
		this.cause = t;
	}

	public ComparatorException getException() {
		return this.cause;
	}
}
