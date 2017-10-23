package util;

import org.testng.Assert;
import org.testng.annotations.Test;


public class FileUtilTest {

    @Test
    public void getCurrentDirectoryTest() {
        String currentDirectory = FileUtil.getCurrentDirectory();
        Assert.assertNotNull(currentDirectory);
//        Assert.assertTrue(currentDirectory.toLowerCase().contains("velocity"),
//                "Current directory is " + currentDirectory);
    }


}