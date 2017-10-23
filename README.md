# About 
The code lives in the repository can generate PDF file from HTML files.<br>
Specificically, it will
1. Generate HTML file using `Apache Velocity`.
2. Convert HTML files to PDF file(s) using `wkhtmltopdf`.
3. (Optional) Merge generated PDF file with another existing PDF file using `iText`.

# How to run it
## Prerequisite 
#### Install `wkhtmltopdf` on MacBook

**`wkhtmltopdf` is a third party software that will be used when generating HTML files.** <br><br>
If `wkhtmltopdf` needs to be run on local MacBook, please go through the following steps:

- Visit [here](https://wkhtmltopdf.org/downloads.html) to find the proper version. Suggest to use `Stable` versions.
- Once the installation package is downloaded on MacBook, right click the package and click `Open`.
- Then follow the instructions to complete the installation.


To validate if `wkhtmltopdf` has been install successfully or not:

By program location:
- Open `terminal`. *N.B.: Please quit and reopen `terminal` if it is been running.*
- Type command `which wkhtmltopdf` to check where the program is installed. 
- The location should be `/usr/local/bin/wkhtmltopdf`

By version number:
- Open `terminal`. *N.B.: Please quit and reopen `terminal` if it is been running.*
- Type command `wkhtmltopdf -V` to check the installed version. 
- The version should match the one that you've selected on the download page.


## Import 
The project is built with `Gradle`. And the preferable IDE is `IntelliJ IDEA`. Run through simple `Gradle` import wizard<br>
in `IntelliJ IDEA`. 

## Run
The test function lives in `/src/test/java/file/PageGeneratorTest.java`. After running the test case in the file. The <br>
file PDF file will be generated.

