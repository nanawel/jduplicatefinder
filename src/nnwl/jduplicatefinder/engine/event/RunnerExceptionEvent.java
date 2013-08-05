package nnwl.jduplicatefinder.engine.event;

import nnwl.jduplicatefinder.engine.Runner;

public class RunnerExceptionEvent extends RunnerEvent
{
	protected Throwable cause;
	
	public RunnerExceptionEvent(Runner r, Throwable t) {
		super(r);
		this.cause = t;
	}
	
	public Throwable getCause() {
		return this.cause;
	}
}
