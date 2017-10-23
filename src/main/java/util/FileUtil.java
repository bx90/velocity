package util;


import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Consumer;


/**
 * FileUtil
 *
 * @author bsun
 */
public class FileUtil {

    public static String getCurrentDirectory() {
        return System.getProperty("user.dir");
    }


    /**
     * Reads application resource file as stream given thread context relative path.
     *
     * @param relativePath
     * @return
     * @throws IOException
     */
    public static InputStream getResourceInputStream(String relativePath) throws IOException {

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(relativePath);

        return inputStream;
    }

    /**
     * Reads application resource file as URL given thread context relative path.
     *
     * @param relativePath
     * @return
     * @throws IOException
     */
    public static URL getResourceURL(String relativePath) throws IOException {

        //ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return classLoader.getResource(relativePath);
    }


    /**
     * Reads File given relative path (inferred from classpath)
     *
     * @param relativePath
     * @return
     * @throws IOException
     */
    public static File getResource(String relativePath) throws IOException {

        URL resource = getResourceURL(relativePath);
        try {
            return Paths.get(resource.toURI()).toFile();
        } catch (Exception e) {
            throw (new IOException(" Not found " + relativePath, e));
        }
    }


    /**
     * Read the argument file date into byte array (in-memory)
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static byte[] read(File file) throws IOException {

        InputStream initialStream = new FileInputStream(file);
        byte[] buffer = new byte[initialStream.available()];
        initialStream.read(buffer);

        return buffer;
    }


    /**
     * Read a text file line by line.
     *
     * @param path
     * @param consumer
     * @throws IOException
     */
    public static void stream(String path, Consumer<String> consumer) throws IOException {

        Files.lines(Paths.get(path)).forEach(consumer);

    }


    /**
     * Read a text file line by line.
     *
     * @param file
     * @param consumer
     * @throws IOException
     */
    public static void stream(File file, Consumer<String> consumer) throws IOException {

        // TODO there is probably a more efficient way to do this, but Java8 streaming library seems to be very path-centric.
        stream(file.getPath(), consumer);
    }


    /**
     * @param name
     * @param data
     * @return
     * @throws IOException
     */
    public static File writeTempFile(String name, byte[] data) throws IOException {

        String path = System.getProperty("java.io.tmpdir") + "/" + name;

        return writeFile(path, data);
    }


    /**
     * @param name
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static File writeTempFile(String name, InputStream inputStream) throws IOException {

        String path = System.getProperty("java.io.tmpdir") + "/" + name;

        return writeFile(path, inputStream);
    }

    /**
     * @param path
     * @param data
     * @return
     * @throws IOException
     */
    public static File writeFile(String path, byte[] data) throws IOException {

        Path filePath = Paths.get(path);
        Files.write(filePath, data);

        return new File(path);
    }


    /**
     * @param path
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static File writeFile(String path, InputStream inputStream) throws IOException {

        File file = new File(path);
        FileUtils.copyInputStreamToFile(inputStream, file);

        return file;
    }


    public static String getMimeType(File file) {

        String name = file.getName();
        int suffixIndex = name.indexOf(".");
        String suffix = (suffixIndex > 0 && suffixIndex + 1 < name.length()) ? name.substring(suffixIndex + 1, name.length()) : null;

        return getMimeTypeByExtension(suffix);
    }


    public static String getMimeTypeByExtension(String extension) {

        // TODO support more types and possibly enumeration
        // https://www.sitepoint.com/web-foundations/mime-types-complete-list/
        if ("TXT".equalsIgnoreCase(extension)) {
            return "text/plain";
        } else if ("HTML".equalsIgnoreCase(extension)) {
            return "text/html";
        } else if ("PDF".equalsIgnoreCase(extension)) {
            return "application/pdf";
        } else if ("JPG".equalsIgnoreCase(extension)) {
            return "image/jpeg";
        } else if ("GIF".equalsIgnoreCase(extension)) {
            return "image/gif";
        } else if ("PNG".equalsIgnoreCase(extension)) {
            return "image/png";
        } else if ("JSON".equalsIgnoreCase(extension)) {
            return "application/json";
        } else if ("SVG".equalsIgnoreCase(extension)) {
            return "image/svg+xml";
        } else if ("RSVG".equalsIgnoreCase(extension)) {
            return "application/octet-stream";
        }

        return null;
    }

    /**
     * Rename a file
     *
     * @param oldFilepath
     * @param newFilename
     */
    public static void renameFile(final String oldFilepath, final String newFilename) {
        File file = new File(oldFilepath);
        file.renameTo(new File(file.getParentFile() + "/" + newFilename));
    }

    /**
     * Delete a file
     *
     * @param filepath
     */
    public static void deleteFile(final String filepath) {
        File file = new File(filepath);
        file.delete();
    }

    public static void removeFiles(List<String> fileNames) {
        for (String fileName : fileNames) {
            new File(fileName).delete();
        }
    }
}
