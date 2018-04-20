/**
 * 
 */
package ExtentReportDemo.ExtentReportDemo.com;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

/**
 * @author satyawan.pawar
 *
 */
public class DemoExtReport {

	ExtentReports extent;
	ExtentTest logger;
	WebDriver driver;

	@BeforeTest
	public void startReport() {
		extent = new ExtentReports(System.getProperty("user.dir") + "/test-output/STMExtentReport.html", true);
		extent.addSystemInfo("Host Name", "SoftwareTestingMaterial").addSystemInfo("Environment", "Automation Testing")
				.addSystemInfo("User Name", "Rajkumar SM");
		extent.loadConfig(new File(System.getProperty("user.dir") + "\\extent-config.xml"));
	}

	@Test
	public void passTest() {
		// extent.startTest("TestCaseName", "Description")
		// TestCaseName – Name of the test
		// Description – Description of the test
		// Starting test
		logger = extent.startTest("passTest");
		Assert.assertTrue(true);
		// To generate the log when the test case is passed
		logger.log(LogStatus.PASS, "Test Case Passed is passTest");
	}

//	@Test
//	public void failTest() {
//		logger = extent.startTest("failTest");
//		System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "//libs//chromedriver.exe");
//		driver = new ChromeDriver();
//		driver.get("https://www.softwaretestingmaterial.com");
//		String currentURL = driver.getCurrentUrl();
//		Assert.assertEquals(currentURL, "NoTitle");
//		logger.log(LogStatus.PASS, "Test Case (failTest) Status is passed");
//	}
//
//	@Test
//	public void skipTest() {
//		logger = extent.startTest("skipTest");
//		throw new SkipException("Skipping - This is not ready for testing ");
//	}

	@AfterMethod
	public void getResult(ITestResult result) throws Exception {
		if (result.getStatus() == ITestResult.FAILURE) {
			logger.log(LogStatus.FAIL, "Test Case Failed is " + result.getName());
			logger.log(LogStatus.FAIL, "Test Case Failed is " + result.getThrowable());
			// To capture screenshot path and store the path of the screenshot in the string
			// "screenshotPath"
			// We do pass the path captured by this mehtod in to the extent reports using
			// "logger.addScreenCapture" method.
			String screenshotPath = DemoExtReport.getScreenshot(driver, result.getName());
			// To add it in the extent report
			logger.log(LogStatus.FAIL, logger.addScreenCapture(imgToBase64(screenshotPath)));
		} else if (result.getStatus() == ITestResult.SKIP) {
			logger.log(LogStatus.SKIP, "Test Case Skipped is " + result.getName());
		}
		extent.endTest(logger);
	}

	@AfterTest
	public void endReport() {
		extent.flush();
		extent.close();
	}

	public static String getScreenshot(WebDriver driver, String screenshotName) throws Exception {
		String dateName = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
		TakesScreenshot ts = (TakesScreenshot) driver;
		File source = ts.getScreenshotAs(OutputType.FILE);
		// after execution, you could see a folder "FailedTestsScreenshots" under src
		// folder
		String destination = System.getProperty("user.dir") + "/FailedTestsScreenshots/" + screenshotName + dateName
				+ ".png";
		File finalDestination = new File(destination);
		FileUtils.copyFile(source, finalDestination);
		// Returns the captured file path
		return destination;

	}

	public static String imgToBase64(String img) throws IOException {
		File f = new File(img); // change path of image according
		// to you
		FileInputStream fis = new FileInputStream(f);
		byte byteArray[] = new byte[(int) f.length()];
		fis.read(byteArray);
		String imageString = Base64.getEncoder().encodeToString(byteArray);
		return "data:image/png;base64," + imageString;
	}

}
