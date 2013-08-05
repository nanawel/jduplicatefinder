package nnwl.jduplicatefinder.engine.event;

/**
 * JDuplicateFinder
 *  
 * @author Anael Ollier <nanawel NOSPAM [at] gmail [dot] com>
 * @license GPLv3 - See LICENSE
 */
public interface RunnerListener
{
	void statusChanged(RunnerEvent ev);
	
	void fileScanStarted(RunnerEvent ev);
	
	void fileScanProgressed(RunnerEvent ev);
	
	void fileScanCompleted(RunnerEvent ev);
	
	void comparatorsFilesAnalyzeStarted(RunnerEvent ev);
	
	void comparatorsFilesAnalyzeProgressed(RunnerEvent ev);
	
	void comparatorsFilesAnalyzeCompleted(RunnerEvent ev);
	
	void comparatorFilesComparisonStarted(RunnerEvent ev);
	
	void comparatorFilesComparisonProgressed(RunnerEvent ev);
	
	void comparatorFilesComparisonCompleted(RunnerEvent ev);
	
	void exceptionCaugth(RunnerExceptionEvent ev);
}
