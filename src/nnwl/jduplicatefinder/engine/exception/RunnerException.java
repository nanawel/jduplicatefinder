package nnwl.jduplicatefinder.engine.exception;

/**
 * JDuplicateFinder
 *  
 * @author Anael Ollier <nanawel NOSPAM [at] gmail [dot] com>
 * @license GPLv3 - See LICENSE
 */
public class RunnerException extends Exception
{
	private static final long serialVersionUID = 7116785858490665679L;

	public RunnerException() {
		super();
	}

	public RunnerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public RunnerException(String message, Throwable cause) {
		super(message, cause);
	}

	public RunnerException(String message) {
		super(message);
	}

	public RunnerException(Throwable cause) {
		super(cause);
	}
}
