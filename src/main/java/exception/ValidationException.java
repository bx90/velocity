package exception;



/**
 * ValidationException.java
 *
 * Indicates that expected input value was missing or did not conform to accepted form.
 *
 * In general, ValidationExceptions indicates user error (not one to point fingers, but...)   It is
 * possible that dev code could result in a ValidationException being thrown, however any such issues should
 * be resolved at development time prior to release (otherwise you are doing it wrong).
 *
 * HTTP Status: 400
 *
 * @author bsun
 */
public class ValidationException extends Exception {


    // reduce visibility so that caller must specify message
    protected ValidationException() {

        super();
    }


    // reduce visibility so that caller must specify message
    protected ValidationException(Throwable cause ) {

        super( cause );
    }


    public ValidationException(String message ) {

        super( message );
    }


    public ValidationException(String message, Throwable cause ) {

        super( message, cause );
    }

}
