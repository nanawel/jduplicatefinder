package nnwl.jduplicatefinder.engine.event;

import nnwl.jduplicatefinder.engine.Runner;

/**
 * JDuplicateFinder
 *
 * @author Anael Ollier <nanawel NOSPAM [at] gmail [dot] com>
 * @license GPLv3 - See LICENSE
 */
public class RunnerExceptionEvent extends RunnerEvent {
	protected Throwable cause;

	public RunnerExceptionEvent(Runner r, Throwable t) {
		super(r);
		this.cause = t;
	}

	public Throwable getCause() {
		return this.cause;
	}
}
