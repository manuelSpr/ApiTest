package config;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.internal.Utils;

import java.io.IOException;
import java.lang.reflect.Method;
import support.CustomAssertion;
import support.LocalReportingFunctional;
import support.LocalReportingValidation;
import support.SysUtil;
import static support.CustomAssertion.assert_messages;
import static support.ConstantData.*;
import static support.SysUtil.*;
import static support.ConstantData.CONFIGURATION_YAML;

public class Hooks {

    protected static RequestSpecification httpRequest = null;
    protected CustomAssertion m_custom = new CustomAssertion();

    /**
     *
     * @throws IOException
     */
    @BeforeSuite
    public static void setUp() throws IOException {
        Reporter.log("Setting up suite hooks", true);
        SysUtil utils = new SysUtil();
        System.setProperty("com.sun.security.enableAIAcaIssuers", "true"); // SSL option needed for JAMA integration- equivalent to vm option "-Dcom.sun.security.enableAIAcaIssuers=true"
        utils.CreateJsonFile(TESTS_RESULTS_JSON_FILE);
        utils.CreateJsonFile(TESTS_RESULTS_JSON_FILE_STEPS);
        InjectWebServiceVersionIntoYaml(CONFIGURATION_YAML);
    }

    /**
     *
     * @param ctx
     */
    @BeforeMethod
    public void setupTest(ITestContext ctx) {
        Reporter.log("Setting up test", true);
        /*Get the configuration from the yaml file*/
        Configuration config = getConfiguration(CONFIGURATION_YAML);
        System.setProperty("jsse.enableSNIExtension", "false");

        /*Set the endpoint*/
        httpRequest = null;
        RestAssured.baseURI = config.getEnv();
        httpRequest = RestAssured.given();

        /*authentication is established if auth value from configuration.yaml is true, this only will work for validation testing*/
        if(config.isAuth()){
            httpRequest.auth().preemptive().basic(config.getAuthUser(), config.getAuthPsw());
            HttpEndpointBodyRequestLog(config.getAuthResource(), null, null, AUTHORIZATION_HEADER);
            Response response = httpRequest.get(config.getAuthResource());
            HttpResponseLog(response);
            httpRequest = null;
            httpRequest = RestAssured.given();
            httpRequest.header(X_AUTH_TOKEN_HEADER, response.getHeader(X_AUTH_TOKEN_HEADER));
            ctx.getCurrentXmlTest().addParameter(HEADER_REQUEST_KEY, X_AUTH_TOKEN_HEADER+" : "+response.getHeader(X_AUTH_TOKEN_HEADER));
        }
    }

    /**
     *
     * @param result
     * @param method
     * @param ctx
     */
    @AfterMethod
    public void finishTest(ITestResult result, Method method, ITestContext ctx) {
        String newMessageFormatted;
        SysUtil utils = new SysUtil();
        if (ITestResult.FAILURE == result.getStatus()) {
            Throwable throwable = result.getThrowable();
            String newMessage = Utils.shortStackTrace(throwable,true);
            if(newMessage.startsWith("java.lang.AssertionError:")){
                newMessageFormatted = utils.throwableMessageFormatter(newMessage);
                try {
                    FieldUtils.writeField(throwable, "detailMessage", newMessageFormatted, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else {
                try {
                    if(newMessage.length()>=150)
                        newMessage = newMessage.substring(0,150);
                    FieldUtils.writeField(throwable, "detailMessage", newMessage, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        /*Add the result of the test to the Json File*/
        utils.EndSteps();
        utils.AddJsonObject(result , method, ctx);
        assert_messages.clear();
        m_custom = new CustomAssertion();
        utils.LogoutFinishedTest(httpRequest);
        Reporter.log("Finishing test hooks", true);
    }


    /**
     *
     * @throws IOException
     */
    @AfterSuite
    public void tearDown() throws IOException {
        SysUtil utils = new SysUtil();
        utils.CloseJsonFile(TESTS_RESULTS_JSON_FILE);
        utils.CloseJsonFile(TESTS_RESULTS_JSON_FILE_STEPS);
        Configuration config = getConfiguration(CONFIGURATION_YAML);
        /*Send the report summary through email if sendEmail value from configuration.yaml is true*/
        /*if(config.isSendEmail()){
            EmailReporting emailReporting = new EmailReporting();
            emailReporting.SendEmailReport(config);
        }*/
        if(config.getType().equals("functional")) {
            LocalReportingFunctional localFunctional = new LocalReportingFunctional();
            localFunctional.saveReport(config);
        }else {
            LocalReportingValidation localValidation = new LocalReportingValidation();
            localValidation.saveReport(config);
        }
        CleanWebServiceVersionIntoYaml(CONFIGURATION_YAML);
        Reporter.log("Finishing suite hooks", true);
    }

}
