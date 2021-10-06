package support;

import java.util.regex.Pattern;

public class ConstantData {

    /**
     * Devices
     */
    public static final String DEVICES_ALLEVENTS_RESOURCE = "/devices/all-events";
    public static final String DEVICES_CHANGE_INTERPRETER_RESOURCE = "/devices/change-interpreter";
    public static final String DEVICES_REPORTS_RESOURCE = "/devices/reports";
    public static final String DEVICES_SEARCH_RESOURCE = "/devices/search";
    public static final String DEVICES_SYMPTOMS_RESOURCE = "/devices/symptoms";
    public static final String DEVICES_UNREGISTERED_SEARCH_RESOURCE = "/devices/unregistered/search";
    public static final String[] DEVICES_FIELDS_EXISTENCE_ARRAY = new String[]{"patient.Name", "prescriptionId", "serialNumber","monitorStartDate","patient.id","prescriberLocation","monitorStatus","trackingId","expectedDeliveryDate","actualDeliveryDate","trackingLastUpdated","wearDuration","myZioActivationDate","receivedDate"};

    /**
     * Base Default Values
     */
    public static final String DEFAULT_HTML_REPORT_FILENAME = "local_report_functional.html";
    public static final String DEFAULT_HTML_FILE_EXTENSION = ".html";

    /**
     * Resources path
     */
    public static final String CONFIGURATION_YAML = "src/main/java/Config/configuration.yaml";
    public static final String BILLING_CSV_FILTER_FILE = "src/main/java/Config/BillingCSVFilter.csv";

    /**
     * Regular expression
     */
    public static final String REPORT_TYPE_ROUTINE_MDN_DESCRIPTION = "Zio AT/XT Final Report: Routine - Amended or Zio AT/XT Final Report: MD Notified - Amended";
    public static final String REPORT_TYPE_DAILY_DESCRIPTION = "Zio AT Daily Report: Routine";
    public static final String VALID_EMAIL_DESCRIPTION = "user@domain.com";
    public static final String VALID_TOKEN_REGEX = "[^-]{8}-[^-]{4}-[^-]{4}-[^-]{4}-[^-]{12}";
    public static final String REPORTS_FILENAME_REGEX = ".*%s.*";
    public static final String DATE_TIME_VALIDATION_REGULAR_EXPRESSION = "^(\\d{4}((-)?(0[1-9]|1[0-2])((-)?(0[1-9]|[1-2][0-9]|3[0-1])(T(24:00(:00(\\.[0]+)?)?|(([0-1][0-9]|2[0-3])(:)[0-5][0-9])((:)[0-5][0-9](\\.[\\d]+)?)?)((\\+|-)(14:00|(0[0-9]|1[0-3])(:)[0-5][0-9])|Z))?)?)?)$";
    public static final String EMAIL_VALIDATE_REGULAR_EXPRESSION = "^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
    public static final String REPORTS_TYPE_MDN_REGULAR_EXPRESSION = "^(FINAL|TRANSMISSION|DDR|BASELINE|DAILY)$";
    public static final String DEVICES_ACTIVE_REGULAR_EXPRESSION = "^(REGISTERED|RECEIVED)$";
    public static final String REPORTS_TYPE_OPTIONS_REGULAR_EXPRESSION = "^(DAILY|TRANSMISSION|DDR|FINAL)$";
    public static final String REPORTS_TYPE_OPTIONS_FOR_MDN_REGULAR_EXPRESSION = "^(TRANSMISSION|FINAL)$";
    public static final String EVENT_TYPES_REGULAR_EXPRESSION = "^(Final Report|Patient Symptom)$";
    public static final String TIME_REGULAR_EXPRESSION = "(Mon|Tue|Wed|Thu|Fri|Sat|Sun), (0[1-9]|[12]\\d|3[01]) (Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec) \\d{4} (?:[01]\\d|2[0123]):(?:[012345]\\d):(?:[012345]\\d)";
    public static final String EVENT_TYPES = "Final Report,Patient Symptom";
    public static final String REPORT_TYPE_ROUTINE_MDN_REGULAR_EXPRESSION = "^(Zio AT Final Report: Routine - Amended|Zio AT Final Report: Routine - Amended Interpretation|Zio AT Final Report: MD Notified - Amended|Zio AT Final Report: Routine|Zio AT Final Report: MD Notified|Zio AT Final Report: Routine - Amended (Replaced)|Zio AT Final Report: MD Notified - Amended (Replaced))$";
    public static final String REPORT_TYPE_FINAL = "FINAL";
    public static final String REPORT_TYPE_BASELINE = "Baseline";
    public static final String REPORT_TYPE_TRANSMISSION = "TRANSMISSION";
    public static final String REPORT_TYPE_MDN = "true";
    public static final String REPORT_TYPE_DAILY = "DAILY";
    public static final String REPORT_TYPE_DDR = "DDR";
    public static final String COMBINED_REPORTS_NAME = "Combined_Reports";
    public static final String ORDER_STATUS_IN_PROCESS = "IN_PROCESS";
    public static final String STATUSES_PATIENTS_REGISTRATION_REGEX = "REGISTERED|ACTIVATED|PENDING_RETURN|SHIPPED_TO_IRHYTHM|IN_TRANSIT_TO_IRHYTHM|DELIVERED_TO_IRHYTHM|PROCESSING_DATA|PENDING_INTERPRETATION||FINAL_REPORT_POSTED|PENDING_SHIPMENT|SHIPPED_TO_PATIENT|IN_TRANSIT_TO_PATIENT|DELIVERED_TO_PATIENT|CANCELLED|NO_DATABA_AVAILABLE|LOST|EXPIRED|IN_TRANSIT_DELAYED_TO_IRYTHM|IN_TRANSIT_DELAYED_TO_PATIENT";
    public static final String VALID_SERIAL_NUMBER = "^([a-zA-Z]{6}[0-9]{4})$";
    public static final String AT_SERIAL_NUMBER_PREFIX = "ATESTJ";
    public static final String AT_QA_ENV_REGEX = "^.*at-qa.*$";
    public static final String ISO_DATE_FORMAT_REGEX = "^([\\+-]?\\d{4}(?!\\d{2}\\b))((-?)((0[1-9]|1[0-2])(\\3([12]\\d|0[1-9]|3[01]))?|W([0-4]\\d|5[0-2])(-?[1-7])?|(00[1-9]|0[1-9]\\d|[12]\\d{2}|3([0-5]\\d|6[1-6])))([T\\s]((([01]\\d|2[0-3])((:?)[0-5]\\d)?|24\\:?00)([\\.,]\\d+(?!:))?)?(\\17[0-5]\\d([\\.,]\\d+)?)?([zZ]|([\\+-])([01]\\d|2[0-3]):?([0-5]\\d)?)?)?)?$";
    public static final String UNKNOWN_DEVICE_STATUS = "UNKNOWN";
    public static final String TIMEFORMAT_HOURSANDMINS = "HH:mm";

    /**
     * Csv files path
     */
    public static String CSV_FILES_DEV_PATH = "src/main/java/resources/testData/";
    public static String CSV_FILES_ATQA_PATH = "src/main/java/resources/testData/";
    public static String CSV_DELIMITER_CHARACTER = ";";
    public static String STRING_WILDCARD_CHARACTER = "%s";
    public static String NUMBER_WILDCARD_CHARACTER = "%d";
    public static String STRING_COMMA_SEPARATOR_CHARACTER = ",";
    public static String STRING_PIPE_SEPARATOR_CHARACTER = Pattern.quote("|");
    public static String STRING_QUESTIONMARK_SEPARATOR_CHARACTER = "?";
    public static String STRING_SINGLESPACE_SEPARATOR_CHARACTER = " ";
    public static String STRING_HYPHEN_SEPARATOR_CHARACTER = "-";
    public static String STRING_HASH_SEPARATOR_CHARACTER = "#";

    /** JAMA resources
     *
     */
    public static final String JAMA_USERNAME = "qa_test";
    public static final String JAMA_PASSWORD = "ChangeMe1";
    public static final String JAMA_ITEMS_ID_TAGS_RESOURCE = "/items/%s/tags"; //see https://jama.irhythmtech.org/api-docs/#/ for details
    public static final String JAMA_ABSTRACTITEMS_RESOURCE = "/abstractitems?project=73&itemType=61&contains=%s"; //see https://jama.irhythmtech.org/api-docs/#/ for details
    public static final String JAMA_VERSION_REGEX = "V\\d_\\d"; //V1_0, for instance
    public static final String JAMA_ITEM_REGEX = "[\\d]{5,7}"; //five or six digits in a row (happens to be located within a URL)
    public static final String JAMA_REMOVE_HYPERLINK_REGEX = "<(.|\\n)+?>";
    public static final String JAMA_RESPONSE_BODY_STRING = "RESPONSE BODY: ";
    public static final String GET_TC_NAME_REGEX = ".*>(.*)<.*";

    public static final String COLUMN_PARAMETER_DESCRIPTION = "Column";
    public static final String TABLE_PARAMETER_DESCRIPTION = "Table";
    public static final String SEARCH_PARAMETER_DESCRIPTION = "Search Parameter";
    public static final String DATABASE_NAME_DESCRIPTION = "DataBase Name";
    public static final String QUERY_DESCRIPTION = "Query";

    /**
     * csv files names
     */
    public static final String DATA_ACCOUNTS_CSV_FILE = "dataAccounts.csv";

    /**
     * Integrations data
     *
     * Important: Splunk host, database host and inventory host are now set in the config files!
     */

    static final String SPLUNK_USERNAME = "qa_test";
    static final String SPLUNK_PSW = "sp1unkSQA";
    static final int  SPLUNK_PORT = 8089;
    public static final String DATA_SPLUNK_CSV_FILE = "dataSplunkTest.csv";
    static final String EARLIEST_TIME_KEY = "earliest_time";
    static final String LATEST_TIME_KEY = "latest_time,";
    static final String SEARCH_KEY = "search";
    static final String TIMESTAMP_TIMEZONE_REGEX_USERAGENT_REGEX = "\\[\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}(,|.)\\d{3} UTC\\].*(origin=\"N/A\"|REMOTE HOST 127.0.0.1|remote_host=\"127.0.0.1\" ).*";
    public static final String DATA_LOGGING_CSV_FILE = "dataLogging.csv";
    public static final String LOGGING_STEPNAME = "executing logging step";

    /**
     * Authorization and x-authtoken strings to evaluate if we are going to take the values from the hooks or replace them from dataproviders values
     */
    public static final String AUTHORIZATION_VALID_CONDITION = "ValidAuthorization";
    public static final String X_AUTH_TOKEN_VALID_CONDITION = "ValidToken";
    public static final String INVALID_X_AUTH_TOKEN = "InvalidToken";

    //region Assertions messages
    public static final String GENERAL_ASSERTION_MESSAGE = "Expected %s= %s; </BR> Found %s= %s";
    public static final String PRECONDITION_ASSERTION_MESSAGE = "Precondition: Expected %s=%s; </BR> Found %s= %s";
    public static final String PRECONDITION_NOT_EQUALS_ASSERTION_MESSAGE = "Precondition: Expected other than %s=%s; </BR> Found %s= %s";

    /**
     * JSON file name that contains the tests results
     */
    public static String TESTS_RESULTS_JSON_FILE= "src/resources/tests_results.json";

    /**
     * Elements that build the json_steps file, this file contains the tests steps for the report
     */
    public static String TESTS_RESULTS_JSON_FILE_STEPS= "src/resources/json_steps.json";
    public static String LOGGING_TEST_RESULTS_JSON_FILE_STEPS= "src/resources/logging_json_steps.json";
    public static String STEP_NAME_KEY = "step";
    public static String STEP_SPLUNK_VALUE = "Splunk Service";
    public static String HEADER_REQUEST_KEY = "headersRequest";
    public static String BODY_REQUEST_KEY = "bodyRequest";
    public static String HEADER_RESPONSE_KEY = "headersResponse";
    public static String BODY_RESPONSE_KEY = "bodyResponse";

    /**
     * Endpoint throws info related to name, BuildNumber and version release
     */
    public static final String WEB_SERVICE_INFO = "/info";

    /**
     * Web Service Key value from yaml file
     */
    public static final String WEB_SERVICE_YAML_KEY = "webServiceVersion";

    /**
     * Log comments
     */
    public static final String REQUEST_BODY_LOG = "Request Body: ";
    public static final String RESPONSE_BODY_LOG = "RAW: ";
    public static final String ENDPOINT_LOG = "Endpoint: ";
    public static final String STEPNAME_LOG = "Step: ";

    /**
     * API Rest Headers
     */
    public static final String CONTENT_TYPE_HEADER = "Content-Type";
    public static final String APPLICATION_JSON_HEADER = "application/json";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String X_AUTH_TOKEN_HEADER = "x-auth-token";
    public static final String MULTIPART_HEADER = "multipart/form-data";
    public static final String APPLICATION_PDF_HEADER = "application/pdf";
    public static final String CONTENT_DISPOSITION_HEADER = "Content-Disposition=inline; filename=";
    public static final String CONTENT_DISPOSITION_TYPE_HEADER= "Content-Disposition";

    /**
     * True and false strings
     */
    public static final String TRUE = "true";
    public static final String FALSE = "false";

    /**
     * JSON nodes names and values for JSon results file
     */
    public static final String TEST_CASE_JSONNODE = "Test Case";
    public static final String RESULT_JSONNODE = "Result";
    public static final String ERROR_JSONNODE = "Error";
    public static final String FAIL_RESULT = "Fail";
    public static final String PASS_RESULT = "Pass";
}
