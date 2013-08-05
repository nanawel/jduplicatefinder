package nnwl.jduplicatefinder.engine.comparators.event;

public interface ComparatorListener
{
	public void filesAnalyzeProgressed(ComparatorEvent ev);
	
	public void filesAnalyzeCompleted(ComparatorEvent ev);
	
	public void exceptionCaught(ComparatorExceptionEvent ex);
}
