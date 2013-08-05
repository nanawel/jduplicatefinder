package nnwl.jduplicatefinder.engine.comparators.exception;

public class ComparatorException extends Exception
{
	private static final long serialVersionUID = 1307706267160320948L;

	public ComparatorException() {
		super();
	}

	public ComparatorException(String message) {
		super(message);
	}

	public ComparatorException(Throwable cause) {
		super(cause);
	}

	public ComparatorException(String message, Throwable cause) {
		super(message, cause);
	}

	public ComparatorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	@Override
	public String toString() {
		// Prefix with exception type
		return this.getCause().getClass() + ": " + super.toString();
	}
}
