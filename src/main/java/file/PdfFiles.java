package file;


import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import exception.ValidationException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import util.FileUtil;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * All functions that create PDF files.
 *
 * @author bsun
 */
public class PdfFiles implements FileGenerator {
//    private static final Logger logger = LoggerFactory.getLogger(PdfFiles.class);

    private static final String COVER_PAGE_PDF_FILE_NEW_POSTFIX = ".coverpage.pdf";
    private static final float MERGE_PDF_COMPRESS_RATIO = 1.F;
    private static final int MERGE_PDF_HEADERANDFOOTER_PAGE_NUMBER = 1;
    private static final int MERGE_PDF_VALUE_ZERO = 0;
    private static final Integer COVER_PAGE_CONTENT_START_POSITION_Y = 700;
    private static final int COMMAND_PARAMETER_LENGTH = 11;

    private final byte[] filePathOfHeaderAndFooterPage;
    private final List<String> htmlFiles;

    static String[] BASE_COMMAND_ARRAY = new String[COMMAND_PARAMETER_LENGTH];

    static {
//        BASE_COMMAND_ARRAY[0] = "/usr/local/bin/wkhtmltopdf";
        BASE_COMMAND_ARRAY[0] = "wkhtmltopdf";
        BASE_COMMAND_ARRAY[1] = "--page-size";
        BASE_COMMAND_ARRAY[2] = "Letter";
        BASE_COMMAND_ARRAY[3] = "--margin-top";
        BASE_COMMAND_ARRAY[4] = "35";
        BASE_COMMAND_ARRAY[5] = "--margin-bottom";
        BASE_COMMAND_ARRAY[6] = "30";
        BASE_COMMAND_ARRAY[7] = "--margin-left";
        BASE_COMMAND_ARRAY[8] = "5";
        BASE_COMMAND_ARRAY[9] = "--margin-right";
        BASE_COMMAND_ARRAY[10] = "5";
    }

    private PdfFiles(Initializer initializer) {
        this.filePathOfHeaderAndFooterPage = initializer.filePathOfHeaderAndFooterPage;
        this.htmlFiles = initializer.htmlFiles;
    }

    public static class Initializer {
//        private final Logger innerClassLogger = LoggerFactory.getLogger(PdfFiles.Initializer.class);
        private byte[] filePathOfHeaderAndFooterPage;
        private List<String> htmlFiles;

        public Initializer setBlankPdfPage(byte[] filePathOfHeaderAndFooterPage) throws ValidationException {
            if (filePathOfHeaderAndFooterPage == null) {
//                innerClassLogger.error("PdfFiles Initializer::blankPdfPage is null.");
                throw new ValidationException("PdfFiles Initializer::blankPdfPage is null.");
            }
            this.filePathOfHeaderAndFooterPage = filePathOfHeaderAndFooterPage;
            return this;
        }

        public Initializer setHtmlFiles(List<String> htmlFiles) throws ValidationException {
            if (htmlFiles == null) {
//                innerClassLogger.error("PdfFiles Initializer::htmlFile List is null.");
                throw new ValidationException("PdfFiles Initializer::htmlFile List is null.");
            }
            this.htmlFiles = htmlFiles;
            return this;
        }

        public PdfFiles initialize() throws ValidationException {
            pdfInitializerValidation(Initializer.class);
            return new PdfFiles(this);
        }

        private void pdfInitializerValidation(Class clazz) throws ValidationException {
            for (Field f : clazz.getDeclaredFields()) {
                try {
                    if (f.get(this) == null) {
                        throw new ValidationException(clazz.getCanonicalName() + "'s " + f.getName() + " field is null. It is a required field. Please check if the field has been initialized properly.");
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    throw new ValidationException(e.getMessage());
                }
            }
        }
    }

    /**
     * Giving the html file names and empty cover page with only header and footer.
     * The function will convert html files to pdf file without header and footer,
     * then merge with empty cover page pdf file with only header and footer.
     *
     * @return File path to the final cover page Pdf file.
     * @throws IOException
     * @throws DocumentException
     */
    @Override
    public List<String> generateFiles() throws ValidationException {
        List<String> pdfFileList = new ArrayList<>();
        String coverPageWithoutHeaderAndFooter = generateCoverPageWithoutHeaderAndFooter(htmlFiles);
        try {
            pdfFileList.add(mergePdfFiles(coverPageWithoutHeaderAndFooter, filePathOfHeaderAndFooterPage));
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
            throw new ValidationException(e.getMessage());
        }
        return pdfFileList;
    }


    /**
     * Run wkhtmltopdf command to generate PDFs.
     *
     * @param htmlFileNames
     * @return Pdf file name.
     * @throws Exception
     */
    private String generateCoverPageWithoutHeaderAndFooter(List<String> htmlFileNames) throws ValidationException {
        String pdfFileName = generatePdfFileName(htmlFileNames);
        String[] wkCommand = generateWKCommand(htmlFileNames, pdfFileName);
        try {
            runWKCommand(wkCommand);
        } catch (IOException | PageFileGeneratorException e) {
            e.printStackTrace();
            throw new ValidationException(e.getMessage());
        }
        FileUtil.removeFiles(htmlFileNames);
        return pdfFileName;
    }

    private void runWKCommand(String[] wkCommand) throws IOException, PageFileGeneratorException {
        ProcessBuilder builder = new ProcessBuilder(wkCommand);
        builder.redirectErrorStream(true);
        Process process = builder.start();

        try (InputStream stream = process.getInputStream();
             InputStreamReader inReader = new InputStreamReader(stream);
             BufferedReader bReader = new BufferedReader(inReader);) {

            String line = bReader.readLine();
            while (line != null) {
                line = bReader.readLine();
            }
        } catch (IOException e) {
//            logger.error("Exception at PdfFiles::runWKCommand.", e);
            throw new PageFileGeneratorException("Exception at PdfFiles::runWKCommand::" + e.getMessage());
        }
    }

    protected String generatePdfFileName(List<String> htmlFileNames) throws ValidationException {
        if (htmlFileNames == null || htmlFileNames.size() < 1) {
            String emptyOrNull = htmlFileNames == null ? "null" : "empty";
//            logger.error("PdfFiles::generatePdfFileName::htmlFileNames list is " + emptyOrNull + ".");
            throw new ValidationException("PdfFiles::generatePdfFileName::Input htmlFileNames is " + emptyOrNull + ".");
        }
        String firstHtmlFileName = htmlFileNames.get(0);
        return replaceFileName(firstHtmlFileName);
    }

    private String replaceFileName(String fileWithPath) {
        String fileName = FilenameUtils.getName(fileWithPath);
        int pos = StringUtils.ordinalIndexOf(fileName, ".", 2);
        String pdfFileName = fileName.substring(0, pos) + COVER_PAGE_PDF_FILE_NEW_POSTFIX;
        return fileWithPath.replace(fileName,pdfFileName);
    }

    private String[] generateWKCommand(List<String> htmlFileNames, String pdfFileName) {
        // the +1 is for storing the output pdf file name.
        String[] cmdArray = new String[BASE_COMMAND_ARRAY.length + htmlFileNames.size() + 1];
        populateBaseCommand(cmdArray);
        populateHtmlNamesInCommands(cmdArray, htmlFileNames);
        populateOutcomePdfFileNameInCommands(cmdArray, pdfFileName);
//        for (int i = 0; i < cmdArray.length; i++) {
//            logger.info("Convert html to pdf command line: {}", cmdArray[i]);
//        }
        return cmdArray;
    }

    private void populateBaseCommand(String[] commandArray) {
        for (int i = 0; i < BASE_COMMAND_ARRAY.length; i++) {
            commandArray[i] = BASE_COMMAND_ARRAY[i];
        }
    }

    private void populateHtmlNamesInCommands(String[] commandArray, List<String> htmlFileNames) {
        for (int i = BASE_COMMAND_ARRAY.length, j = 0; i < commandArray.length && j < htmlFileNames.size(); i++, j++) {
            commandArray[i] = htmlFileNames.get(j);
        }
    }

    private void populateOutcomePdfFileNameInCommands(String[] commandArray, String pdfFileName) {
        commandArray[commandArray.length - 1] = pdfFileName;
    }

    private String mergePdfFiles(String coverPage, byte[] headerAndFooter) throws IOException, DocumentException {
        String outputFilePath = coverPage.replace(".coverpage.pdf", ".coverpage.final.pdf");
        PdfReader coverPageReader = new PdfReader(coverPage);
//        PdfReader red = new PdfReader(FileUtil.read(new File("") ));
        PdfReader headerAndFooterReader = new PdfReader(headerAndFooter);

        mergePageByPage(coverPageReader, headerAndFooterReader, outputFilePath);
        coverPageReader.close();
        headerAndFooterReader.close();
        FileUtil.deleteFile(coverPage);

        return outputFilePath;
    }

    private void mergePageByPage(PdfReader coverPageReader, PdfReader headerAndFooterReader, String outputFilePath) throws FileNotFoundException, DocumentException {
        Rectangle pageFrame = coverPageReader.getPageSize(MERGE_PDF_HEADERANDFOOTER_PAGE_NUMBER);

        float outputPageWidth = pageFrame.getWidth();
        float outputPageHeight = pageFrame.getHeight();
        float upperRightX = outputPageWidth;
        float upperRightY = COVER_PAGE_CONTENT_START_POSITION_Y;
        float lowerLeftX = 0;
        float lowerLeftY = 0;

        Document outputPageDocumentUnit = new Document(new Rectangle(outputPageWidth, outputPageHeight));
        PdfWriter outputFileWriter = PdfWriter.getInstance(outputPageDocumentUnit, new FileOutputStream(outputFilePath));
        outputPageDocumentUnit.open();
        PdfContentByte pdfContentByte = outputFileWriter.getDirectContent();

        PdfImportedPage headerAndFooterToBeImported = outputFileWriter.getImportedPage(headerAndFooterReader, MERGE_PDF_HEADERANDFOOTER_PAGE_NUMBER);
        for (int coverPageNumber = 1; coverPageNumber <= coverPageReader.getNumberOfPages(); coverPageNumber++) {
            outputPageDocumentUnit.newPage();
            PdfImportedPage coverPageToBeImported = outputFileWriter.getImportedPage(coverPageReader, coverPageNumber);
            coverPageToBeImported.setBoundingBox(new Rectangle(lowerLeftX, lowerLeftY, upperRightX, upperRightY));
            pdfContentByte.addTemplate(headerAndFooterToBeImported, MERGE_PDF_COMPRESS_RATIO, MERGE_PDF_VALUE_ZERO, MERGE_PDF_VALUE_ZERO, MERGE_PDF_COMPRESS_RATIO, MERGE_PDF_VALUE_ZERO, MERGE_PDF_VALUE_ZERO);
            pdfContentByte.addTemplate(coverPageToBeImported, MERGE_PDF_COMPRESS_RATIO, MERGE_PDF_VALUE_ZERO, MERGE_PDF_VALUE_ZERO, MERGE_PDF_COMPRESS_RATIO, MERGE_PDF_VALUE_ZERO, MERGE_PDF_VALUE_ZERO);
        }
        outputPageDocumentUnit.close();
        outputFileWriter.close();
    }
}
