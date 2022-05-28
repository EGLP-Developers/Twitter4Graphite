package me.eglp.twitter.util;

public class TwitterException extends RuntimeException {

	private static final long serialVersionUID = 1714700374213614235L;

	public TwitterException() {
		super();
	}

	public TwitterException(String message, Throwable cause) {
		super(message, cause);
	}

	public TwitterException(String message) {
		super(message);
	}

	public TwitterException(Throwable cause) {
		super(cause);
	}
	
}
