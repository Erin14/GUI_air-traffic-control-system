package towersim.util;

/**
 * Exception thrown when there is insufficient space to undertake an action.
 * @ass1
 */
public class NoSpaceException extends Exception {

    /**
     * Constructs a NoSpaceException with no detail message.
     *
     * @see Exception#Exception()
     * @ass1
     */
    public NoSpaceException() {
        super();
    }

    /**
     * Constructs a NoSpaceException that contains a helpful detail message explaining why the
     * exception occurred.
     * <p>
     * <b>Important:</b> do not write JUnit tests that expect a valid implementation of the
     * assignment to have a certain error message, as the official solution will use different
     * messages to those you are expecting, if any at all.
     *
     * @param message detail message
     * @see Exception#Exception(String)
     * @ass1
     */
    public NoSpaceException(String message) {
        super(message);
    }
}
