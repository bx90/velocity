package file;

import bean.ProjectTemplateMapping;
import exception.ValidationException;

import java.util.List;
import java.util.Map;

/**
 * @author bsun
 */
public class PageGenerator {
//    private static Logger logger = LoggerFactory.getLogger(CoverPageGenerator.class);
    private List<ProjectTemplateMapping> projectTemplateMappings;
    private Map<String, Object> objectMaps;
    private byte[] emptyPage;

    public PageGenerator(List<ProjectTemplateMapping> projectTemplateMappings, Map<String, Object> objectMaps, byte[] emptyPage) {
        this.projectTemplateMappings = projectTemplateMappings;
        this.objectMaps = objectMaps;
        this.emptyPage = emptyPage;
    }

    public byte[] generateCoverPage() throws ValidationException {
        return Page.generateCoverpage(emptyPage, objectMaps, projectTemplateMappings);
    }

}
