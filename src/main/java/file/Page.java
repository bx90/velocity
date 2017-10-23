package file;


import bean.ProjectTemplateMapping;
import exception.ValidationException;
import util.FileUtil;
import velocityengine.HtmlVelocityEngine;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;


/**
 * @Author bsun
 */


public class Page implements FileGenerator {

    private final byte[] blankPdfPage;
    private final String finalPageDirectory;
    // This object contains the path to the template. Can be replaced by
    // other objects in future base on the requirement.
    private final List<ProjectTemplateMapping> projectTemplateMappingList;
    private final Map<String, Object> objectMaps;

    private Page(Initializer initializer) {
        blankPdfPage = initializer.blankPdfPage;
        projectTemplateMappingList = initializer.projectTemplateMappingList;
        objectMaps = initializer.objectMaps;
        finalPageDirectory = initializer.finalPageDirectory;
    }

    public static class Initializer {
//        private final Logger innerClassLogger = LoggerFactory.getLogger(CoverPage.Initializer.class);

        private byte[] blankPdfPage;
        private List<ProjectTemplateMapping> projectTemplateMappingList;
        private Map<String, Object> objectMaps;
        private String finalPageDirectory;

        private Initializer setBlankPdfPageFile(byte[] blankPdfPage) throws ValidationException {
            if (blankPdfPage == null) {
//                innerClassLogger.error("CoverPage Initializer::blankPdfPage List is null.");
                throw new ValidationException("CoverPage Initializer::blankPdfPage List is null.");
            }
            this.blankPdfPage = blankPdfPage;
            return this;
        }

        private Initializer setProjectTemplateMappingList(List<ProjectTemplateMapping> projectTemplateMappings) throws ValidationException {
            if (projectTemplateMappings == null) {
//                innerClassLogger.error("CoverPage Initializer::projectTemplateMappings List is null.");
                throw new ValidationException("CoverPage Initializer::projectTemplateMappings List is null.");
            }
            this.projectTemplateMappingList = projectTemplateMappings;
            return this;
        }

        private Initializer setDataObjectList(Map<String, Object> objectMaps) throws ValidationException {
            if (objectMaps == null) {
//                innerClassLogger.error("CoverPage Initializer::objectMaps List is null.");
                throw new ValidationException("CoverPage Initializer::objectMaps List is null.");
            }
            this.objectMaps = objectMaps;
            return this;
        }

        private Initializer setFinalPageDirectory(String finalPageDirectory) throws ValidationException {
            if (finalPageDirectory == null) {
//                innerClassLogger.error("CoverPage Initializer::finalPageDirectory List is null.");
                throw new ValidationException("CoverPage Initializer::finalPageDirectory List is null.");
            }
            this.finalPageDirectory = finalPageDirectory;
            return this;
        }

        private Page initialize() throws ValidationException {
            coverPageInitializerValidation(Page.Initializer.class);
            return new Page(this);
        }


        // Check if all mandatory fields are properly initialized before converting to the final HTML object.
        private void coverPageInitializerValidation(Class clazz) throws ValidationException {
            for (Field f : clazz.getDeclaredFields()) {
                try {
                    if (f.get(this) == null) {
//                        innerClassLogger.error("{}'s {} field is null. It is a mandatory field. Please check if the field has been initialized properly.", clazz.getCanonicalName(), f.getName());
                        throw new ValidationException(clazz.getCanonicalName() + "'s " + f.getName() + " field is null. It is a required field. Please check if the field has been initialized properly.");
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    throw new ValidationException(e.getMessage());
                }
            }
        }
    }


    public static byte[] generateCoverpage(byte[] emptyPage, Map<String, Object> objectMap, List<ProjectTemplateMapping> projectTemplateMappingList) throws ValidationException {
        FileGenerator coverPage = new Page.Initializer()
                .setBlankPdfPageFile(emptyPage)
                .setProjectTemplateMappingList(projectTemplateMappingList)
                .setFinalPageDirectory(projectTemplateMappingList.get(0).getTempFilePath())
                .setDataObjectList(objectMap)
                .initialize();

        String pdfFilePath = coverPage.generateFiles().get(0);
        byte[] coverPageByteArr = new byte[0];
        try {
            coverPageByteArr = FileUtil.read(new File(pdfFilePath));
        } catch (IOException e) {
            e.printStackTrace();
            throw new ValidationException(e.getMessage());
        }
        FileUtil.deleteFile(pdfFilePath);

        return coverPageByteArr;

    }


    @Override
    public List<String> generateFiles() throws ValidationException {

        // Initialize Velocity Engine.
        HtmlVelocityEngine coverPageEngine = new HtmlVelocityEngine.Engine()
                .setVelocityEngine()
                .setVelocityContext()
                .ignite();

        // Create HTML files.
        FileGenerator htmlFiles = new HtmlFiles.Initializer()
                .setVelocityEngine(coverPageEngine.getEngine())
                .setVelocityContext(coverPageEngine.getContext())
                .setTempFilePath(finalPageDirectory)
                .setProjectTemplateMappingList(projectTemplateMappingList)
                .setObjectList(objectMaps)
                .initialize();
        List<String> htmlFilesList = htmlFiles.generateFiles();

        // Create PDF files.
        FileGenerator pdfFiles = new PdfFiles.Initializer()
                .setBlankPdfPage(blankPdfPage)
                .setHtmlFiles(htmlFilesList)
                .initialize();

        return pdfFiles.generateFiles();
    }
}