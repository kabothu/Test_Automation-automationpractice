package com.steps;

import com.DriverFactory;
import com.buildListeners.TestNGListener;
import com.buildSettings.ExcelEnvironment;
import com.buildSettings.TestCommons;
import com.buildSettings.buildPrettyMessage.PrettyMessageBuilder;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.slf4j.MDC;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;

import java.io.IOException;

/**
 * Test_Automation-automationpractice
 *
 * @author kamil.nowocin
 **/

public class Hooks extends DriverFactory implements ITestListener {

    /**
     * For TestNG -> @Test annotation
     **/
    @BeforeTest(description = "Setting up Excel File")
    public void dataSetup() {
        ExcelEnvironment.setExcelSheet(ExcelEnvironment.TEST_DATA_EXCEL_SHEET_NAME);
    }

    @BeforeMethod(description = "Setting up Test Class")
    public void beforeTest(ITestResult iTestResult) throws IOException {
        MDC.put("testid", PrettyMessageBuilder.getTestDescription(iTestResult));
        startBrowser();
        TestCommons.networkThrottling(false);
    }

    @AfterMethod(description = "Teardown Test Class")
    public void afterTest() {
        MDC.remove("testid");
        getDriver().close();
    }

    @AfterSuite(alwaysRun = true, description = "Teardown Test Suite")
    public void afterSuite() {
        destroyDriver();
    }

    /**
     * For Cucumber -> Feature file
     **/
    @Before
    public void beforeScenario(Scenario scenario) throws IOException {
        MDC.put("testid", scenario.getName().toUpperCase());
        TestNGListener.onScenarioStart(scenario);
        startBrowser();
        TestCommons.networkThrottling(false);
    }

    @After
    public void afterScenario(Scenario scenario) throws IOException {
        TestNGListener.onScenarioFinish(scenario);
        if (scenario.isFailed()) {
            localSaveScreenshotPNG(scenario);
            allureSaveScreenshotPNG();
            allureSaveTextLogCucumber(scenario);
        }
        MDC.remove("testid");
        destroyDriver();
    }
}