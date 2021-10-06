package support;
import io.restassured.response.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.asserts.IAssert;
import org.testng.asserts.SoftAssert;
import org.testng.collections.Lists;

import java.util.*;

import static support.ConstantData.TIMESTAMP_TIMEZONE_REGEX_USERAGENT_REGEX;
import static support.CustonAssertionHelpers.*;
import static support.SysUtil.*;

public class CustomAssertion extends SoftAssert {

    public static List<String> assert_messages = Lists.newArrayList();

    @Override
    public void onBeforeAssert(IAssert a) {
    }

    @Override
    public void onAfterAssert(IAssert a) {
        if (a.getMessage().startsWith("Precondition:"))
            assert_messages.add(a.getMessage());
        else
            assert_messages.add("Assertion: " + a.getMessage());
    }

    @Override
    public void onAssertSuccess(IAssert<?> assertCommand) {
    }

    @Override
    public void onAssertFailure(IAssert<?> assertCommand, AssertionError ex) {
    }

    public static List<String> getAssertMessages() {
        return assert_messages;
    }

    /**
     * Override of compactAssertion, this one has no defaultSearchTerm
     *
     * @param message
     * @param response
     * @param responseSearchTerm
     * @param expectedResponse
     * @param expectedSearchTerm
     * @throws ParseException
     */
    public void compactAssertion(String message, Response response, String responseSearchTerm, String expectedResponse, String expectedSearchTerm) throws ParseException {
        compactAssertion(message, response, responseSearchTerm, expectedResponse, expectedSearchTerm, null);
    }

    /**
     * @param message
     * @param response
     * @param responseSearchTerm
     * @param expectedResponse
     * @param expectedSearchTerm
     * @param defaultExpectedSearchTerm
     * @throws ParseException
     */
    public void compactAssertion(String message, Response response, String responseSearchTerm, String expectedResponse, String expectedSearchTerm, String defaultExpectedSearchTerm) throws ParseException {
        compactAssertion(message, response, responseSearchTerm, expectedResponse, expectedSearchTerm, defaultExpectedSearchTerm, false);
    }

    /**
     * @param message
     * @param response
     * @param responseSearchTerm
     * @param expectedResponse
     * @param expectedSearchTerm
     * @param isPrecondition
     * @throws ParseException
     */
    public void compactAssertion(String message, Response response, String responseSearchTerm, String expectedResponse, String expectedSearchTerm, boolean isPrecondition) throws ParseException {
        compactAssertion(message, response, responseSearchTerm, expectedResponse, expectedSearchTerm, null, isPrecondition);
    }

    /**
     * This will parse through a pair of JSON bodies (one actual JSON, one expected JSON), search them for *one* specified key or series of keys, and compare the values that are found
     * See ReportsInterpretersSdzmdsK9AP for an example
     * This may not work for all assertions, but should handle most of the simpler ones.
     * For searching through a "data.results" JSONArray or similar, try the method dataResultsAssertion
     *
     * @param message
     * @param response
     * @param responseSearchTerm
     * @param expectedResponse
     * @param expectedSearchTerm
     * @param defaultExpectedSearchTerm
     * @param isPrecondition
     * @throws ParseException
     */
    public void compactAssertion(String message, Response response, String responseSearchTerm, String expectedResponse, String expectedSearchTerm, String defaultExpectedSearchTerm, boolean isPrecondition) throws ParseException {
        JSONParser parser = new JSONParser();

        String[] responseTerms = responseSearchTerm.split("\\.");
        String[] expectedTerms = expectedSearchTerm.split("\\.");
        JSONObject actualObject;
        JSONObject expectedObject;
        String actualRaw = response.getBody().asString();

        try {
            actualObject = (JSONObject) parser.parse(actualRaw); //parse response body
        } catch (ClassCastException ex) { //if it starts out as an array, such as Inventory WS responses
            actualObject = (JSONObject) parser.parse(actualRaw.substring(1, actualRaw.length() - 1));
        }
        try {
            expectedObject = (JSONObject) parser.parse(expectedResponse); //parse response body
        } catch (ClassCastException ex) { //if it starts out as an array, such as Inventory WS responses
            expectedObject = (JSONObject) parser.parse(expectedResponse.substring(1, expectedResponse.length() - 1));
        }

        String actual = searchJSONObject(actualObject, responseTerms);
        String expected = searchJSONObject(expectedObject, expectedTerms);

        if (expected == null) {
            expected = defaultExpectedSearchTerm; //whatever key is expected if not specified in the expected JSON
        }
        if (isPrecondition) {
            assertEquals(actual, expected, PreconditionAssertionMessage(message, actual, expected));
        } else {
            assertEquals(actual, expected, GeneralAssertionMessage(message, actual, expected));
        }
    }

    /**
     * Same as compactAssertion, but for when the object in question is an array
     *
     * @param message
     * @param response
     * @param responseSearchTerm
     * @param expectedResponse
     * @param expectedSearchTerm
     * @throws ParseException
     */
    public void compactArrayAssertion(String message, Response response, String responseSearchTerm, String expectedResponse, String expectedSearchTerm) throws ParseException {
        JSONParser parser = new JSONParser();
        String[] responseTerms = responseSearchTerm.split("\\.");
        String[] expectedTerms = expectedSearchTerm.split("\\.");
        JSONObject actualObject = (JSONObject) parser.parse(response.getBody().asString()); //parse response body
        JSONObject expectedObject = (JSONObject) parser.parse(expectedResponse); //parse expected body
        JSONArray actual = getJSONArray(actualObject, responseTerms);
        JSONArray expected = getJSONArray(expectedObject, expectedTerms);
        assertEquals(actual, expected, GeneralAssertionMessage(message, actual.toJSONString(), expected.toJSONString()));
    }

    /**
     * Override of compactAssertion, this one has no defaultSearchTerm
     *
     * @param message
     * @param response
     * @param responseSearchTerm
     * @param expectedResponse
     * @param expectedSearchTerm
     * @throws ParseException
     */
    public void dataResultsAssertion(String message, Response response, String responseSearchTerm, String expectedResponse, String expectedSearchTerm) throws ParseException {
        dataResultsAssertion(message, response, responseSearchTerm, expectedResponse, expectedSearchTerm, null);
    }

    /**
     * Similar to Compact Assertion, but for searching through a JSONArray with multiple entries nested within the JSON you are searching. Primarily for searching through data.results
     * Make sure you use %s in the message!
     *
     * @param message
     * @param response
     * @param responseSearchTerm
     * @param expectedResponse
     * @param expectedSearchTerm
     * @param defaultExpectedSearchTerm
     * @throws ParseException
     */
    public void dataResultsAssertion(String message, Response response, String responseSearchTerm, String expectedResponse, String expectedSearchTerm, String defaultExpectedSearchTerm) throws ParseException {
        JSONParser parser = new JSONParser();
        String[] responseTerms = responseSearchTerm.split("\\.");
        String[] expectedTerms = expectedSearchTerm.split("\\.");
        JSONObject actualObject = (JSONObject) parser.parse(response.getBody().asString()); //parse response body
        JSONObject expectedObject = (JSONObject) parser.parse(expectedResponse); //parse expected body
        JSONArray actualArray = getJSONArray(actualObject, responseTerms);
        JSONArray expectedArray = getJSONArray(expectedObject, expectedTerms);
        for (int i = 0; i < expectedArray.size(); i++) {
            String actual = searchJSONObject((JSONObject) actualArray.get(i), Arrays.copyOfRange(responseTerms, 2, responseTerms.length));
            String expected = searchJSONObject((JSONObject) expectedArray.get(i), Arrays.copyOfRange(expectedTerms, 2, expectedTerms.length));
            assertEquals(actual, expected, GeneralAssertionMessage(String.format(message, "[" + i + "]"), actual, expected));
        }
    }

    /**
     * Same as dataResultsAssertion, but searches for a specific word contained in the String to be searched
     *
     * @param message
     * @param response
     * @param responseSearchTerm
     * @param expectedTerm
     * @throws ParseException
     */
    public void dataIncludesAssertion(String message, Response response, String responseSearchTerm, String expectedTerm) throws ParseException {
        JSONParser parser = new JSONParser();
        String[] responseTerms = responseSearchTerm.split("\\.");
        JSONObject actualObject = (JSONObject) parser.parse(response.getBody().asString()); //parse response body
        JSONArray actualArray = getJSONArray(actualObject, responseTerms);
        for (int i = 0; i < actualArray.size(); i++) {
            String actual = searchJSONObject((JSONObject) actualArray.get(i), Arrays.copyOfRange(responseTerms, 2, responseTerms.length)); //search by last term?
            assertTrue(actual.contains(expectedTerm), GeneralAssertionMessage(String.format(message + " contains " + expectedTerm, "[" + i + "]"), String.valueOf(actual.contains(expectedTerm)), "true"));
        }
    }

    /**
     * Searches for a specific field contained in the JsonResponse to be searched
     *
     * @param response           response obteined from the HTTP request
     * @param responseSearchTerm search path and field to search "path.path.field"
     */
    public void ContainsKeyAssertion(Response response, String responseSearchTerm) {
        String[] responseTerms = responseSearchTerm.split("\\.");
        ArrayList SearchList = new ArrayList();
        ArrayList SearchValue = new ArrayList();
        for (String value : responseTerms) {
            SearchList.add(value);
            SearchValue.add(value);
        }
        SearchList.remove(SearchList.size() - 1);
        Map<String, Object> standarRestResultResponse = GetHashMapResponseBody(response.getBody().asString());
        for (Object value : SearchList) {
            HashMap dataReports = (HashMap<String, String>) standarRestResultResponse.get(value.toString());
            standarRestResultResponse = dataReports;
        }
        assertTrue(standarRestResultResponse.containsKey(SearchValue.get(SearchValue.size() - 1)), GeneralAssertionMessage("contains " + SearchValue.get(SearchValue.size() - 1), String.valueOf(standarRestResultResponse.containsKey(SearchValue.get(SearchValue.size() - 1))), String.valueOf(true)));
    }

    /**
     * Same as dataResultsAssertion, but makes sure the searched fields are in the correct order
     *
     * @param message
     * @param response
     * @param responseSearchTerm
     * @param ascending          TRUE if ascending, FALSE if descending
     * @throws ParseException
     */
    public void dataSortAssertion(String message, Response response, String responseSearchTerm, boolean ascending) throws ParseException {
        JSONParser parser = new JSONParser();
        String[] responseTerms = responseSearchTerm.split("\\.");
        JSONObject actualObject = (JSONObject) parser.parse(response.getBody().asString()); //parse response body
        ArrayList unsortedArray = new ArrayList();
        ArrayList sortedArray = new ArrayList();
        JSONArray actualArray = getJSONArray(actualObject, responseTerms);
        if (actualArray.size() < 1) {
            fail("Array of results has less than two elements! Cannot assert sort order!"); // expecting more than one element to sort
        }
        for (int i = 0; i < actualArray.size(); i++) {
            unsortedArray.add(searchJSONObject((JSONObject) actualArray.get(i), responseTerms[responseTerms.length - 1]));
        }
        for (Object Value : unsortedArray) {
            sortedArray.add(Value);
        }
        if (ascending) {
            sortedArray = SysUtil.SortArray(sortedArray, "ASC");
        } else {
            sortedArray = SysUtil.SortArray(sortedArray, "DESC");
        }
        for (int i = 0; i < sortedArray.size(); i++) {
            assertEquals(unsortedArray.get(i), sortedArray.get(i), GeneralAssertionMessage(String.format(message, "[" + i + "]"), unsortedArray.get(i).toString(), sortedArray.get(i).toString()));
        }
    }

    /**
     * Same as dataSortAssertion, but makes sure the searched fields are NOT in the correct order
     *
     * @param message             String assertion message
     * @param response            Response object with the results
     * @param responseSearchTerm  String response field to be sorted
     * @param ascending           Boolean TRUE if ascending, FALSE if descending
     * @throws ParseException
     */
    public void dataNotSortedAssertion(String message, Response response, String responseSearchTerm, boolean ascending) throws ParseException {
        JSONParser parser = new JSONParser();
        String[] responseTerms = responseSearchTerm.split("\\.");
        JSONObject actualObject = (JSONObject) parser.parse(response.getBody().asString()); //parse response body
        ArrayList unsortedArray = new ArrayList();
        ArrayList sortedArray = new ArrayList();
        JSONArray actualArray = getJSONArray(actualObject, responseTerms);
        if (actualArray.size() < 1) {
            fail("Array of results has less than two elements! Cannot assert sort order!"); // expecting more than one element to sort
        }
        for (int i = 0; i < actualArray.size(); i++) {
            unsortedArray.add(searchJSONObject((JSONObject) actualArray.get(i), responseTerms[responseTerms.length - 1]));
        }
        for (Object Value : unsortedArray) {
            sortedArray.add(Value);
        }
        if (ascending) {
            sortedArray = SysUtil.SortArray(sortedArray, "ASC");
        } else {
            sortedArray = SysUtil.SortArray(sortedArray, "DESC");
        }
        for (int i = 0; i < sortedArray.size(); i++) {
            assertNotEquals(unsortedArray.get(i), sortedArray.get(i), PreconditionNotEqualsAssertionMessage(String.format(message, "[" + i + "]"), unsortedArray.get(i).toString(), sortedArray.get(i).toString()));
        }
    }

    /**
     * Method to warranty a sorting assertion using the Quicksort algorithm managing string only values
     *
     * @param message
     * @param response           Response object with the positive results
     * @param responseSearchTerm Full key path to look by (i.e. data.results.startDate) and create with it a new array to sort
     * @param ascending          Order to sort by (true if Ascending, false if Descending
     * @throws ParseException
     */
    public void dataQuickSortAssertion(String message, Response response, String responseSearchTerm, boolean ascending) throws ParseException {
        JSONParser parser = new JSONParser();
        String[] responseTerms = responseSearchTerm.split("\\.");
        String str = response.getBody().asString();
        JSONObject actualObject = (JSONObject) parser.parse(response.getBody().asString());
        ArrayList unsortedArray = new ArrayList();
        JSONArray actualArray = getJSONArray(actualObject, responseTerms);
        if (actualArray.size() < 1)
            fail("Array of results has less than two elements! Cannot assert sort order!"); // expecting more than one element to sort

        for (int i = 0; i < actualArray.size(); i++)
            unsortedArray.add(searchJSONObject((JSONObject) actualArray.get(i), responseTerms[responseTerms.length - 1]));

        String sortedElements[] = new String[unsortedArray.size()];
        for (int j = 0; j < unsortedArray.size(); j++)
            sortedElements[j] = (unsortedArray.get(j) != null) ? unsortedArray.get(j).toString() : "null";

        SysUtil utils = new SysUtil();
        if (ascending)
            utils.sortAscending(sortedElements);
        else
            utils.sortDescending(sortedElements);

        for (int i = 0; i < unsortedArray.size(); i++) {
            if (unsortedArray.get(i) != null)
                assertEquals(unsortedArray.get(i), sortedElements[i], GeneralAssertionMessage(String.format(message, "[" + i + "]"), unsortedArray.get(i).toString(), sortedElements[i]));
            else
                i++;
        }
    }


    /**
     * Same as dataResultsAssertion, but makes sure the searched fields are in the correct order
     *
     * @param message
     * @param response
     * @param responseSearchTerm
     * @param ascending          TRUE if ascending, FALSE if descending
     * @throws ParseException TODO- the sort function order the elements using ASCII values First: "!"#$%&/()=...", SECOND: "ABCDEFG..." (capitalized), THIRD: "abcdefg... (lowercase)
     *                        TODO- the sorting is not alphabetically verify a sorting function to sorting NOT by Ascci value.
     */
    public void dataSortAssertionToLowercase(String message, Response response, String responseSearchTerm, boolean ascending) throws ParseException {
        JSONParser parser = new JSONParser();
        String[] responseTerms = responseSearchTerm.split("\\.");
        JSONObject actualObject = (JSONObject) parser.parse(response.getBody().asString()); //parse response body
        ArrayList unsortedArray = new ArrayList();
        ArrayList sortedArray = new ArrayList();
        JSONArray actualArray = getJSONArray(actualObject, responseTerms);
        if (actualArray.size() < 1) {
            fail("Array of results has less than two elements! Cannot assert sort order!"); // expecting more than one element to sort
        }
        for (int i = 0; i < actualArray.size(); i++) {
            unsortedArray.add(searchJSONObject((JSONObject) actualArray.get(i), responseTerms[responseTerms.length - 1]).toLowerCase());
        }
        for (Object Value : unsortedArray) {
            sortedArray.add(Value.toString().toLowerCase());
        }
        if (ascending) {
            sortedArray = SysUtil.SortArray(sortedArray, "ASC");
        } else {
            sortedArray = SysUtil.SortArray(sortedArray, "DESC");
        }
        for (int i = 0; i < sortedArray.size(); i++) {
            assertEquals(unsortedArray.get(i), sortedArray.get(i), GeneralAssertionMessage(String.format(message, "[" + i + "]"), unsortedArray.get(i).toString(), sortedArray.get(i).toString()));
        }
    }

    /**
     * Assert that a field simply exists in the response JSON
     *
     * @param message
     * @param response
     * @param responseSearchTerm
     */
    public void existenceAssertion(String message, Response response, String responseSearchTerm) throws ParseException {
        JSONParser parser = new JSONParser();
        String[] responseTerms = responseSearchTerm.split("\\.");
        JSONObject actualObject = (JSONObject) parser.parse(response.getBody().asString()); //parse response body
        String actual = searchJSONObject(actualObject, responseTerms);
        assertNotNull(actual, GeneralAssertionMessage(message + " exists", String.valueOf(actual != null), "true"));
    }

    /**
     * Assert the Status line from the response
     *
     * @param response
     * @param statusLine
     */
    public void StatusLineAssertion(Response response, String statusLine) {
        assertEquals(response.getStatusLine().trim(), statusLine, GeneralAssertionMessage("Status line", response.getStatusLine().trim(), statusLine));
    }

    /**
     * Assert the Status line from the JSONobject
     * Function works only with the logging test cases. We retrieve the whole step performed in the bulk api calls
     *
     * @param stepJSON retrieve in the JSON pivot file that contains the actual status line
     * @param statusLine expected status line
     */
    public void StatusLineAssertion(JSONObject stepJSON, String statusLine) {
        String responseStatusLine = getStatusLineFromJSONString(stepJSON.toJSONString());
        responseStatusLine = responseStatusLine.replaceAll("\\\\", "");
        assertEquals(responseStatusLine.trim(), statusLine, GeneralAssertionMessage("Status line", responseStatusLine.trim(), statusLine));
    }

    /**
     * Assert the array size
     *
     * @param message
     * @param response
     * @param responseSearchTerm
     * @param expectedResponse
     * @param expectedSearchTerm
     * @throws ParseException
     */
    public void ArraySizeAssertion(String message, Response response, String responseSearchTerm, String expectedResponse, String expectedSearchTerm) throws ParseException {
        JSONParser parser = new JSONParser();
        String[] responseTerms = responseSearchTerm.split("\\.");
        String[] expectedTerms = expectedSearchTerm.split("\\.");
        JSONObject actualObject = (JSONObject) parser.parse(response.getBody().asString()); //parse response body
        JSONObject expectedObject = (JSONObject) parser.parse(expectedResponse); //parse expected body
        JSONArray actualArray = getJSONArray(actualObject, responseTerms);
        JSONArray expectedArray = getJSONArray(expectedObject, expectedTerms);
        assertEquals(actualArray.size(), expectedArray.size(), GeneralAssertionMessage(message, String.valueOf(actualArray.size()), String.valueOf(expectedArray.size())));
    }

    /**
     * <h1>Assert the Splunk events! - T1 requirement</h1>
     * This function assert the Splunk events according to a list of expected events
     *
     * @param splunkEvents   These events come from a request to Splunk server
     * @param expectedEvents These elements come from data provider
     */
    public void SplunkRequestEventsAssertion(ArrayList<String> splunkEvents, ArrayList expectedEvents) {
        for (String event : splunkEvents) {
            for (Object expectedEvent : expectedEvents) {
                if (event.matches("(.*)" + expectedEvent.toString() + "(.*)")) {
                    assertTrue(event.matches("(.*)" + expectedEvent.toString() + "(.*)"), GeneralAssertionMessage("log row[" + (splunkEvents.indexOf(event) + 1) + "] contains " + expectedEvent.toString() + " event", String.valueOf(true), String.valueOf(event.matches("(.*)" + expectedEvent.toString() + "(.*)"))));
                    expectedEvents.remove(expectedEvent);
                    break;
                }
            }
        }
        if (!expectedEvents.isEmpty())
            assertEquals(expectedEvents.toString(), "0", GeneralAssertionMessage("missing event(s)", expectedEvents.toString(), "0"));
    }

    /**
     * <h1>Assert the Splunk Time/Date stamp - T2 requirement!</h1>
     * This function assert the Splunk events according to a list of expected events
     *
     * @param splunkEvents   These events come from a request to Splunk server
     * @param expectedEvents These elements come from data provider
     */
    public void SplunkTimeStampAssertion(ArrayList<String> splunkEvents, ArrayList expectedEvents, String accountId) {
        for (String event : splunkEvents) {
            for (Object expectedEvent : expectedEvents) {
                if (event.matches("^.*" + expectedEvent.toString() + ".*$")) {
                    assertTrue(event.matches("^.*" + TIMESTAMP_TIMEZONE_REGEX_USERAGENT_REGEX + "(" + accountId + ")?.*$"), GeneralAssertionMessage("log row[" + (splunkEvents.indexOf(event) + 1) + "] includes time/date stamp, timezone and user agent", String.valueOf(true), String.valueOf(event.matches("^.*" + TIMESTAMP_TIMEZONE_REGEX_USERAGENT_REGEX + ".*$"))));
                    expectedEvents.remove(expectedEvent);
                    break;
                }
            }
        }
        if (!expectedEvents.isEmpty())
            assertEquals(expectedEvents.toString(), "0", GeneralAssertionMessage("missing event(s)", "0", expectedEvents.toString()));
    }

    /**
     * <h1>Assert the Splunk Time/Date stamp - T2 requirement!</h1>
     * This function assert the Splunk events according to a list of expected events
     *
     * @param splunkEvents   These events come from a request to Splunk server
     * @param expectedEvents These elements come from data provider
     * @param dataFields     These elements come from data provider
     */
    public void SplunkDataFieldsAssertion(ArrayList<String> splunkEvents, ArrayList expectedEvents, String dataFields) {
        for (String event : splunkEvents) {
            for (Object expectedEvent : expectedEvents) {
                if (event.matches("^.*" + expectedEvent.toString() + ".*$")) {
                    assertTrue(event.matches("^.*" + dataFieldsRegEx(dataFields) + ".*$"), GeneralAssertionMessage("log row[" + (splunkEvents.indexOf(event) + 1) + "] includes data [" + dataFields + "]", String.valueOf(true), String.valueOf(event.matches("^.*" + dataFieldsRegEx(dataFields) + ".*$"))));
                    expectedEvents.remove(expectedEvent);
                    break;
                }
            }
        }
        if (!expectedEvents.isEmpty())
            assertEquals(expectedEvents.toString(), "0", GeneralAssertionMessage("missing event(s)", expectedEvents.toString(), "0"));
    }

    /**
     * <h1>Assert the Splunk event and data fields! - T5 requirement</h1>
     * This function assert the Splunk events according to a list of expected events and data fields
     *
     * @param splunkEvents   These events come from a request to Splunk server
     * @param expectedEvents These elements come from data provider
     * @param dataFields     These elements come from data provider
     */
    public void SplunkNegativeAssertion(ArrayList<String> splunkEvents, ArrayList expectedEvents, String dataFields) {
        String dataFieldsRegEx = dataFieldsRegEx(dataFields);
        for (String event : splunkEvents) {
            for (Object expectedEvent : expectedEvents) {
                if (event.matches("^.*" + expectedEvent.toString() + ".*$")) {
                    assertTrue(event.matches("^.*" + expectedEvent.toString() + ".*$"), GeneralAssertionMessage("log row[" + (splunkEvents.indexOf(event) + 1) + "] contains " + expectedEvent.toString() + " event", String.valueOf(true), String.valueOf(event.matches("^.*" + expectedEvent.toString() + ".*$"))));
                    assertTrue(event.matches("^.*" + dataFieldsRegEx + ".*$"), GeneralAssertionMessage("log row[" + (splunkEvents.indexOf(event) + 1) + "] includes data [" + dataFields + "]", String.valueOf(true), String.valueOf(event.matches("^.*" + dataFieldsRegEx + ".*$"))));
                    expectedEvents.remove(expectedEvent);
                    break;
                }
            }
        }
        if (!expectedEvents.isEmpty())
            assertEquals(expectedEvents.toString(), "0", GeneralAssertionMessage("missing event(s)", expectedEvents.toString(), "0"));
    }

    /**
     * <h1>Assert the Splunk events, data fields and time/date stamp! - T1, T2 and T3 requirements</h1>
     * This function assert the Splunk events, time/data stamp, timezone and data fields according to a list of expected events.
     *
     * @param splunkEvents   These events come from a request to Splunk server
     * @param expectedEvents These elements come from data provider
     * @param dataFields     These elements come from data provider
     */
    public void SplunkRequestAssertion(ArrayList<String> splunkEvents, ArrayList expectedEvents, String dataFields) {
        for (String event : splunkEvents) {
            for (Object expectedEvent : expectedEvents) {
                if (event.matches("^.*" + expectedEvent.toString() + ".*$")) {
                    assertTrue(event.matches("^.*" + expectedEvent.toString() + ".*$"), GeneralAssertionMessage("log row[" + (splunkEvents.indexOf(event) + 1) + "] contains " + expectedEvent.toString() + " event", String.valueOf(true), String.valueOf(event.matches("^.*" + expectedEvent.toString() + ".*$"))));
                    assertTrue(event.matches("^.*" + TIMESTAMP_TIMEZONE_REGEX_USERAGENT_REGEX + ".*$"), GeneralAssertionMessage("log row[" + (splunkEvents.indexOf(event) + 1) + "] includes time/date stamp, timezone and user agent", String.valueOf(true), String.valueOf(event.matches("^.*" + TIMESTAMP_TIMEZONE_REGEX_USERAGENT_REGEX + ".*$"))));
                    assertTrue(event.matches("^.*" + dataFieldsRegEx(dataFields) + ".*$"), GeneralAssertionMessage("log row[" + (splunkEvents.indexOf(event) + 1) + "] includes data fields [" + dataFields + "]", String.valueOf(true), String.valueOf(event.matches("^.*" + dataFieldsRegEx(dataFields) + ".*$"))));
                    expectedEvents.remove(expectedEvent);
                    break;
                }
            }
        }
        if (!expectedEvents.isEmpty())
            assertEquals(expectedEvents.toString(), "0", GeneralAssertionMessage("missing event(s)", expectedEvents.toString(), "0"));
    }

    /**
     * <h1>Assert the Splunk event to check the password field!</h1>
     * This function assert the Splunk events according to a list of expected events
     *
     * @param splunkEvents   These events come from a request to Splunk server
     * @param expectedEvents These elements come from data provider
     * @param dataFields     These elements come from data provider
     */
    public void SplunkPasswordAssertion(ArrayList<String> splunkEvents, ArrayList expectedEvents, String dataFields) {
        for (String event : splunkEvents) {
            for (Object expectedEvent : expectedEvents) {
                if (!event.matches("(.*)" + expectedEvent.toString() + "(.*)")) {
                    assertFalse(event.matches("(.*)" + dataFieldsRegEx(dataFields) + "(.*)"), GeneralAssertionMessage("log row[" + (splunkEvents.indexOf(event) + 1) + "] contains data [" + dataFields + "]", String.valueOf(false), String.valueOf(event.matches("(.*)" + dataFieldsRegEx(dataFields) + "(.*)"))));
                    expectedEvents.remove(expectedEvent);
                    break;
                }
            }
        }
        if (!expectedEvents.isEmpty())
            assertEquals(expectedEvents.toString(), "0", GeneralAssertionMessage("missing event(s)", expectedEvents.toString(), "0"));
    }

    /**
     * Assert the array size
     *
     * @param message
     * @param response
     * @param responseSearchTerm
     * @param expectedResponse
     * @param expectedSearchTerm
     * @throws ParseException
     */
    public void inventoryAssertion(String message, Response response, String responseSearchTerm, String expectedResponse, String expectedSearchTerm) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONArray actualArray = (JSONArray) parser.parse(response.getBody().asString()); //parse response body
        JSONArray expectedArray = (JSONArray) parser.parse(expectedResponse); //parse expected response
        JSONObject actualObject = (JSONObject) actualArray.get(0);
        JSONObject expectedObject = (JSONObject) expectedArray.get(0);
        String actual = searchJSONObject(actualObject, responseSearchTerm);
        String expected = searchJSONObject(expectedObject, expectedSearchTerm);
        assertEquals(actual, expected, GeneralAssertionMessage(message, String.valueOf(actual), String.valueOf(expected)));
    }

    /**
     * <h1>Assert fields existence into an object that is in an array</h1>
     *
     * @param message                 String regarding to the path where the array is, example.- data.results, the message will be "Expected data.results[0].patientId exists= true"
     * @param response                Response that comes from the request
     * @param expectedFieldsExistence String of the fields expected and tht will be asserting, example.- patientId,patientFirstName,patientLastName,patientBirthdate,externalPatientId,deviceProductType
     * @throws ParseException
     */
    public void FieldsExistenceAssertion(String message, Response response, String expectedFieldsExistence) throws ParseException {
        FieldsExistenceAssertion(message, response, expectedFieldsExistence, false);
    }

    /**
     * <h1>Overload for Assert fields existence into an object that is in an array</h1>
     *
     * @param message                 String regarding to the path where the array is, example.- data.results, the message will be "Expected data.results[0].patientId exists= true"
     * @param response                Response that comes from the request
     * @param expectedFieldsExistence String of the fields expected and tht will be asserting, example.- patientId,patientFirstName,patientLastName,patientBirthdate,externalPatientId,deviceProductType
     * @param isASingleElement        This flag defines if the response corresponds to a single element which is not contained into an array
     * @throws ParseException
     */
    public void FieldsExistenceAssertion(String message, Response response, String expectedFieldsExistence, boolean isASingleElement) throws ParseException {
        JSONParser parser = new JSONParser();
        String[] responseTerms = message.split("\\.");
        JSONObject actualObject = (JSONObject) parser.parse(response.getBody().asString()); //parse response body
        if(!isASingleElement) {
            JSONArray actualArray = getJSONArray(actualObject, responseTerms);
            for (Object object : actualArray) {
                for (Object field : StringToArrayList(expectedFieldsExistence)) {
                    assertTrue(object.toString().contains(field.toString()), GeneralAssertionMessage(message + "[" + actualArray.indexOf(object) + "]." + field.toString() + " exists", String.valueOf(object.toString().contains(field.toString())), String.valueOf(true)));
                }
            }
        }
        else {
            for (Object field : StringToArrayList(expectedFieldsExistence)) {
                assertTrue(actualObject.toString().contains(field.toString()), GeneralAssertionMessage(message + field.toString() + " exists", String.valueOf(actualObject.toString().contains(field.toString())), String.valueOf(true)));
            }
        }
    }

    /**
     * <h1>Assert fields existence inside the results Object/h1>
     *
     * @param message       String regarding to the path where the array is, example.- data.results, the message will be "Expected data.results[0].patientId exists= true"
     * @param results       Results, content of data.results section of each Api Call
     * @param expectedField String of the fields expected and tht will be asserting, example.- patientId,patientFirstName,patientLastName,patientBirthdate,externalPatientId,deviceProductType
     * @throws ParseException
     */
    public void FieldsExistenceAssertion(String message, ArrayList results, String expectedField) {
        for (int i = 0; i < results.size(); i++) {
            HashMap item = (HashMap) results.get(i);
            assertTrue(item.containsKey(expectedField), GeneralAssertionMessage(message + "[" + i + "]." + expectedField + " exists", String.valueOf(item.toString().contains(expectedField)), String.valueOf(true)));
        }
    }

    /**
     * <h1>Assert the array errors for a unsuccessful request, this function iterate throght the array and assert all the items</h1>
     *
     * @param response         Response object with the errors retrieved from the request
     * @param expectedResponse Expected request with the errors
     * @throws ParseException
     */
    public void ArrayErrorsAssertion(Response response, String expectedResponse) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject actualObject = (JSONObject) parser.parse(response.getBody().asString()); //parse response body
        JSONObject expectedObject = (JSONObject) parser.parse(expectedResponse); //parse expected body
        JSONArray actualErrors = getJSONArray(actualObject, "errors");
        JSONArray expectedErrors = getJSONArray(expectedObject, "errors");
        for (Object error : actualErrors) {
            for (Object expectedError : expectedErrors) {
                if (error.equals(expectedError)) {
                    assertEquals(error.toString(), expectedError.toString(), GeneralAssertionMessage("errors[" + actualErrors.indexOf(error) + "]", error.toString(), expectedError.toString()));
                    expectedErrors.remove(expectedError);
                    break;
                }
            }
        }
        if (!expectedErrors.isEmpty())
            assertEquals(expectedErrors.toString(), "0", GeneralAssertionMessage("missing error(s)", expectedErrors.toString(), "0"));
    }

    /**
     * <h1>Assert lastName and firstName according to search approach for Reports and Patients endpoints</h1>
     *
     * @param resultsResponse ArrayList of the results found in the response
     * @param lstWords        ArrayList of the words found in the search request
     * @param isReportSearch  Boolean value determines if the response comes from Reports endpoint or another one
     */
    public void SearchApproachLastFirstNameAssertion(ArrayList resultsResponse, ArrayList lstWords, boolean isReportSearch) {
        for (int i = 0; i < resultsResponse.size(); i++) {
            Map<String, Object> userResult = (Map<String, Object>) resultsResponse.get(i);
            String pathElement = "";
            if (isReportSearch) {
                userResult = (Map<String, Object>) userResult.get("patient");
                pathElement = ".patient";
            }
            if (userResult.get("firstName").toString().toLowerCase().matches("^" + lstWords.get(0).toString().toLowerCase() + ".*$")) {
                assertTrue(userResult.get("firstName").toString().toLowerCase().matches("^" + lstWords.get(0).toString().toLowerCase() + ".*$"), GeneralAssertionMessage("data.results[" + i + "]" + pathElement + ".firstName starts with " + lstWords.get(0), String.valueOf(userResult.get("firstName").toString().toLowerCase().matches("^" + lstWords.get(0).toString().toLowerCase() + ".*$")), String.valueOf(true)));
                assertTrue(userResult.get("lastName").toString().toLowerCase().matches("^" + lstWords.get(1).toString().toLowerCase() + ".*$"), GeneralAssertionMessage("data.results[" + i + "]" + pathElement + ".lastName starts with " + lstWords.get(1), String.valueOf(userResult.get("lastName").toString().toLowerCase().matches("^" + lstWords.get(1).toString().toLowerCase() + ".*$")), String.valueOf(true)));
            } else {
                assertTrue(userResult.get("firstName").toString().toLowerCase().matches("^" + lstWords.get(1).toString().toLowerCase() + ".*$"), GeneralAssertionMessage("data.results[" + i + "]" + pathElement + ".firstName starts with " + lstWords.get(1), String.valueOf(userResult.get("firstName").toString().toLowerCase().matches("^" + lstWords.get(1).toString().toLowerCase() + ".*$")), String.valueOf(true)));
                assertTrue(userResult.get("lastName").toString().toLowerCase().matches("^" + lstWords.get(0).toString().toLowerCase() + ".*$"), GeneralAssertionMessage("data.results[" + i + "]" + pathElement + ".lastName starts with " + lstWords.get(0), String.valueOf(userResult.get("lastName").toString().toLowerCase().matches("^" + lstWords.get(0).toString().toLowerCase() + ".*$")), String.valueOf(true)));
            }
        }
        if (resultsResponse.isEmpty())
            assertFalse(true, "No results found");
    }

    /**
     * <h1>Assert serial number according to search approach for Reports and Patients endpoints</h1>
     *
     * @param resultsResponse ArrayList of the results found in the response
     * @param lstWords        ArrayList of the words found in the search request
     */
    public void SearchApproachSerialNumAssertion(ArrayList resultsResponse, ArrayList lstWords) {
        for (int i = 0; i < resultsResponse.size(); i++) {
            Map<String, Object> userResult = (Map<String, Object>) resultsResponse.get(i);
            assertEquals(userResult.get("serialNumber").toString(), lstWords.get(0), GeneralAssertionMessage("data.results[" + i + "].serialNumber", userResult.get("serialNumber").toString(), lstWords.get(0).toString()));
        }
        if (resultsResponse.isEmpty())
            assertFalse(true, "No results found");
    }

    /**
     * <h1>Assert fisrtName or lastName or externalId according to search approach for Reports and Patients endpoints</h1>
     *
     * @param resultsResponse ArrayList of the results found in the response
     * @param lstWords        ArrayList of the words found in the search request
     * @param isReportSearch  Boolean value determines if the response comes from Reports endpoint or another one
     */
    public void SearchApproachFirstLastExternalIdAssertion(ArrayList resultsResponse, ArrayList lstWords, boolean isReportSearch) {
        for (int i = 0; i < resultsResponse.size(); i++) {
            Map<String, Object> userResult = (Map<String, Object>) resultsResponse.get(i);
            String pathElement = "";
            if (isReportSearch) {
                userResult = (Map<String, Object>) userResult.get("patient");
                pathElement = ".patient";
            }
            if (userResult.get("firstName").toString().toLowerCase().matches("^" + lstWords.get(0).toString().toLowerCase() + ".*$")) {
                assertTrue(userResult.get("firstName").toString().toLowerCase().contains(lstWords.get(0).toString().toLowerCase()),GeneralAssertionMessage("data.results[" + i + "]" + pathElement + ".firstName ",String.valueOf(userResult.get("firstName")),String.valueOf(lstWords.get(0))));
            } else if (userResult.get("lastName").toString().toLowerCase().matches("^" + lstWords.get(0).toString().toLowerCase() + ".*$")) {
                assertTrue(userResult.get("lastName").toString().toLowerCase().contains(lstWords.get(0).toString().toLowerCase()),GeneralAssertionMessage("data.results[" + i + "]" + pathElement + ".lastName ",String.valueOf(userResult.get("lastName")),String.valueOf(lstWords.get(0))));
            } else
                assertTrue(userResult.get("externalPatientId").toString().toLowerCase().contains(lstWords.get(0).toString().toLowerCase()),GeneralAssertionMessage("data.results[" + i + "]" + pathElement + ".externalPatientId ",String.valueOf(userResult.get("externalPatientId")),String.valueOf(lstWords.get(0))));
        }
        if (resultsResponse.isEmpty())
            assertFalse(true, "No results found");
    }

    /**
     * <h1>Assert fisrtName or lastName or externalId according to search approach for Reports and Patients endpoints</h1>
     *
     * @param resultsResponse ArrayList of the results found in the response
     * @param lstWords        ArrayList of the words found in the search request
     * @param isReportSearch  Boolean value determines if the response comes from Reports endpoint or another one
     */
    public void SearchApproachPartialFirstLastExternalIdAssertion(ArrayList resultsResponse, ArrayList lstWords, boolean isReportSearch) {
        for (int i = 0; i < resultsResponse.size(); i++) {
            Map<String, Object> userResult = (Map<String, Object>) resultsResponse.get(i);
            String pathElement = "";
            if (isReportSearch) {
                userResult = (Map<String, Object>) userResult.get("patient");
                pathElement = ".patient";
            }
            if (userResult.get("firstName").toString().toLowerCase().contains(lstWords.get(0).toString().toLowerCase())) {
                assertTrue(userResult.get("firstName").toString().toLowerCase().contains(lstWords.get(0).toString().toLowerCase()), GeneralAssertionMessage("data.results[" + i + "]" + pathElement + ".firstName match to the partial value", String.valueOf(userResult.get("firstName").toString().contains(lstWords.get(0).toString())), String.valueOf(true)));
            } else if (userResult.get("lastName").toString().toLowerCase().contains(lstWords.get(0).toString().toLowerCase())) {
                assertTrue(userResult.get("lastName").toString().toLowerCase().contains(lstWords.get(0).toString().toLowerCase()), GeneralAssertionMessage("data.results[" + i + "]" + pathElement + ".lastName match to the partial value", String.valueOf(userResult.get("lastName").toString().contains(lstWords.get(0).toString())), String.valueOf(true)));
            } else
                assertTrue(userResult.get("externalPatientId").toString().contains(lstWords.get(0).toString()), GeneralAssertionMessage("data.results[" + i + "].externalPatientId match to the partial value", String.valueOf(userResult.get("externalPatientId").toString().contains(lstWords.get(0).toString())), String.valueOf(true)));
        }
        if (resultsResponse.isEmpty())
            assertFalse(true, "No results found");
    }

    /**
     * <h1>Assert lastName and firstName according to search approach for Reports and Patients endpoints</h1>
     *
     * @param resultsResponse ArrayList of the results found in the response
     * @param lstWords        ArrayList of the words found in the search request
     * @param isReportSearch  Boolean value determines if the response comes from Reports endpoint or another one
     */
    public void SearchApproachPartialLastFirstNameAssertion(ArrayList resultsResponse, ArrayList lstWords, boolean isReportSearch) {
        for (int i = 0; i < resultsResponse.size(); i++) {
            Map<String, Object> userResult = (Map<String, Object>) resultsResponse.get(i);
            String pathElement = "";
            if (isReportSearch) {
                userResult = (Map<String, Object>) userResult.get("patient");
                pathElement = ".patient";
            }
            if (userResult.get("firstName").toString().toLowerCase().contains(lstWords.get(0).toString().toLowerCase())) {
                assertTrue(userResult.get("firstName").toString().toLowerCase().contains(lstWords.get(0).toString().toLowerCase()), GeneralAssertionMessage("data.results[" + i + "]" + pathElement + ".firstName contains " + lstWords.get(0), String.valueOf(userResult.get("firstName").toString().toLowerCase().contains(lstWords.get(0).toString().toLowerCase())), String.valueOf(true)));
                assertTrue(userResult.get("lastName").toString().toLowerCase().contains(lstWords.get(1).toString().toLowerCase()), GeneralAssertionMessage("data.results[" + i + "]" + pathElement + ".lastName contains " + lstWords.get(1), String.valueOf(userResult.get("lastName").toString().toLowerCase().contains(lstWords.get(1).toString().toLowerCase())), String.valueOf(true)));
            } else {
                assertTrue(userResult.get("firstName").toString().toLowerCase().contains(lstWords.get(1).toString().toLowerCase()), GeneralAssertionMessage("data.results[" + i + "]" + pathElement + ".firstName contains " + lstWords.get(1), String.valueOf(userResult.get("firstName").toString().toLowerCase().contains(lstWords.get(1).toString().toLowerCase())), String.valueOf(true)));
                assertTrue(userResult.get("lastName").toString().toLowerCase().contains(lstWords.get(0).toString().toLowerCase()), GeneralAssertionMessage("data.results[" + i + "]" + pathElement + ".lastName contains " + lstWords.get(0), String.valueOf(userResult.get("lastName").toString().toLowerCase().contains(lstWords.get(0).toString().toLowerCase())), String.valueOf(true)));
            }
        }
        if (resultsResponse.isEmpty())
            assertFalse(true, "No results found");
    }

    /**
     * <h1>Assert fisrtName or lastName or PatientId according to search approach for Devices endpoints</h1>
     *
     * @param resultsResponse ArrayList of the results found in the response
     * @param lstWords        ArrayList of the words found in the search request
     * @param isReportSearch  Boolean value determines if the response comes from Reports endpoint or another one
     */
    public void SearchApproachPartialFirstLastPatientIdAssertion(ArrayList resultsResponse, ArrayList lstWords, boolean isReportSearch) {
        for (int i = 0; i < resultsResponse.size(); i++) {
            Map<String, Object> userResult = (Map<String, Object>) resultsResponse.get(i);
            String pathElement = "";
            if (isReportSearch) {
                userResult = (Map<String, Object>) userResult.get("patient");
                pathElement = ".patient";
            }
            if (userResult.get("firstName").toString().toLowerCase().contains(lstWords.get(0).toString().toLowerCase())) {
                assertTrue(userResult.get("firstName").toString().toLowerCase().contains(lstWords.get(0).toString().toLowerCase()), GeneralAssertionMessage("data.results[" + i + "]" + pathElement + ".firstName match to the partial value", String.valueOf(userResult.get("firstName").toString().contains(lstWords.get(0).toString())), String.valueOf(true)));
            } else if (userResult.get("lastName").toString().toLowerCase().contains(lstWords.get(0).toString().toLowerCase())) {
                assertTrue(userResult.get("lastName").toString().toLowerCase().contains(lstWords.get(0).toString().toLowerCase()), GeneralAssertionMessage("data.results[" + i + "]" + pathElement + ".lastName match to the partial value", String.valueOf(userResult.get("lastName").toString().contains(lstWords.get(0).toString())), String.valueOf(true)));
            } else
                assertTrue(userResult.get("externalPatientId").toString().contains(lstWords.get(0).toString()), GeneralAssertionMessage("data.results[" + i + "].externalPatientId match to the partial value", String.valueOf(userResult.get("externalPatientId").toString().contains(lstWords.get(0).toString())), String.valueOf(true)));
        }
        if (resultsResponse.isEmpty())
            assertFalse(true, "No results found");
    }

    /**
     * <h1>Assert patientFirstName, patientLastName, patientId or serialNumber according to search approach for Patients endpoint</h1>
     *
     * @param resultsResponse ArrayList of the results found in the response
     * @param lstWords        ArrayList of the words found in the search request
     */
    public void SearchApproachPartialPatientFirstLastIdAssertion(ArrayList resultsResponse, ArrayList lstWords) {
        for (int i = 0; i < resultsResponse.size(); i++) {
            Map<String, Object> userResult = (Map<String, Object>) resultsResponse.get(i);
            if (userResult.get("patientFirstName").toString().toLowerCase().contains(lstWords.get(0).toString().toLowerCase())) {
                assertTrue(userResult.get("patientFirstName").toString().toLowerCase().contains(lstWords.get(0).toString().toLowerCase()), GeneralAssertionMessage("data.results[" + i + "]" + ".patientFirstName match to the partial value", String.valueOf(userResult.get("patientFirstName").toString().contains(lstWords.get(0).toString())), String.valueOf(true)));
            } else if (userResult.get("patientLastName").toString().toLowerCase().contains(lstWords.get(0).toString().toLowerCase())) {
                assertTrue(userResult.get("patientLastName").toString().toLowerCase().contains(lstWords.get(0).toString().toLowerCase()), GeneralAssertionMessage("data.results[" + i + "]" + ".patientLastName match to the partial value", String.valueOf(userResult.get("patientLastName").toString().contains(lstWords.get(0).toString())), String.valueOf(true)));
            } else if(userResult.get("patientExternalId").toString().contains(lstWords.get(0).toString())){
                assertTrue(userResult.get("patientExternalId").toString().contains(lstWords.get(0).toString()), GeneralAssertionMessage("data.results[" + i + "].patientExternalId match to the partial value", String.valueOf(userResult.get("patientExternalId").toString().contains(lstWords.get(0).toString())), String.valueOf(true)));
            }else
                assertTrue(userResult.get("serialNumber").toString().contains(lstWords.get(0).toString()), GeneralAssertionMessage("data.results[" + i + "].serialNumber match to the partial value", String.valueOf(userResult.get("serialNumber").toString().contains(lstWords.get(0).toString())), String.valueOf(true)));
        }
        if (resultsResponse.isEmpty())
            assertFalse(true, "No results found");
    }

}
