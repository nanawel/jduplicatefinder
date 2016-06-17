package nnwl.jduplicatefinder.engine.event;

/**
 * JDuplicateFinder
 *
 * @author Anael Ollier <nanawel NOSPAM [at] gmail [dot] com>
 * @license GPLv3 - See LICENSE
 */
public class RunnerAdapter implements RunnerListener {
	@Override
	public void statusChanged(RunnerEvent ev) {
	}

	@Override
	public void fileScanStarted(RunnerEvent ev) {
	}

	@Override
	public void fileScanProgressed(RunnerEvent ev) {
	}

	@Override
	public void fileScanCompleted(RunnerEvent ev) {
	}

	@Override
	public void comparatorsFilesAnalyzeStarted(RunnerEvent ev) {
	}

	@Override
	public void comparatorsFilesAnalyzeProgressed(RunnerEvent ev) {
	}

	@Override
	public void comparatorsFilesAnalyzeCompleted(RunnerEvent ev) {
	}

	@Override
	public void exceptionCaught(RunnerExceptionEvent ev) {
	}

	@Override
	public void comparatorFilesComparisonStarted(RunnerEvent ev) {
	}

	@Override
	public void comparatorFilesComparisonProgressed(RunnerEvent ev) {
	}

	@Override
	public void comparatorFilesComparisonCompleted(RunnerEvent ev) {
	}
}
