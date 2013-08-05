package nnwl.jduplicatefinder.engine.comparators.event;

/**
 * JDuplicateFinder
 *  
 * @author Anael Ollier <nanawel NOSPAM [at] gmail [dot] com>
 * @license GPLv3 - See LICENSE
 */
public interface ComparatorListener
{
	public void filesAnalyzeProgressed(ComparatorEvent ev);
	
	public void filesAnalyzeCompleted(ComparatorEvent ev);
	
	public void exceptionCaught(ComparatorExceptionEvent ex);
}
