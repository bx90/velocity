package file;


import exception.ValidationException;

import java.util.List;

/**
 * @author bsun
 */
public interface FileGenerator {
    List<String> generateFiles() throws ValidationException;

    class PageFileGeneratorException extends Exception {
        public PageFileGeneratorException(String message) {
            super(message);
        }
    }
}
