package bean;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author bsun
 */

public class ProjectTemplateMapping implements Comparable {
    private String id;
    private String projectId;
    private String templateFileName;
    private String displaySequence;
    private String templateVersion;

    public ProjectTemplateMapping(String id, String projectId, String templateFileName, String displaySequence,
                                  String templateVersion, String tempFilePath) {
        this.id = id;
        this.projectId = projectId;
        this.templateFileName = templateFileName;
        this.displaySequence = displaySequence;
        this.templateVersion = templateVersion;
        this.tempFilePath = tempFilePath;
    }

    private String tempFilePath;

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ProjectTemplateMapping{");
        sb.append("id='").append(id).append('\'');
        sb.append(", projectId='").append(projectId).append('\'');
        sb.append(", templateFileName='").append(templateFileName).append('\'');
        sb.append(", displaySequence='").append(displaySequence).append('\'');
        sb.append(", templateVersion='").append(templateVersion).append('\'');
        sb.append(", tempFilePath='").append(tempFilePath).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getTemplateFileName() {
        return templateFileName;
    }

    public void setTemplateFileName(String templateFileName) {
        this.templateFileName = templateFileName;
    }

    public String getDisplaySequence() {
        return displaySequence;
    }

    public void setDisplaySequence(String displaySequence) {
        this.displaySequence = displaySequence;
    }

    public String getTemplateVersion() {
        return templateVersion;
    }

    public void setTemplateVersion(String templateVersion) {
        this.templateVersion = templateVersion;
    }

    public String getTempFilePath() {
        return tempFilePath;
    }

    public void setTempFilePath(String tempFilePath) {
        this.tempFilePath = tempFilePath;
    }
    @Override
    public int compareTo(Object o) {
        return Integer.compare(Integer.parseInt(this.getDisplaySequence()), Integer.valueOf(((ProjectTemplateMapping) o).getDisplaySequence()));
    }

    public String getId() {
        return null;
    }

    public Boolean matchProjectId(String projectId) {
        return this.projectId.equalsIgnoreCase(projectId);
    }
    public static List<ProjectTemplateMapping> getRecordsBasedOnProjectId(List<ProjectTemplateMapping> projectTemplateMappings, String projectId) {
       return projectTemplateMappings.stream()
                                     .filter(map -> map.matchProjectId(projectId))
                                     .collect(Collectors.toList());
    }
}
