package support;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.specification.RequestSpecification;
import org.json.simple.JSONObject;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import config.Configuration;

import static support.ConstantData.*;
import static support.Enumerations.Versions.NO_VERSION;
import testStep.JamaSteps;

public class SysUtil {

    /**
     * Get the configuration from yaml file and create the configuration class
     *
     * @param file
     * @return Configuration instance class
     */
    public static Configuration getConfiguration(String file) {
        Configuration config = null;
        Yaml yaml = new Yaml();
        try (InputStream in = Files.newInputStream(Paths.get(file))) {
            config = yaml.loadAs(in, Configuration.class);
            System.out.println(config.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return config;
    }

    /**
     * Define if the CSV files path will be related to AT-QA or QA env, if env in cnfiguration contains at-qa the path will be related to atQA folder
     *
     * @return
     */
    public static String GetCSVFilesPath() {
        Configuration config = getConfiguration(CONFIGURATION_YAML);
        String path = "";
        if (config.getEnv().matches(AT_QA_ENV_REGEX)) {
            path = CSV_FILES_ATQA_PATH;
        } else {
            path = CSV_FILES_DEV_PATH;
        }
        return path;
    }

    /**
     * Method to retrieve a List of dataProviders filtered by the desired versions that come from the configuration.yaml file
     * @param br                Buffered Reader initialized with a CSV file for processing it
     * @param dataProviderName  Data Provider to look by inside the CSV Files
     * @return
     */
    protected List<Object[]> GetRunnableTestData(BufferedReader br, String dataProviderName){
        List<Object[]> testData = new ArrayList<>();
        String line = "";
        try{
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] TestDataDetail = line.split(CSV_DELIMITER_CHARACTER);
                if (TestDataDetail.length > 0) {
                    if (TestDataDetail[0].equals(dataProviderName) && TestDataDetail[TestDataDetail.length-1].equals("true")) {
                        SysUtil sysUtils = new SysUtil();
                        String elements[] = getDesiredVersionsToBeRun();
                        for(String version : elements){
                            version = version.trim(); //For removing trailing and leading spaces if they exist
                            if (elements.length == 1 && version.equals(Enumerations.Versions.ALL.name()))
                                testData.add(CSVReaders.GetCSVInputData(TestDataDetail));
                            else {
                                if (version.equals(sysUtils.GetJamaVersion(TestDataDetail[1])) || version.equals(Enumerations.Versions.NO_VERSION.name()))
                                    testData.add(CSVReaders.GetCSVInputData(TestDataDetail));
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException ie) {
                System.out.println("Error occured while closing the BufferedReader");
                ie.printStackTrace();
            }
        }
        return testData;
    }

    /**
     * @returns an array of versions to be ran
     */
    public String[] getDesiredVersionsToBeRun() {
        Configuration config = getConfiguration(CONFIGURATION_YAML);
        String element = config.getVersions();
        return (!element.equals("")) ? element.split(",") : new String[]{ Enumerations.Versions.ALL.name() };
    }

    /**
     * use JAMA integration to find which version (1.0, 1.5, 1.6, etc) that a test case (in JAMA terms, an "item") belongs to.
     * @param testCaseName String name of the test case
     * @return
     */
    protected String GetJamaVersion(String testCaseName){
        final Pattern JAMA_ITEM_MATCHER = Pattern.compile(JAMA_ITEM_REGEX);
        JamaSteps jamaSteps = new JamaSteps();
        try {
            Matcher jamaMatcher = JAMA_ITEM_MATCHER.matcher(testCaseName);
            if (jamaMatcher.find())
                return jamaSteps.parseJamaVersion(jamaSteps.getJamaTags(jamaMatcher.group(0)));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return NO_VERSION.name(); //if version can't can be found, then default to NO_VERSION. That means it will be considered "unsorted".
    }

    /**
     * Pattern for general assertions
     * example.- Assertion: Expected Status line= HTTP/1.1 200; Found Status line= HTTP/1.1 200
     *
     * @param key
     * @param response
     * @param expected
     * @return meesage assertion
     */
    public static String GeneralAssertionMessage(String key, String response, String expected) {
        if (response == null)
            response = "null";
        return String.format(GENERAL_ASSERTION_MESSAGE, key, expected, key, response);
    }

    /**
     * <h1>String to array list!</h1>
     * Convert a string("string one, string two, ...") into ArrayList
     *
     * @param inputString String.- input data that will be converted into ArrayList
     * @return
     */
    public static ArrayList StringToArrayList(String inputString) {
        return new ArrayList<>(Arrays.asList(inputString.split(",")));
    }

    /**
     * <h1>T3 requiremet DataFields string builder!</h1>
     * Build a regex using the data fields
     *
     * @param inputString String.- input data that will be converted into regex
     * @return
     */
    public static String dataFieldsRegEx(String inputString) {
        ArrayList dataFields = StringToArrayList(inputString);
        StringBuilder regEx = new StringBuilder(".*");
        for (Object fieldData : dataFields) {
            regEx.append(fieldData.toString()).append(".*");
        }
        return regEx.toString();
    }

    /**
     * Retrieve the status line. Used only for logging tests
     * It uses a RegEx in order to retrieve the status line from the JSON string
     *
     * @param stepJSON String name of type dashboard_myInterpretationInbox_p0
     * @return String component name
     */
    public static String getStatusLineFromJSONString(String stepJSON){
        Pattern pattern = Pattern.compile("(HTTP\\\\/1.1 \\d{3})");
        Matcher matcher = pattern.matcher(stepJSON);
        if (matcher.find())
            matcher.group(1);
        return matcher.group(1);
    }

    /**
     * sort an ArrayList by order
     *
     * @param array
     * @param order
     * @return
     */
    public static ArrayList SortArray(ArrayList array, String order) {
        array.sort(order.equals(Enumerations.SortOrder.DESC.toString()) ? Comparator.reverseOrder() : Comparator.naturalOrder());
        return array;
    }

    /**
     * Sorts an array in Ascending order
     * @param arrayToSort Array to sort
     */
    public void sortAscending(String arrayToSort[]) {
        String contentToBeSorted[] = arrayToSort;
        if (arrayToSort == null || arrayToSort.length == 0) {
            return;
        }
        contentToBeSorted = arrayToSort;
        quickSort(0, arrayToSort.length - 1, contentToBeSorted);
    }

    /**
     * Sorts an array in Descending order
     * @param arrayToSort Array to sort
     */
    public void sortDescending(String arrayToSort[]) {
        sortAscending(arrayToSort);
        invertOrderToDescending(arrayToSort);
    }

    /**
     * Pattern for precondition not equals assertions
     * example.- Assertion: Expected Status line= HTTP/1.1 200; Found Status line= HTTP/1.1 200
     *
     * @param key
     * @param reponse
     * @param expected
     * @return meesage assertion
     */
    public static String PreconditionNotEqualsAssertionMessage(String key, String reponse, String expected) {
        return String.format(PRECONDITION_NOT_EQUALS_ASSERTION_MESSAGE, key, expected, key, reponse);
    }

    /**
     * JSON string is casted in hashMap
     *
     * @param responseBody
     * @return
     */
    public static Map<String, Object> GetHashMapResponseBody(String responseBody) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = new HashMap<>();
        try {
            map = mapper.readValue(responseBody, new TypeReference<Map<String, Object>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * Pattern for precondition assertions
     * example.- Assertion: Expected Status line= HTTP/1.1 200; Found Status line= HTTP/1.1 200
     *
     * @param key
     * @param reponse
     * @param expected
     * @return meesage assertion
     */
    public static String PreconditionAssertionMessage(String key, String reponse, String expected) {
        return String.format(PRECONDITION_ASSERTION_MESSAGE, key, expected, key, reponse);
    }

    /**
     * Create the JSON file in order to performed the email report
     *
     * @param jsonFile
     */
    public void CreateJsonFile(String jsonFile) {
        File file = new File(jsonFile); //filepath is being passes through //ioc         //and filename through a method
        try {
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(jsonFile, true);
            fileWriter.write("[");
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Inject the Web Servise version into YAML configuration file
     *
     * @param configFile
     * @throws IOException
     */
    public static void InjectWebServiceVersionIntoYaml(String configFile) throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        ObjectNode root = (ObjectNode) mapper.readTree(new File(configFile));
        root.put(WEB_SERVICE_YAML_KEY, GetWebServiceVersion(configFile));
        mapper.writer().writeValue(new File(configFile), root);
    }

    /**
     * Retrieve the Web Service Version - GET /version
     * In order to get the full version we have to concatenate the version and build_number
     *
     * @return
     */
    public static String GetWebServiceVersion(String configFile) {
        Configuration config = getConfiguration(configFile);
        RequestSpecification httpRequest = null;
        RestAssured.baseURI = config.getEnv();
        httpRequest = RestAssured.given();
        Response response = httpRequest.request(io.restassured.http.Method.GET, WEB_SERVICE_INFO);
        HashMap appHash = (HashMap<String, String>) GetHashMapResponseBody(response.getBody().asString()).get("app");
        return appHash.get("version").toString() + "-" + appHash.get("build_number").toString();
    }

    /**
     * Print the endpoint and the object body request in the Log execution and TestNG report
     *
     * @param endpoint
     * @param bodyRequest
     * @param formParams
     * @param stepName
     */
    public static void HttpEndpointBodyRequestLog(String endpoint, String bodyRequest, ArrayList<FormParameters> formParams, String stepName) {
        Configuration config = getConfiguration(CONFIGURATION_YAML);
        Reporter.log("----------", true);
        Reporter.log(STEPNAME_LOG + stepName, true);
        Reporter.log(ENDPOINT_LOG + config.getEnv() + endpoint, true);
        if (bodyRequest != null) {
            Reporter.log("", true);
            Reporter.log(REQUEST_BODY_LOG + bodyRequest, true);
        }
        if (formParams != null) {
            Reporter.log("form-data: ");
            for (FormParameters formParameters : formParams) {
                Reporter.log(formParameters.getKey() + ": " + formParameters.getValue());
            }
        }
    }

    /**
     * Print the raw response in the Log execution and TestNG report
     *
     * @param response
     */
    public static void HttpResponseLog(Response response) {
        String headers = "";
        for (Header header : response.getHeaders()) {
            headers += header + "</BR>";
            Reporter.log(header.toString(), true);
        }
        headers += response.getContentType() + "</BR>";
        ResponseBody body = response.getBody();
        Reporter.log("", true);
        Reporter.log(RESPONSE_BODY_LOG, true);
        if (headers.contains("application/octet-stream") || headers.contains(APPLICATION_PDF_HEADER)) {
            Reporter.log("A file was received in the response. A truncated version follows:", true);
            Reporter.log(response.getBody().asString().substring(0, 500), true);
        } else {
            body.prettyPrint();
        }
        Reporter.log("", true);
        Reporter.log(response.statusLine(), true);
    }

    /**
     * Return the exception formatted, ready to be used in the report
     *
     * @param newMessage
     * @return
     */
    public String throwableMessageFormatter(String newMessage) {
        String arrayAssertionsFailures[] = newMessage.split(",");
        String introductionMessage = arrayAssertionsFailures[0].split("\n\t")[0];
        String firstAssertion = arrayAssertionsFailures[0].split("\n\t")[1];
        firstAssertion = firstAssertion.replaceAll("&lt;/BR&gt;", "");
        StringBuilder newMessageFormatted = new StringBuilder(introductionMessage + "</BR></BR>" + firstAssertion + "</BR></BR>");
        for (int i = 0; i < arrayAssertionsFailures.length; i++) {
            String assertion = arrayAssertionsFailures[i];
            if (i != 0) {
                String formatAssertion = assertion.replaceAll("&lt;/BR&gt;", "");
                newMessageFormatted.append(formatAssertion).append("</BR></BR>");
            }
        }
        return newMessageFormatted.toString();
    }

    /**
     * Write flag in JSON for steps to identify steps in tests
     * Created in order to fit with logging tests. Overloaded method
     *
     */
    public void EndSteps(){
        EndSteps(null, null);
    }

    /**
     * Write flag in JSON for steps to identify steps in tests
     * Updated in order to be able to write the pivot JSON file that help with the logging tests
     *
     * @param jsonFile file where we put the bulk api calls performed in the massiveApiCalls BeforeClass hook in the LoggingTest.java class
     * @param testCase name of the test case that will be written
     */
    public void EndSteps(String jsonFile, String testCase) {
        JSONObject steps = new JSONObject();
        if (testCase != null)
            steps.put("TestCase", testCase);
        else
            steps.put("END", "END_STEPS");
        try {
            FileWriter fileWriter = new FileWriter(jsonFile != null ? jsonFile : TESTS_RESULTS_JSON_FILE_STEPS, true);
            fileWriter.write("\n" + steps.toJSONString());
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    /**
     * * Write the test case in the JSON file
     *
     * @param result
     */
    @SuppressWarnings("unchecked")
    public void AddJsonObject(ITestResult result, Method method, ITestContext ctx) {
        JSONObject jsonObject = new JSONObject();
        try {
            String msg = String.format("TestNG Debugger : %s() running with parameters %s.", result.getMethod()
                    .getConstructorOrMethod().getName(), Arrays.toString(result.getParameters()));
            System.out.println(msg);
            if (result.getParameters() != null && result.getParameters().length > 0 && !method.getAnnotation(Test.class).testName().equals("")) {
                jsonObject.put(TEST_CASE_JSONNODE, method.getAnnotation(Test.class).testName() + result.getParameters()[0].toString());
            } else if (result.getParameters() != null && result.getParameters().length > 0 && method.getAnnotation(Test.class).testName().equals("")) {
                jsonObject.put(TEST_CASE_JSONNODE, result.getParameters()[0].toString());
            } else {
                jsonObject.put(TEST_CASE_JSONNODE, method.getAnnotation(Test.class).testName());
            }
            jsonObject.put(RESULT_JSONNODE, result.isSuccess() ? PASS_RESULT : FAIL_RESULT);
            jsonObject.put(ERROR_JSONNODE, result.isSuccess() ? Expectations() : Expectations() + "<BR/><BR/>Throwable error message:<BR/> " + result.getThrowable().getMessage());
            jsonObject.put(DESCRIPTION_VALUE, ctx.getCurrentXmlTest().getParameter(DESCRIPTION_VALUE));

            FileWriter fileWriter = new FileWriter(TESTS_RESULTS_JSON_FILE, true);
            fileWriter.write("\n" + jsonObject.toJSONString());
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    /**
     * <h1>Closed session after each test!</h1>
     * Performed closing session after executing each test cases in order to avoid running out the available sessions per user
     *
     * @param httpRequest Http client that have all the endpoint configuration
     */
    public void LogoutFinishedTest(RequestSpecification httpRequest) {
        Reporter.log("", true);
        Reporter.log("*************** Closing session after finished test case ***************", true);
        Response response = httpRequest.request(io.restassured.http.Method.POST, LOGOUT_RESOURCE);
        HttpResponseLog(response);
        Reporter.log("*************** Closed session after finished test case ***************", true);
        Reporter.log("", true);
    }

    /**
     * Close the JSON file at the end of the regresion
     *
     * @param jsonFile
     */
    public void CloseJsonFile(String jsonFile) {
        File file = new File(jsonFile);
        try {
            if (file.exists()) {
                file.createNewFile();
                FileWriter fileWriter = new FileWriter(jsonFile, true);
                fileWriter.write("\n]");
                fileWriter.flush();
                fileWriter.close();
            } else {
                Reporter.log("The " + jsonFile + " file does not exist", true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Clean the Web Servise version value into YAML configuration file in order send wrong data
     *
     * @param configFile
     * @throws IOException
     */
    public static void CleanWebServiceVersionIntoYaml(String configFile) throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        ObjectNode root = (ObjectNode) mapper.readTree(new File(configFile));
        root.put(WEB_SERVICE_YAML_KEY, "");
        mapper.writer().writeValue(new File(configFile), root);
    }

}
