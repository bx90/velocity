package file;

import bean.ProjectTemplateMapping;
import exception.ValidationException;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * All functions that create HTML files.
 *
 * @author bsun
 */
public class HtmlFiles implements FileGenerator {
//    private final Logger logger = LoggerFactory.getLogger(HtmlFiles.class);
    private static final String ENCODING = "UTF-8";

    private final VelocityEngine htmlEngine;
    private final VelocityContext htmlContext;
    private final String tempFilePath;
    private final List<ProjectTemplateMapping> projectTemplateMappingList;
    private final Map<String, Object> objectMap;

    private HtmlFiles(Initializer initializer) {
        this.htmlEngine = initializer.htmlEngine;
        this.htmlContext = initializer.htmlContext;
        this.tempFilePath = initializer.tempFilePath;
        this.projectTemplateMappingList = initializer.projectTemplateMappingList;
        this.objectMap = initializer.objectMap;
    }

    static class Initializer {
//        private final Logger innerClassLogger = LoggerFactory.getLogger(HtmlFiles.Initializer.class);
        private VelocityEngine htmlEngine;
        private VelocityContext htmlContext;
        private String tempFilePath;
        private List<ProjectTemplateMapping> projectTemplateMappingList;
        private Map<String, Object> objectMap;

        // This part is supporting live update.
        // While your application is running, the template can be updated.
        Initializer setVelocityEngine(VelocityEngine htmlEngine) throws ValidationException {
            initializerValidationHelper(htmlEngine, VelocityEngine.class);
            htmlEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
            htmlEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
            this.htmlEngine = htmlEngine;
            return this;
        }

        Initializer setVelocityContext(VelocityContext htmlContext) throws ValidationException {
            initializerValidationHelper(htmlContext, VelocityContext.class);
            this.htmlContext = htmlContext;
            return this;
        }

        Initializer setTempFilePath(String tempFilePath) throws ValidationException {
            initializerValidationHelper(tempFilePath, String.class);
            this.tempFilePath = tempFilePath;
            return this;
        }

        Initializer setProjectTemplateMappingList(List<ProjectTemplateMapping> projectTemplateMappings) throws ValidationException {
            initializerValidationHelper(projectTemplateMappings, ProjectTemplateMapping.class);
            Collections.sort(projectTemplateMappings);
            this.projectTemplateMappingList = projectTemplateMappings;
            return this;
        }

        Initializer setObjectList(Map<String, Object> objectMap) throws ValidationException {
            initializerValidationHelper(objectMap, Object.class);
            this.objectMap = objectMap;
            return this;
        }

        HtmlFiles initialize() throws ValidationException {
            htmlInitializerValidation(Initializer.class);
            return new HtmlFiles(this);
        }


        private void initializerValidationHelper(Object object, Class clazz) throws ValidationException {
            if (object == null) {
//                innerClassLogger.error("HtmlFiles::Initializer::{} object is null.", clazz.getSimpleName());
                throw new ValidationException("Exception happened while initializing HtmlFile Object. " +
                        "The " + clazz.getSimpleName() + " is null.");
            }
        }

        // Check if all mandatory fields are properly initialized before converting to the final HTML object.
        private void htmlInitializerValidation(Class<Initializer> clazz) throws  ValidationException {
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

    @Override
    public List<String> generateFiles() throws ValidationException {
        populateVelocityContext(htmlContext, objectMap);
        return generateHtmlFileWithVelocityTemplate("test1", "test2");
    }


    /**
     * Use put function to provide the object name and object to context property.
     * e.g.: Adding GHMirati object, the function will add
     *
     * @param htmlContext
     * @param dataObjects
     */
    private void populateVelocityContext(VelocityContext htmlContext, Map<String, Object> dataObjects) {
        dataObjects.forEach((key, value) -> htmlContext.put(key, value));
    }

    /**
     * Generate temporary HTML files.
     *
     * @param sampleId
     * @param runId
     * @return List of HTML files.
     * @throws IOException
     */
    private List<String> generateHtmlFileWithVelocityTemplate(String sampleId, String runId) throws ValidationException {
        List<String> htmlFileList = new ArrayList<>();
        String fileName = null;

        for (int index = 0; index < projectTemplateMappingList.size(); index++) {
            fileName = generateFileName(sampleId, runId, projectTemplateMappingList.get(index).getTemplateFileName(), tempFilePath, index + 1);
            htmlFileList.add(fileName);
            try (final OutputStream out = new FileOutputStream(fileName); // Output file Name
                 final Writer writer = new OutputStreamWriter(out, Charset.forName(ENCODING))) {
                htmlEngine.mergeTemplate(projectTemplateMappingList.get(index).getTemplateFileName(), ENCODING, htmlContext, writer);
            } catch (IOException e) {
//                logger.info("Exception at generateHtmlFileWithVelocityTemplate", e);
                e.printStackTrace();
                throw new ValidationException(e.getMessage());
            }
        }
        return htmlFileList;
    }

    private String generateFileName(String sampleId, String runId, String templateName, String htmlFilePath, int index) {
        String postfix = templateName == null ? null : templateName.toLowerCase().contains("footer") ? "footer.html" : index + ".html";
        return htmlFilePath + sampleId + "." + runId + ".coverpage." + postfix;
    }
}
