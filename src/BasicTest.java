import atu.testrecorder.ATUTestRecorder;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.TouchAction;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.remote.DesiredCapabilities;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.math.BigInteger;
import java.net.URL;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BasicTest {

    private static final Moshi MOSHI = new Moshi.Builder().build();
    private static final JsonAdapter<List<Build>> BUILDS_JSON_ADAPTER = MOSHI.adapter(
            Types.newParameterizedType(List.class, Build.class));
    static class Build {
        String name;
        String href;
    }
    private AppiumDriver driver;
    private OCR ocr;


    private static String imgDir = System.getProperty("user.dir") + "/res/";
    private static String reportDir = System.getProperty("user.dir") + "/reports/";
    private static String buildtDir = System.getProperty("user.dir") + "/builds/";
    private static double DEFAULT_MIN_SIMILARITY = 0.8;
    private ATUTestRecorder recorder;


    @Before
    public void setUp() throws Exception {

        //Start Appium server programmatically
        Process p = Runtime.getRuntime().exec("appium");
        Thread.sleep(5000);

        DateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH-mm-ss");
        Date date = new Date();


        DesiredCapabilities desired_caps = new DesiredCapabilities();
        desired_caps.setCapability("platFormName","Android");
        desired_caps.setCapability("platformName","Android");
        desired_caps.setCapability("platformVersion","6.0.1");
        desired_caps.setCapability("deviceName","ZY223KL976");


        // For new installation
        //String appPath = "/Users/shumakove/Documents/KT_Builds/Google/KT_HD_Google_Global_Review_api15_v2.17.1_rev127643_c3681643_14-04-2017_release-test-releaseTest.apk";
        //desired_caps.setCapability("app",appPath);
        desired_caps.setCapability("appPackage","com.zeptolab.thieves.google");
        desired_caps.setCapability("appActivity","com.zeptolab.thieves.ThievesActivity");

        try {
            driver = new AppiumDriver(new URL("http://127.0.0.1:4723/wd/hub"), desired_caps);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ocr = new OCR(driver);
        recorder = new ATUTestRecorder(reportDir,"Test-" + dateFormat.format(date),false);
        recorder.start();

    }

    public void getLogs() {
        LogEntries entireLogBuffer = driver.manage().logs().get("logcat");
        Iterator<LogEntry> logIter = entireLogBuffer.iterator();
        while(logIter.hasNext()) {
            LogEntry entry = logIter.next();
            System.out.println(entry.getMessage());
        }
    }



    @Test
    public void tutorialStage() throws Exception {
        TouchAction action = new TouchAction(driver);

        int width = driver.manage().window().getSize().getWidth();
        int height = driver.manage().window().getSize().getHeight();

        // Testing coppa screen
        Thread.sleep(8000);
        BufferedImage currentScreen  = ocr.takeScreenshot();
        ocr.saveToFile(reportDir+"coppaScreen.png",currentScreen);

        ocr.waitUntilImageExists(imgDir + "coppa.png",currentScreen,2);
        Point2D coppaTopCoords = ocr.getCoords(currentScreen,imgDir + "coppaTop.png");
        System.out.println((int)coppaTopCoords.getX() + " " + (int)coppaTopCoords.getY());
        Point2D coppaBottomCoords = ocr.getCoords(currentScreen,imgDir + "coppaBottom.png");
        System.out.println((int)coppaBottomCoords.getX() + " " + (int)coppaBottomCoords.getY());
        for(int i = 0; i < 5; i++) {
            action.longPress((int) coppaTopCoords.getX(), (int) coppaTopCoords.getY())
                    .moveTo((int) coppaBottomCoords.getX(), (int) coppaBottomCoords.getY()).release().perform();
        }
        for(int i = 0; i < 2; i++) {
            action.longPress((int) coppaBottomCoords.getX(), (int) coppaBottomCoords.getY())
                    .moveTo((int) coppaTopCoords.getX(), (int) coppaTopCoords.getY()).release().perform();

        }

        ocr.clickByImage(imgDir + "coppaOkButton.png",currentScreen);
        getLogs();

        // Testing nickname screen
        Thread.sleep(8000);
        currentScreen  = ocr.takeScreenshot();
        ocr.saveToFile(reportDir+"nickScreen.png",currentScreen);
        ocr.waitUntilImageExists(imgDir + "nicknameOkButton.png",currentScreen,8);
        for(int i = 0; i < 3; i++) {
            SecureRandom random = new SecureRandom();
            driver.findElementByClassName("android.widget.EditText").sendKeys(new BigInteger(130, random).toString(32));
            driver.findElementByClassName("android.widget.EditText").clear();
            ocr.clickByImage(imgDir + "nicknameOkButton.png",currentScreen);
        }

        SecureRandom random = new SecureRandom();
        driver.findElementByClassName("android.widget.EditText").sendKeys(new BigInteger(130, random).toString(32));
        ocr.clickByImage(imgDir + "nicknameOkButton.png",currentScreen);
        getLogs();

        // Testing first screen of tutorial
        Thread.sleep(15000);
        currentScreen  = ocr.takeScreenshot();
        ocr.saveToFile(reportDir+"tutorialStage1.png",currentScreen);
        ocr.waitUntilImageExists(imgDir + "tutorialStage1.png",currentScreen,2);
        driver.tap(1, width / 2, height / 2, 1);
        getLogs();

        Thread.sleep(3000);
        currentScreen  = ocr.takeScreenshot();
        ocr.saveToFile(reportDir+"tutorialStage1Start.png",currentScreen);
        ocr.waitUntilImageExists(imgDir + "tutorialStage1Start.png",currentScreen,2);
        getLogs();


    }


    @After
    public void tearDown() throws Exception {

        System.out.println("tearDown() called");
        Thread.sleep(2000);
        driver.quit();

    }
}