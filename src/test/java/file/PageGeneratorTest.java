package file;

import bean.Person;
import bean.ProjectTemplateMapping;
import exception.ValidationException;
import org.apache.commons.io.FileUtils;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author bsun
 */
public class PageGeneratorTest {
    private Map<String, Object> objectMaps = new HashMap<>();
    private List<ProjectTemplateMapping> projectTemplateMappings = new LinkedList<>();
    private byte[] empytPage;
    private static final String outputFile = FileUtil.getCurrentDirectory() + "/src/test/resources/output.pdf";


    @BeforeTest
    public void setup() throws IOException {
        objectMaps.put("person", new Person("FN", "SD", 10));
        projectTemplateMappings.add(new ProjectTemplateMapping("1", "MIR_01",
                "template.vm","1", "1",
                "/tmp/"));
        String emptyPagePath = FileUtil.getCurrentDirectory() + "/src/test/resources/emptypdf.pdf";

        Path path = Paths.get(emptyPagePath);
        empytPage = Files.readAllBytes(path);
    }

    @Test
    public void test() throws ValidationException, IOException {
        PageGenerator page = new PageGenerator(projectTemplateMappings, objectMaps, empytPage);
        FileUtils.writeByteArrayToFile(new File(outputFile),
                page.generateCoverPage());
    }

    @AfterTest
    public void tearDown() {
        System.out.println("PDF file has been generated in " + outputFile);
    }
}