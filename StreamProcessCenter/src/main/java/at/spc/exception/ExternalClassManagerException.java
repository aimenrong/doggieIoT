package at.spc.exception;

/**
 * Created by eyonlig on 9/22/2017.
 */
public class ExternalClassManagerException extends RuntimeException {
    public ExternalClassManagerException(Exception e) {
        super(e);
    }
}
