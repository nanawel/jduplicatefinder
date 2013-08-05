package nnwl.jduplicatefinder.engine.event;

import nnwl.jduplicatefinder.engine.Runner;

/**
 * JDuplicateFinder
 *  
 * @author Anael Ollier <nanawel NOSPAM [at] gmail [dot] com>
 * @license GPLv3 - See LICENSE
 */
public class RunnerEvent
{
	protected Runner runner;
	
	protected int status = Runner.STATUS_UNDEFINED;
	
	private int oldStatus = Runner.STATUS_UNDEFINED;

	protected long currentFilesCount = 0;
	
	protected long totalFilesCount = 0;
	
	protected String label = null;
	
	public RunnerEvent(Runner r) {
		this.runner = r;
	}
	
	public RunnerEvent(Runner r, int oldStatus, int status, long currentFilesCount, long totalFilesCount) {
		this(r);
		this.status = status;
		this.oldStatus = oldStatus;
		this.currentFilesCount = currentFilesCount;
		this.totalFilesCount = totalFilesCount;
	}
	
	public RunnerEvent(Runner r, int status, long currentFilesCount, long totalFilesCount) {
		this(r);
		this.status = status;
		this.currentFilesCount = currentFilesCount;
		this.totalFilesCount = totalFilesCount;
	}
	
	public Runner getRunner() {
		return runner;
	}

	public long getCurrentFilesCount() {
		return currentFilesCount;
	}
	
	public long getTotalFilesCount() {
		return totalFilesCount;
	}
	
	public int getStatus() {
		return status;
	}
	
	public int getOldStatus() {
		return oldStatus;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		if (this.label != null) {
			return;
		}
		this.label = label;
	}
}
