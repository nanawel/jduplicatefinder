package nnwl.jduplicatefinder.engine.comparators.event;

import nnwl.jduplicatefinder.engine.comparators.AbstractDuplicateComparator;

public class ComparatorEvent
{
	protected AbstractDuplicateComparator comparator;
	
	protected long currentFilesCount = 0;
	
	protected long totalFilesCount = 0;

	public ComparatorEvent(AbstractDuplicateComparator comparator) {
		this(comparator, 0, 0);
	}
	
	public ComparatorEvent(AbstractDuplicateComparator comparator, long currentFilesCount, long totalFilesCount) {
		this.comparator = comparator;
		this.currentFilesCount = currentFilesCount;
		this.totalFilesCount = totalFilesCount;
	}
	
	public long getCurrentFilesCount() {
		return currentFilesCount;
	}

	public long getTotalFilesCount() {
		return totalFilesCount;
	}
	
	public AbstractDuplicateComparator getComparator() {
		return comparator;
	}
}
