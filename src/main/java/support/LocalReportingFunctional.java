package support;

import objects.Configuration;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import test.java.testSteps.JamaSteps;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static support.ConstantData.*;
import static support.ConstantData.QUERY_DESCRIPTION;
import static support.ConstantData.SEARCH_PARAMETER_DESCRIPTION;
import static support.Enumerations.Versions.*;

import static support.SysUtils.CapitalizedComponentName;
import static support.SysUtils.getConfiguration;

public class LocalReportingFunctional {

  private static float passRate;
  private static float failRate;
  private static int passed;
  private static int failed;
  private int stepNumber = 0;
  private String html;
  private List<String> lstTCNamesInScope = new ArrayList<>();
  private List<String> lstTCNamesOutScope = new ArrayList<>();
  private List<String> lstComponentsInScope = new ArrayList<>();
  private ArrayList executedTestCasesGlobal = new ArrayList<>();

  private final String COMPONENT = "component";
  private final String ID = "id";
  private final String RESULT = "result";


  private int finalTotal = 0;
  private int finalPass = 0 ;
  private int finalFail = 0 ;
  private float finalPassRate=0;
  private static JSONArray resultObjects; //holds all results
  private static JSONArray V1_0_Results = new JSONArray(); //all V1_0 results
  private static JSONArray V1_5_Results = new JSONArray(); //all V1_5 results
  private static JSONArray V1_6_Results = new JSONArray(); //all V1_6 results
  private static JSONArray V1_7_Results = new JSONArray(); //all V1_7 results
  private static JSONArray V1_8_Results = new JSONArray(); //all V1_8 results
  private static JSONArray V2_0_Results = new JSONArray(); //all V2_0 results
  private static JSONArray V2_1_Results = new JSONArray(); //all V2_1 results
  private static JSONArray V2_2_Results = new JSONArray(); //all V2_2 results
  private static JSONArray V2_3_Results = new JSONArray(); //all V2_3 results
  private static JSONArray versionless_Results = new JSONArray(); //all results without a web service version
  private static JSONArray V1_0_Steps = new JSONArray(); //all V1_0 steps
  private static JSONArray V1_5_Steps = new JSONArray(); //all V1_5 steps
  private static JSONArray V1_6_Steps = new JSONArray(); //all V1_6 steps
  private static JSONArray V1_7_Steps = new JSONArray(); //all V1_7 steps
  private static JSONArray V1_8_Steps = new JSONArray(); //all V1_8 steps
  private static JSONArray V2_0_Steps = new JSONArray(); //all V2_0 steps
  private static JSONArray V2_1_Steps = new JSONArray(); //all V2_1 steps
  private static JSONArray V2_2_Steps = new JSONArray(); //all V2_2 steps
  private static JSONArray V2_3_Steps = new JSONArray(); //all V2_3 steps
  private static JSONArray versionless_Steps = new JSONArray(); //all steps without a web service version
  private static final Pattern JAMA_ITEM_MATCHER = Pattern.compile(JAMA_ITEM_REGEX);

  private JamaSteps jamaSteps = new JamaSteps();
  private static String ShowElementsforNoVersion = "show(\"NO_VERSION\",\"V1_0\", \"V1_5\", \"V1_6\", \"Summary\", \"V1_7\", \"V1_8\", \"V2_0\", \"V2_1\", \"V2_2\", \"V2_3\");";
  private static String ShowElementsforSummary = "show(\"Summary\",\"V1_0\", \"V1_5\", \"V1_6\", \"V1_7\", \"V1_8\", \"V2_0\", \"V2_1\", \"V2_2\", \"V2_3\", \"NO_VERSION\");";
  private static String ShowElementsforV10 = "show(\"V1_0\",\"V1_5\", \"V1_6\", \"V1_7\", \"V1_8\", \"V2_0\", \"V2_1\", \"V2_2\", \"V2_3\", \"Summary\", \"NO_VERSION\");";
  private static String ShowElementsforV15 = "show(\"V1_5\",\"V1_0\", \"Summary\", \"V1_6\", \"V1_7\", \"V1_8\", \"V2_0\", \"V2_1\", \"V2_2\", \"V2_3\", \"NO_VERSION\");";
  private static String ShowElementsforV16 = "show(\"V1_6\",\"V1_0\", \"V1_5\", \"Summary\", \"V1_7\", \"V1_8\", \"V2_0\", \"V2_1\", \"V2_2\", \"V2_3\", \"NO_VERSION\");";
  private static String ShowElementsforV17 = "show(\"V1_7\",\"V1_0\", \"V1_5\", \"V1_6\", \"V1_8\", \"V2_0\", \"V2_1\", \"V2_2\", \"V2_3\", \"Summary\", \"NO_VERSION\");";
  private static String ShowElementsforV18 = "show(\"V1_8\",\"V1_0\", \"V1_5\", \"V1_6\",\"V1_7\", \"V2_0\", \"V2_1\", \"V2_2\", \"V2_3\", \"Summary\", \"NO_VERSION\");";
  private static String ShowElementsforV20 = "show(\"V2_0\",\"V1_0\", \"V1_5\", \"V1_6\",\"V1_7\", \"V1_8\", \"V2_1\", \"V2_2\", \"V2_3\", \"Summary\", \"NO_VERSION\");";
  private static String ShowElementsforV21 = "show(\"V2_1\",\"V1_0\", \"V1_5\", \"V1_6\",\"V1_7\", \"V1_8\", \"V2_0\", \"V2_2\", \"V2_3\", \"Summary\", \"NO_VERSION\");";
  private static String ShowElementsforV22 = "show(\"V2_2\",\"V1_0\", \"V1_5\", \"V1_6\",\"V1_7\", \"V1_8\", \"V2_0\", \"V2_1\", \"V2_3\", \"Summary\", \"NO_VERSION\");";
  private static String ShowElementsforV23 = "show(\"V2_3\",\"V1_0\", \"V1_5\", \"V1_6\",\"V1_7\", \"V1_8\", \"V2_0\", \"V2_1\", \"V2_2\", \"Summary\", \"NO_VERSION\");";
  /**
   * Save the HTML report in the project root
   * @param config
   */
  public void saveReport(Configuration config){
    String report = EmailBody(config);
    try (PrintWriter out = new PrintWriter(config.getHtmlReportFileName())) {
      out.println(report);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  /**
   * It gets the component name from new test cases related to certain kinda string like  dashboard_myInterpretationInbox_p0
   * It uses a RegEx in order to get the name
   *
   * @param testCaseName String name of type dashboard_myInterpretationInbox_p0
   * @return String component name
   */
  private String getComponentName(String testCaseName){
    Pattern pattern = Pattern.compile(V15_COMPLETE_TC_NAME_REGEX);
    Matcher matcher = pattern.matcher(testCaseName);
    if (matcher.find())
      matcher.group(1);
    return matcher.group(1);
  }

  /**
   * Fill the lstComponentsInScope list with the components found on all the test cases
   * Fill the lstTCNamesInScope list with the test cases in the scope according to Verification Plan/Verification Report
   * Fill the lstTCNamesOutScope list with the test cases out of the scope according to Verification Plan/Verification Report
   */
  private void FillListsComponentsTestCases() {
    lstTCNamesInScope = new ArrayList<>();
    lstTCNamesOutScope = new ArrayList<>();
    for (Object resultObject : resultObjects) {
      JSONObject result = (JSONObject) resultObject;
      String testCaseName = result.get(TEST_CASE_JSONNODE).toString();

      if(testCaseName.matches(JAMA_REMOVE_HYPERLINK_REGEX)){ //detect hyperlink tags
        testCaseName = testCaseName.replaceAll(JAMA_REMOVE_HYPERLINK_REGEX, ""); //strip hyperlink tags for the purposes of determining component
        testCaseName = testCaseName.trim();
      }
      if(testCaseName.matches("^.*" + ID_VERIFICATION_PLAN + ".*$") || //has the "sdzmds" designation
              testCaseName.matches(V15_COMPLETE_TC_NAME_REGEX)){
        if(testCaseName.contains(ID_VERIFICATION_PLAN)){
          lstTCNamesInScope.add(testCaseName.substring(0, testCaseName.lastIndexOf(ID_VERIFICATION_PLAN)));
        }else {
          lstTCNamesInScope.add(getComponentName(testCaseName));
        }
      }else {
        lstTCNamesOutScope.add(testCaseName);
      }
    }

    LoggedTCOutscope();
    lstComponentsInScope = lstTCNamesInScope.stream().distinct().collect(Collectors.toList());
  }

  /**
   * Retrieve the test cases related to specific component
   * @param componentName
   * @return ArrayList
   */
  private ArrayList GetTestCasesByComponent(String componentName){
    ArrayList testCasesByComponent = new ArrayList();
    ArrayList testCasesToRemove = new ArrayList();
    for (Object testCase : executedTestCasesGlobal) {
      HashMap testCaseMap = (HashMap) testCase;
      if (testCaseMap.get(ID).toString().matches("^(" + componentName + ID_VERIFICATION_PLAN + ").*$") ||
              testCaseMap.get(ID).toString().matches("^" + componentName + V15_REQUIREMENT_REGEX + "$")) {
        testCasesByComponent.add(testCaseMap);
        testCasesToRemove.add(testCase);
      }
    }
    RemoveTestListedOnComponent(testCasesToRemove);
    return testCasesByComponent;
  }

  /**
   * Remove the test case from the ArrayList of the total test cases executed once they have been added to a component
   * @param listTestCasesToRemove ArrayList of the objects to remove once the test case has been added to a component
   */
  private void RemoveTestListedOnComponent(ArrayList<Integer> listTestCasesToRemove){
    for (Object testCase: listTestCasesToRemove) {
      executedTestCasesGlobal.remove(testCase);
    }
  }

  /**
   * Create a regular expression with the different components found in the test cases, i.e. user_management|session_management|reports
   * @return
   */
  private String ComponentsInScopeRegEx() {
    StringBuilder componentsInScope = new StringBuilder();
    for (String component : lstComponentsInScope) {
      componentsInScope.append(component).append("|");
    }
    return componentsInScope.substring(0, componentsInScope.length() - 1);
  }

  /**
   * Print out the test cases that are not part of the verification plan.
   * These test cases do not match in with RegEx get the component names
   */
  private void LoggedTCOutscope(){
    if (lstTCNamesOutScope.size() > 0) {
      System.out.println("\n******** Test cases out of scope. NOT in HTML report. ********");
      System.out.println("There are " + lstTCNamesOutScope.size() + " test cases out of scope:");
      for (String resultObject : lstTCNamesOutScope){        
        System.out.println(String.valueOf(lstTCNamesOutScope.indexOf(resultObject) + 1) + ". " + resultObject + "\n");
      }
    } else {
      System.out.println("\n******** There aren't test cases out of scope. ********\n");
    }
  }

  /**
   * use JAMA integration to find which version (1.0, 1.5, 1.6, etc) that a test case (in JAMA terms, an "item") belongs to.
   * @param testCaseName String name of the test case
   * @return
   */
  private String GetJamaVersion(String testCaseName){
    try {
      Matcher jamaMatcher = JAMA_ITEM_MATCHER.matcher(testCaseName);
      if (jamaMatcher.find())
        return jamaSteps.parseJamaVersion(jamaSteps.getJamaTags(jamaMatcher.group(0))); //ask JAMA API for the test case tags, then extract the relevant version tag as a String
    }
    catch(Exception e) //if the JAMA URL in the test case name is invalid or if the test case is not tagged with a version #
    {
      e.printStackTrace();
    }
    return NO_VERSION.name(); //if version can't can be found, then default to NO_VERSION. That means it will be considered "unsorted".
  }

  /** sort all the resultObject items into separate lists, sorted by the web service version
   *
   */
  private void SortJamaVersions(){
    //sort result objects into version numbers
    SysUtils utils = new SysUtils();
    JSONArray stepsObjects = utils.ReadJsonFile(TESTS_RESULTS_JSON_FILE_STEPS);
    int stepNumber = 0;

    for (Object resultObject : resultObjects) {
      JSONObject result = (JSONObject) resultObject;
      String version = GetJamaVersion(result.get(TEST_CASE_JSONNODE).toString());  //get JAMA WS version number, defaults to 1.0 if it can't be found
      //Collect steps of the actual test
      JSONObject step;
      JSONArray stepsTest = new JSONArray();
      do {
        step = (JSONObject) stepsObjects.get(stepNumber);
        stepsTest.add(step);
        stepNumber++;
      } while (!step.containsKey("END"));
      if (!(version != null))
        version = "undefined";

      try {
        switch (version) { //can't use enums for this, so string literals it is
          case ("V1_0"): {
            V1_0_Results.add(resultObject);
            V1_0_Steps.addAll(stepsTest);
            break;
          }
          case ("V1_5"): {
            V1_5_Results.add(resultObject);
            V1_5_Steps.addAll(stepsTest);
            break;
          }
          case ("V1_6"): {
            V1_6_Results.add(resultObject);
            V1_6_Steps.addAll(stepsTest);
            break;
          }
          case ("V1_7"): {
            V1_7_Results.add(resultObject);
            V1_7_Steps.addAll(stepsTest);
            break;
          }
          case ("V1_8"): {
            V1_8_Results.add(resultObject);
            V1_8_Steps.addAll(stepsTest);
            break;
          }
          case ("V2_0"): {
            V2_0_Results.add(resultObject);
            V2_0_Steps.addAll(stepsTest);
            break;
          }
          case ("V2_1"): {
            V2_1_Results.add(resultObject);
            V2_1_Steps.addAll(stepsTest);
            break;
          }
          case ("V2_2"): {
            V2_2_Results.add(resultObject);
            V2_2_Steps.addAll(stepsTest);
            break;
          }
          case ("V2_3"): {
            V2_3_Results.add(resultObject);
            V2_3_Steps.addAll(stepsTest);
            break;
          }
          default:
            versionless_Results.add(resultObject); //if something goes wrong, default to having no version
            versionless_Steps.addAll(stepsTest);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * opposite of SortJamaVersions. Gives you version based on which versioned JSONArray it happens to be in.
   * @param resultObject the result object to evaluate
   * @returns String the version string.
   */
  private String RetrievJamaVersion(JSONObject resultObject){
    if (V1_0_Results.contains(resultObject))
    {
      return V1_0.name();
    }
    else if (V1_5_Results.contains(resultObject))
    {
      return V1_5.name();
    }
    else if (V1_6_Results.contains(resultObject))
    {
      return V1_6.name();
    }
    else if (V1_7_Results.contains(resultObject))
    {
      return V1_7.name();
    }
    else if (V1_8_Results.contains(resultObject))
    {
      return V1_8.name();
    }
    else if (V2_0_Results.contains(resultObject))
    {
      return V2_0.name();
    }
    else if (V2_1_Results.contains(resultObject))
    {
      return V2_1.name();
    }
    else if (V2_2_Results.contains(resultObject))
    {
      return V2_2.name();
    }
    else if (V2_3_Results.contains(resultObject))
    {
      return V2_3.name();
    }
    return NO_VERSION.name();
  }

  /**
   * Read the JSON file and build the HTML body
   * @return emailBody as string
   */
  private String EmailBody(Configuration config) {
    SysUtils utils = new SysUtils();

    resultObjects = utils.ReadJsonFile(TESTS_RESULTS_JSON_FILE);
    FillListsComponentsTestCases();
    passed = 0;
    failed = 0;

    for (Object resultObject : resultObjects) { //go through results and count pass/fail rate
      JSONObject result = (JSONObject) resultObject;
      String testCaseName = result.get(TEST_CASE_JSONNODE).toString();
      if(testCaseName.matches(JAMA_REMOVE_HYPERLINK_REGEX)){ //detect hyperlink tags
        testCaseName = testCaseName.replaceAll(JAMA_REMOVE_HYPERLINK_REGEX, "").trim(); //strip hyperlink tags for the purposes of determining component
      }
      if (testCaseName.matches("^(" + ComponentsInScopeRegEx() + ")" + ID_VERIFICATION_PLAN + ".*$") ||
              testCaseName.matches("^(" + ComponentsInScopeRegEx() + ")" + V15_REQUIREMENT_REGEX + "$")) {
        if (result.get(RESULT_JSONNODE).equals(PASS_RESULT)) {
          passed = passed + 1;
        } else if (result.get(RESULT_JSONNODE).equals(FAIL_RESULT)) {
          failed = failed + 1;
        }
      }
    }
    int passedChartSize = 0;
    int failedChartSize = 0;
    if (resultObjects.size() != 0) {
      passedChartSize = ((passed * 500) / lstTCNamesInScope.size());
      failedChartSize = ((failed * 500) / lstTCNamesInScope.size());

      passRate = ((float) (passed * 100) / lstTCNamesInScope.size());
      failRate = ((float) (failed * 100) / lstTCNamesInScope.size());
    }

    SortJamaVersions();


    html = "<!DOCTYPE html>"
            + "<html>"
            + "		<head>"
            + "    <link href=\"https://stackpath.bootstrapcdn.com/bootstrap/4.2.1/css/bootstrap.min.css\" rel=\"stylesheet\" integrity=\"sha384-GJzZqFGwb1QTTN6wy59ffF1BuGJpLSa9DkKMp0DgiMDm4iYMj70gZWKYbI706tWS\"\n"
            + "        crossorigin=\"anonymous\">"
            + " <script src=\"https://stackpath.bootstrapcdn.com/bootstrap/4.2.1/js/bootstrap.min.js\" integrity=\"sha384-B0UglyR+jN6CkvvICOB2joaf5I4l3gm9GU6Hc1og6Ls7i6U/mkkaduKaBhlAXv9k\" crossorigin=\"anonymous\"></script>"
            + "         <script> "
            + "             function show() { "
            + "             document.getElementById(arguments[0]).style.display='block';" //first argument of show() is displayed normally
            + "             document.getElementById(\"linkTo\" + arguments[0]).disabled=true;" //disable the buttons for the tables you're viewing
            + "             document.getElementById(\"linkTo\" + arguments[0]).style.backgroundColor =\"#e74c3c\";" //disable the buttons for the tables you're viewing
            + "             document.getElementById(\"downlinkTo\" + arguments[0]).style.backgroundColor =\"#e74c3c\";" //disable the buttons for the tables you're viewing

            + "             for (var i =1; i<arguments.length; i++){"
            + "                 document.getElementById(arguments[i]).style.display='none'; " //hide the tables you're not viewing
            + "                 document.getElementById(\"linkTo\" + arguments[i]).disabled=false; " //enable the buttons for the tables you're not viewing
            +"                  document.getElementById(\"linkTo\" + arguments[i]).style.backgroundColor =\"#9ACD32\";"
            +"                  document.getElementById(\"downlinkTo\" + arguments[i]).style.backgroundColor =\"#9ACD32\"; }"
            + "             return false; } "
            + "         </script>"
            + "        <script src='https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js'></script>"
            + "        <script type='text/javascript' src='/path/to/jquery.tablesorter.js'></script> "
            + "        <script src='https://cdnjs.cloudflare.com/ajax/libs/jquery.tablesorter/2.31.0/js/jquery.tablesorter.js'></script> "
            + "        <script>$(document).ready(function() { $(\"#summaryTable\").tablesorter(); } );</script>"
            + "        <script>$(document).ready(function() { $(\"#summaryTable\").tablesorter( {sortList: [[0,0], [1,0]]} ); } );</script>"
            //Scroll function to Go to top button
            + "        <script type='text/javascript'>"
            + "         $(document).ready(function(){ "
            + "             $(window).scroll(function(){ "
            + "                if ($(this).scrollTop() > 100) { "
            + "                   $('#scroll').fadeIn(); "
            + "                } else { "
            + "                  $('#scroll').fadeOut(); "
            + "                   } "
            + "            }); "
            + "        $('#scroll').click(function(){ "
            + "           $(\"html, body\").animate({ scrollTop: 0 }, 600); "
            + "           return false; }); "
            + "            });"

            //Scroll function to downlinkToSummary button
            + "         $(document).ready(function(){ "
            + "             $(window).scroll(function(){ "
            + "                if ($(this).scrollTop() > 100) { "
            + "                   $('#downlinkToSummary').fadeIn(); "
            + "                } else { "
            + "                  $('#downlinkToSummary').fadeOut(); "
            + "                   } "
            + "            }); "
            + "        $('#downlinkToSummary').click(function(){ "
            +             ShowElementsforSummary
            + "           $(\"html, body\").animate({ scrollTop: 0 }, 600); "
            + "           return false; }); "
            + "            });"

            //Scroll function to downlinkToMiscellaneous button
            + "         $(document).ready(function(){ "
            + "             $(window).scroll(function(){ "
            + "                if ($(this).scrollTop() > 100) { "
            + "                   $('#downlinkToNO_VERSION').fadeIn(); "
            + "                } else { "
            + "                  $('#downlinkToNO_VERSION').fadeOut(); "
            + "                   } "
            + "            }); "
            + "        $('#downlinkToNO_VERSION').click(function(){ "
            +             ShowElementsforNoVersion
            + "           $(\"html, body\").animate({ scrollTop: 0 }, 600); "
            + "           return false; }); "
            + "            });"

            //Scroll function to downlinkToV1_0 button
            + "         $(document).ready(function(){ "
            + "             $(window).scroll(function(){ "
            + "                if ($(this).scrollTop() > 100) { "
            + "                   $('#downlinkToV1_0').fadeIn(); "
            + "                } else { "
            + "                  $('#downlinkToV1_0').fadeOut(); "
            + "                   } "
            + "            }); "
            + "        $('#downlinkToV1_0').click(function(){ "
            +             ShowElementsforV10
            + "           $(\"html, body\").animate({ scrollTop: 0 }, 600); "
            + "           return false; }); "
            + "            });"

            //Scroll function to downlinkToV1_5 button
            + "         $(document).ready(function(){ "
            + "             $(window).scroll(function(){ "
            + "                if ($(this).scrollTop() > 100) { "
            + "                   $('#downlinkToV1_5').fadeIn(); "
            + "                } else { "
            + "                  $('#downlinkToV1_5').fadeOut(); "
            + "                   } "
            + "            }); "
            + "        $('#downlinkToV1_5').click(function(){ "
            +             ShowElementsforV15
            + "           $(\"html, body\").animate({ scrollTop: 0 }, 600); "
            + "           return false; }); "
            + "            });"

            //Scroll function to downlinkToV1_6 button
            + "         $(document).ready(function(){ "
            + "             $(window).scroll(function(){ "
            + "                if ($(this).scrollTop() > 100) { "
            + "                   $('#downlinkToV1_6').fadeIn(); "
            + "                } else { "
            + "                  $('#downlinkToV1_6').fadeOut(); "
            + "                   } "
            + "            }); "
            + "        $('#downlinkToV1_6').click(function(){ "
            +             ShowElementsforV16
            + "           $(\"html, body\").animate({ scrollTop: 0 }, 600); "
            + "           return false; }); "
            + "            });"

            //Scroll function to downlinkToV1_7 button
            + "         $(document).ready(function(){ "
            + "             $(window).scroll(function(){ "
            + "                if ($(this).scrollTop() > 100) { "
            + "                   $('#downlinkToV1_7').fadeIn(); "
            + "                } else { "
            + "                  $('#downlinkToV1_7').fadeOut(); "
            + "                   } "
            + "            }); "
            + "        $('#downlinkToV1_7').click(function(){ "
            +                 ShowElementsforV17
            + "           $(\"html, body\").animate({ scrollTop: 0 }, 600); "
            + "           return false; }); "
            + "            });"

            //Scroll function to downlinkToV1_8 button
            + "         $(document).ready(function(){ "
            + "             $(window).scroll(function(){ "
            + "                if ($(this).scrollTop() > 100) { "
            + "                   $('#downlinkToV1_8').fadeIn(); "
            + "                } else { "
            + "                  $('#downlinkToV1_8').fadeOut(); "
            + "                   } "
            + "            }); "
            + "        $('#downlinkToV1_8').click(function(){ "
            +                 ShowElementsforV18
            + "           $(\"html, body\").animate({ scrollTop: 0 }, 600); "
            + "           return false; }); "
            + "            });"

            //Scroll function to downlinkToV2_0 button
            + "         $(document).ready(function(){ "
            + "             $(window).scroll(function(){ "
            + "                if ($(this).scrollTop() > 100) { "
            + "                   $('#downlinkToV2_0').fadeIn(); "
            + "                } else { "
            + "                  $('#downlinkToV2_0').fadeOut(); "
            + "                   } "
            + "            }); "
            + "        $('#downlinkToV2_0').click(function(){ "
            +                 ShowElementsforV20
            + "           $(\"html, body\").animate({ scrollTop: 0 }, 600); "
            + "           return false; }); "
            + "            });"

            //Scroll function to downlinkToV2_1 button
            + "         $(document).ready(function(){ "
            + "             $(window).scroll(function(){ "
            + "                if ($(this).scrollTop() > 100) { "
            + "                   $('#downlinkToV2_1').fadeIn(); "
            + "                } else { "
            + "                  $('#downlinkToV2_1').fadeOut(); "
            + "                   } "
            + "            }); "
            + "        $('#downlinkToV2_1').click(function(){ "
            +                 ShowElementsforV21
            + "           $(\"html, body\").animate({ scrollTop: 0 }, 600); "
            + "           return false; }); "
            + "            });"

            //Scroll function to downlinkToV2_2 button
            + "         $(document).ready(function(){ "
            + "             $(window).scroll(function(){ "
            + "                if ($(this).scrollTop() > 100) { "
            + "                   $('#downlinkToV2_2').fadeIn(); "
            + "                } else { "
            + "                  $('#downlinkToV2_2').fadeOut(); "
            + "                   } "
            + "            }); "
            + "        $('#downlinkToV2_2').click(function(){ "
            +                 ShowElementsforV22
            + "           $(\"html, body\").animate({ scrollTop: 0 }, 600); "
            + "           return false; }); "
            + "            });"

            //Scroll function to downlinkToV2_3 button
            + "         $(document).ready(function(){ "
            + "             $(window).scroll(function(){ "
            + "                if ($(this).scrollTop() > 100) { "
            + "                   $('#downlinkToV2_3').fadeIn(); "
            + "                } else { "
            + "                  $('#downlinkToV2_3').fadeOut(); "
            + "                   } "
            + "            }); "
            + "        $('#downlinkToV2_3').click(function(){ "
            +                 ShowElementsforV23
            + "           $(\"html, body\").animate({ scrollTop: 0 }, 600); "
            + "           return false; }); "
            + "            });"

            + "         $(document).ready(function () {"
            + "             $(document).on('click', '#linkToSummary', function() {"
            +                   ShowElementsforSummary
            + "             });"
            + "          });"
            + "         $(document).ready(function () {"
            + "             $(document).on('click', '#linkToNO_VERSION', function() {"
            +                   ShowElementsforNoVersion
            + "             });"
            + "          });"
            + "         $(document).ready(function () {"
            + "             $(document).on('click', '#linkToV1_0', function() {"
            +                   ShowElementsforV10
            + "             });"
            + "          });"
            + "         $(document).ready(function () {"
            + "             $(document).on('click', '#linkToV1_5', function() {"
            +                   ShowElementsforV15
            + "             });"
            + "          });"
            + "         $(document).ready(function () {"
            + "             $(document).on('click', '#linkToV1_6', function() {"
            +                   ShowElementsforV16
            + "             });"
            + "          });"
            + "         $(document).ready(function () {"
            + "             $(document).on('click', '#linkToV1_7', function() {"
            +                   ShowElementsforV17
            + "             });"
            + "          });"
            + "         $(document).ready(function () {"
            + "             $(document).on('click', '#linkToV1_8', function() {"
            +                   ShowElementsforV18
            + "             });"
            + "          });"
            + "         $(document).ready(function () {"
            + "             $(document).on('click', '#linkToV2_0', function() {"
            +                   ShowElementsforV20
            + "             });"
            + "          });"
            + "         $(document).ready(function () {"
            + "             $(document).on('click', '#linkToV2_1', function() {"
            +                   ShowElementsforV21
            + "             });"
            + "          });"
            + "         $(document).ready(function () {"
            + "             $(document).on('click', '#linkToV2_2', function() {"
            +                   ShowElementsforV22
            + "             });"
            + "          });"
            + "         $(document).ready(function () {"
            + "             $(document).on('click', '#linkToV2_3', function() {"
            +                   ShowElementsforV23
            + "             });"
            + "          });"
            + "      </script> "

            + "      <meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />"
            + "        <style>"
            + "          table, div{"
            + "            font-size:13px;"
            + "            word-break: break-all;"
            + "          }"
            + "          table th{"
            + "            background-color: #20b2aa;"
            + "            word-break: break-all;"
            + "          }"
            + "          button { background-color: #eee; border-width: 2px; padding: 5px; font-size: 14px; cursor: pointer; display: inline-block; } /* On mouse-over */ button:hover {background: #20b2aa;}  "
            + "          #summaryTable th:hover{background: #eee;}"
            + "#summaryTable th .spnTooltip { z-index:10;display:none; padding:5px 5px; margin-top:15px;}"
            + "#summaryTable th:hover .spnTooltip{ display:inline; position:absolute; color:#eee; border:1px solid #DCA; background:#464646;} .callout {z-index:20;position:absolute;top:30px;border:0;left:-12px;}"

            + "          div.ex1 { "
            + "            height: 400px; "
            + "            overflow: auto; "
            + "          }"
            + "          div.ex2 { "
            + "            height: 800px; "
            + "            overflow: auto; "
            + "            text-align: center; "
            + "            line-height:800px; "
            + "          }"
            + "          span { "
            + "            display: inline-block; "
            + "            vertical-align: middle; "
            + "            line-height: normal; "
            + "          }"
            + "          .report_header{"
            + "            font-size:15px;"
            + "          }"
            + "           pre { "
            + "             white-space: pre-wrap; "
            + "             word-wrap: break-word;"
            + "          }"
            + "          .graph {"
            + "            width: 700px;"
            + "            padding-left: 5px;"
            + "          }"
            + "          .passed, .failed, .pending {"
            + "            height: 25px;"
            + "          }"
            + "          .passed .label,.failed .label,.pending .label{"
            + "            padding-top: 3px;"
            + "            float: left;"
            + "            width: 175px;"
            + "          }"
            + "          .passed .bar, .failed .bar, .pending .bar {"
            + "            font: 10px sans-serif;"
            + "            text-align: right;"
            + "            float: left;"
            + "          }"
            + "          .passed .bar{"
            + "            background-color: #9ACD32;"
            + "          }"
            + "          .failed .bar{"
            + "            background-color: #FF6347;"
            + "          }"
            + "          .pending .bar{"
            + "            background-color: #F0E68C;"
            + "          }"
            + "          .code{"
            + "            font-size:12px;"
            + "            vertical-align:top;"
            + "          }"
            + "          .failed_text{"
            + "            color: red;"
            + "          }"
            + "          hr{"
            + "                display: block;"
            + "                margin-top: 0.5em;"
            + "                margin-bottom: 0.5em;"
            + "                margin-left: auto;"
            + "                margin-right: auto;"
            + "                border-style: inset;"
            + "                border-width: 1px;"
            + "            }"
            //Start Style for "go up button"
            + "          #scroll {"
            + "                 position:fixed;"
            + "                 right:10px;"
            + "                 bottom:10px;"
            + "                 cursor:pointer;"
            + "                 width:50px;"
            + "                 height:50px;"
            + "                 background-color:#9ACD32;"
            + "                 text-indent:-9999px;"
            + "                 display:none;"
            + "                 -webkit-border-radius:60px;"
            + "                 -moz-border-radius:60px;"
            + "                 border-radius:60px"
            + "                 }"
            + "          #scroll span {"
            + "                 position:absolute;"
            + "                 top:50%;"
            + "                 left:50%;"
            + "                 margin-left:-8px;"
            + "                 margin-top:-12px;"
            + "                 height:0;"
            + "                 width:0;"
            + "                 border:8px solid transparent;"
            + "                 border-bottom-color:#ffffff;"
            + "                 }"

            + "           #scroll:hover {"
            + "                 background-color:#e74c3c;"
            + "                 opacity:1;filter:\"alpha(opacity=100)\";"
            + "                 -ms-filter:\"alpha(opacity=100)\";"
            + "                 }"
            //End Style for "go up button"
            //Start Style for "go to v1.0 test cases"
            + "          #downlinkToV1_0 {"
            + "                 position:fixed;"
            + "                 right:425px;"
            + "                 bottom:10px;"
            + "                 cursor:pointer;"
            + "                 width:50px;"
            + "                 height:50px;"
            + "                 background-color:#9ACD32;"
            + "                 text-align: center;"
            + "                 display: grid;"
            + "                 align-items: center;"
            + "                 -webkit-border-radius:60px;"
            + "                 -moz-border-radius:60px;"
            + "                 border-radius:60px"
            + "                 }"
            + "           #downlinkToV1_0:hover {"
            + "                 background-color:#e74c3c;"
            + "                 opacity:1;filter:\"alpha(opacity=100)\";"
            + "                 -ms-filter:\"alpha(opacity=100)\";"
            + "                 }"
            //End Style for "go to v1.0 test cases"
            //Start Style for "go to v1.5 test cases"
            + "          #downlinkToV1_5 {"
            + "                 position:fixed;"
            + "                 right:495px;"
            + "                 bottom:10px;"
            + "                 cursor:pointer;"
            + "                 width:50px;"
            + "                 height:50px;"
            + "                 background-color:#9ACD32;"
            + "                 text-align: center;"
            + "                 display: grid;"
            + "                 align-items: center;"
            + "                 -webkit-border-radius:60px;"
            + "                 -moz-border-radius:60px;"
            + "                 border-radius:60px"
            + "                 }"
            + "           #downlinkToV1_5:hover {"
            + "                 background-color:#e74c3c;"
            + "                 opacity:1;filter:\"alpha(opacity=100)\";"
            + "                 -ms-filter:\"alpha(opacity=100)\";"
            + "                 }"
            //End Style for "go to v1.5 test cases"
            //Start Style for "go to v1.6 test cases"
            + "          #downlinkToV1_6 {"
            + "                 position:fixed;"
            + "                 right:565px;"
            + "                 bottom:10px;"
            + "                 cursor:pointer;"
            + "                 width:50px;"
            + "                 height:50px;"
            + "                 background-color:#9ACD32;"
            + "                 text-align: center;"
            + "                 display: grid;"
            + "                 align-items: center;"
            + "                 -webkit-border-radius:60px;"
            + "                 -moz-border-radius:60px;"
            + "                 border-radius:60px"
            + "                 }"
            + "           #downlinkToV1_6:hover {"
            + "                 background-color:#e74c3c;"
            + "                 opacity:1;filter:\"alpha(opacity=100)\";"
            + "                 -ms-filter:\"alpha(opacity=100)\";"
            + "                 }"
            //End Style for "go to v1.6 test cases"
            //Start Style for "go to v1.7 test cases"
            + "          #downlinkToV1_7 {"
            + "                 position:fixed;"
            + "                 right:635px;"
            + "                 bottom:10px;"
            + "                 cursor:pointer;"
            + "                 width:50px;"
            + "                 height:50px;"
            + "                 background-color:#9ACD32;"
            + "                 text-align: center;"
            + "                 display: grid;"
            + "                 align-items: center;"
            + "                 -webkit-border-radius:60px;"
            + "                 -moz-border-radius:60px;"
            + "                 border-radius:60px"
            + "                 }"

            + "           #downlinkToV1_7:hover {"
            + "                 background-color:#e74c3c;"
            + "                 opacity:1;filter:\"alpha(opacity=100)\";"
            + "                 -ms-filter:\"alpha(opacity=100)\";"
            + "                 }"
            //End Style for "go to v1.7 test cases"
            //Start Style for "go to v1.8 test cases"
            + "          #downlinkToV1_8 {"
            + "                 position:fixed;"
            + "                 right:705px;"
            + "                 bottom:10px;"
            + "                 cursor:pointer;"
            + "                 width:50px;"
            + "                 height:50px;"
            + "                 background-color:#9ACD32;"
            + "                 text-align: center;"
            + "                 display: grid;"
            + "                 align-items: center;"
            + "                 -webkit-border-radius:60px;"
            + "                 -moz-border-radius:60px;"
            + "                 border-radius:60px"
            + "                 }"

            + "           #downlinkToV1_8:hover {"
            + "                 background-color:#e74c3c;"
            + "                 opacity:1;filter:\"alpha(opacity=100)\";"
            + "                 -ms-filter:\"alpha(opacity=100)\";"
            + "                 }"
            //End Style for "go to v1.8 test cases"
            //Start Style for "go to v2.0 test cases"
            + "          #downlinkToV2_0 {"
            + "                 position:fixed;"
            + "                 right:775px;"
            + "                 bottom:10px;"
            + "                 cursor:pointer;"
            + "                 width:50px;"
            + "                 height:50px;"
            + "                 background-color:#9ACD32;"
            + "                 text-align: center;"
            + "                 display: grid;"
            + "                 align-items: center;"
            + "                 -webkit-border-radius:60px;"
            + "                 -moz-border-radius:60px;"
            + "                 border-radius:60px"
            + "                 }"

            + "           #downlinkToV2_0:hover {"
            + "                 background-color:#e74c3c;"
            + "                 opacity:1;filter:\"alpha(opacity=100)\";"
            + "                 -ms-filter:\"alpha(opacity=100)\";"
            + "                 }"
            //End Style for "go to v2.0 test cases"
            //Start Style for "go to v2.1 test cases"
            + "          #downlinkToV2_1 {"
            + "                 position:fixed;"
            + "                 right:845px;"
            + "                 bottom:10px;"
            + "                 cursor:pointer;"
            + "                 width:50px;"
            + "                 height:50px;"
            + "                 background-color:#9ACD32;"
            + "                 text-align: center;"
            + "                 display: grid;"
            + "                 align-items: center;"
            + "                 -webkit-border-radius:60px;"
            + "                 -moz-border-radius:60px;"
            + "                 border-radius:60px"
            + "                 }"

            + "           #downlinkToV2_1:hover {"
            + "                 background-color:#e74c3c;"
            + "                 opacity:1;filter:\"alpha(opacity=100)\";"
            + "                 -ms-filter:\"alpha(opacity=100)\";"
            + "                 }"
            //End Style for "go to v2.1 test cases"
            //Start Style for "go to v2.2 test cases"
            + "          #downlinkToV2_2 {"
            + "                 position:fixed;"
            + "                 right:915px;"
            + "                 bottom:10px;"
            + "                 cursor:pointer;"
            + "                 width:50px;"
            + "                 height:50px;"
            + "                 background-color:#9ACD32;"
            + "                 text-align: center;"
            + "                 display: grid;"
            + "                 align-items: center;"
            + "                 -webkit-border-radius:60px;"
            + "                 -moz-border-radius:60px;"
            + "                 border-radius:60px"
            + "                 }"

            + "           #downlinkToV2_2:hover {"
            + "                 background-color:#e74c3c;"
            + "                 opacity:1;filter:\"alpha(opacity=100)\";"
            + "                 -ms-filter:\"alpha(opacity=100)\";"
            + "                 }"
            //End Style for "go to v2.2 test cases"
            //Start Style for "go to v2.3 test cases"
            + "          #downlinkToV2_3 {"
            + "                 position:fixed;"
            + "                 right:985px;"
            + "                 bottom:10px;"
            + "                 cursor:pointer;"
            + "                 width:50px;"
            + "                 height:50px;"
            + "                 background-color:#9ACD32;"
            + "                 text-align: center;"
            + "                 display: grid;"
            + "                 align-items: center;"
            + "                 -webkit-border-radius:60px;"
            + "                 -moz-border-radius:60px;"
            + "                 border-radius:60px"
            + "                 }"

            + "           #downlinkToV2_3:hover {"
            + "                 background-color:#e74c3c;"
            + "                 opacity:1;filter:\"alpha(opacity=100)\";"
            + "                 -ms-filter:\"alpha(opacity=100)\";"
            + "                 }"
            //End Style for "go to v2.3 test cases"
            //Start Style for "go to summary section"
            + "          #downlinkToSummary {"
            + "                 position:fixed;"
            + "                 right:75px;"
            + "                 bottom:10px;"
            + "                 cursor:pointer;"
            + "                 width:150px;"
            + "                 height:50px;"
            + "                 background-color:#9ACD32;"
            + "                 text-align: center;"
            + "                 display: grid;"
            + "                 align-items: center;"
            + "                 -webkit-border-radius:60px;"
            + "                 -moz-border-radius:60px;"
            + "                 border-radius:60px"
            + "                 }"

            + "           #downlinkToSummary:hover {"
            + "                 background-color:#e74c3c;"
            + "                 opacity:1;filter:\"alpha(opacity=100)\";"
            + "                 -ms-filter:\"alpha(opacity=100)\";"
            + "                 }"
            //End Style for "go to summary section"
            //Start Style for "Upper Button to go Miscellaneous test cases"
            + "          #downlinkToNO_VERSION {"
            + "                 position:fixed;"
            + "                 right:235px;"
            + "                 bottom:10px;"
            + "                 cursor:pointer;"
            + "                 width:175px;"
            + "                 height:50px;"
            + "                 background-color:#9ACD32;"
            + "                 text-align: center;"
            + "                 display: grid;"
            + "                 align-items: center;"
            + "                 -webkit-border-radius:60px;"
            + "                 -moz-border-radius:60px;"
            + "                 border-radius:60px"
            + "                 }"
            + "           #downlinkToNO_VERSION:hover {"
            + "                 background-color:#e74c3c;"
            + "                 opacity:1;filter:\"alpha(opacity=100)\";"
            + "                 -ms-filter:\"alpha(opacity=100)\";"
            + "                 }"
            //End Style for "Upper Button to go Miscellaneous test cases"
            //Start Style for "Upper Button to go to v1.0 test cases"
            + "          #linkToV1_0 {"
            + "                 cursor:pointer;"
            + "                 width:50px;"
            + "                 height:50px;"
            + "                 background-color:#9ACD32;"
            + "                 text-align: center;"
            + "                 display: grid;"
            + "                 align-items: center;"
            + "                 -webkit-border-radius:60px;"
            + "                 -moz-border-radius:60px;"
            + "                 border-radius:60px"
            + "                 }"
            + "           #linkToV1_0:hover {"
            + "                 background-color:#e74c3c;"
            + "                 opacity:1;filter:\"alpha(opacity=100)\";"
            + "                 -ms-filter:\"alpha(opacity=100)\";"
            + "                 }"
            //End Style for "go to v1.0 test cases"
            //Start Style for "Upper Button to go to v1.5 test cases"
            + "          #linkToV1_5 {"
            + "                 cursor:pointer;"
            + "                 width:50px;"
            + "                 height:50px;"
            + "                 background-color:#9ACD32;"
            + "                 text-align: center;"
            + "                 display: grid;"
            + "                 align-items: center;"
            + "                 -webkit-border-radius:60px;"
            + "                 -moz-border-radius:60px;"
            + "                 border-radius:60px"
            + "                 }"
            + "           #linkToV1_5:hover {"
            + "                 background-color:#e74c3c;"
            + "                 opacity:1;filter:\"alpha(opacity=100)\";"
            + "                 -ms-filter:\"alpha(opacity=100)\";"
            + "                 }"
            //End Style for "go to v1.5 test cases"
            //Start Style for "Upper Button to go to v1.6 test cases"
            + "          #linkToV1_6 {"
            + "                 cursor:pointer;"
            + "                 width:50px;"
            + "                 height:50px;"
            + "                 background-color:#9ACD32;"
            + "                 text-align: center;"
            + "                 display: grid;"
            + "                 align-items: center;"
            + "                 -webkit-border-radius:60px;"
            + "                 -moz-border-radius:60px;"
            + "                 border-radius:60px"
            + "                 }"
            + "           #linkToV1_6:hover {"
            + "                 background-color:#e74c3c;"
            + "                 opacity:1;filter:\"alpha(opacity=100)\";"
            + "                 -ms-filter:\"alpha(opacity=100)\";"
            + "                 }"
            //End Style for "go to v1.6 test cases"
            //Start Style for "Upper Button to go to v1.7 test cases"
            + "          #linkToV1_7 {"
            + "                 cursor:pointer;"
            + "                 width:50px;"
            + "                 height:50px;"
            + "                 background-color:#9ACD32;"
            + "                 text-align: center;"
            + "                 display: grid;"
            + "                 align-items: center;"
            + "                 -webkit-border-radius:60px;"
            + "                 -moz-border-radius:60px;"
            + "                 border-radius:60px"
            + "                 }"
            + "           #linkToV1_7:hover {"
            + "                 background-color:#e74c3c;"
            + "                 opacity:1;filter:\"alpha(opacity=100)\";"
            + "                 -ms-filter:\"alpha(opacity=100)\";"
            + "                 }"
            //End Style for "go to v1.7 test cases"
            //Start Style for "Upper Button to go to v1.8 test cases"
            + "          #linkToV1_8 {"
            + "                 cursor:pointer;"
            + "                 width:50px;"
            + "                 height:50px;"
            + "                 background-color:#9ACD32;"
            + "                 text-align: center;"
            + "                 display: grid;"
            + "                 align-items: center;"
            + "                 -webkit-border-radius:60px;"
            + "                 -moz-border-radius:60px;"
            + "                 border-radius:60px"
            + "                 }"
            + "           #linkToV1_8:hover {"
            + "                 background-color:#e74c3c;"
            + "                 opacity:1;filter:\"alpha(opacity=100)\";"
            + "                 -ms-filter:\"alpha(opacity=100)\";"
            + "                 }"
            //End Style for "go to v1.8 test cases"
            //Start Style for "Upper Button to go to v2.0 test cases"
            + "          #linkToV2_0 {"
            + "                 cursor:pointer;"
            + "                 width:50px;"
            + "                 height:50px;"
            + "                 background-color:#9ACD32;"
            + "                 text-align: center;"
            + "                 display: grid;"
            + "                 align-items: center;"
            + "                 -webkit-border-radius:60px;"
            + "                 -moz-border-radius:60px;"
            + "                 border-radius:60px"
            + "                 }"
            + "           #linkToV2_0:hover {"
            + "                 background-color:#e74c3c;"
            + "                 opacity:1;filter:\"alpha(opacity=100)\";"
            + "                 -ms-filter:\"alpha(opacity=100)\";"
            + "                 }"
            //End Style for "go to v2.0 test cases"
            //Start Style for "Upper Button to go to v2.1 test cases"
            + "          #linkToV2_1 {"
            + "                 cursor:pointer;"
            + "                 width:50px;"
            + "                 height:50px;"
            + "                 background-color:#9ACD32;"
            + "                 text-align: center;"
            + "                 display: grid;"
            + "                 align-items: center;"
            + "                 -webkit-border-radius:60px;"
            + "                 -moz-border-radius:60px;"
            + "                 border-radius:60px"
            + "                 }"
            + "           #linkToV2_1:hover {"
            + "                 background-color:#e74c3c;"
            + "                 opacity:1;filter:\"alpha(opacity=100)\";"
            + "                 -ms-filter:\"alpha(opacity=100)\";"
            + "                 }"
            //End Style for "go to v2.1 test cases"
            //Start Style for "Upper Button to go to v2.2 test cases"
            + "          #linkToV2_2 {"
            + "                 cursor:pointer;"
            + "                 width:50px;"
            + "                 height:50px;"
            + "                 background-color:#9ACD32;"
            + "                 text-align: center;"
            + "                 display: grid;"
            + "                 align-items: center;"
            + "                 -webkit-border-radius:60px;"
            + "                 -moz-border-radius:60px;"
            + "                 border-radius:60px"
            + "                 }"
            + "           #linkToV2_2:hover {"
            + "                 background-color:#e74c3c;"
            + "                 opacity:1;filter:\"alpha(opacity=100)\";"
            + "                 -ms-filter:\"alpha(opacity=100)\";"
            + "                 }"
            //End Style for "go to v2.2 test cases"
            //Start Style for "Upper Button to go to v2.3 test cases"
            + "          #linkToV2_3 {"
            + "                 cursor:pointer;"
            + "                 width:50px;"
            + "                 height:50px;"
            + "                 background-color:#9ACD32;"
            + "                 text-align: center;"
            + "                 display: grid;"
            + "                 align-items: center;"
            + "                 -webkit-border-radius:60px;"
            + "                 -moz-border-radius:60px;"
            + "                 border-radius:60px"
            + "                 }"
            + "           #linkToV2_3:hover {"
            + "                 background-color:#e74c3c;"
            + "                 opacity:1;filter:\"alpha(opacity=100)\";"
            + "                 -ms-filter:\"alpha(opacity=100)\";"
            + "                 }"
            //End Style for "go to v2.3 test cases"
            //Start Style for "Upper Button to go to Summary page"
            + "          #linkToSummary {"
            + "                 cursor:pointer;"
            + "                 width:150px;"
            + "                 height:50px;"
            + "                 background-color:#9ACD32;"
            + "                 text-align: center;"
            + "                 display: grid;"
            + "                 align-items: center;"
            + "                 -webkit-border-radius:60px;"
            + "                 -moz-border-radius:60px;"
            + "                 border-radius:60px"
            + "                 }"
            + "           #linkToSummary:hover {"
            + "                 background-color:#e74c3c;"
            + "                 opacity:1;filter:\"alpha(opacity=100)\";"
            + "                 -ms-filter:\"alpha(opacity=100)\";"
            + "                 }"
            //End Style for "Upper Button to go to Summary page"
            //Start Style for "Upper Button to go Miscellaneous test cases"
            + "          #linkToNO_VERSION {"
            + "                 cursor:pointer;"
            + "                 width:200px;"
            + "                 height:50px;"
            + "                 background-color:#9ACD32;"
            + "                 text-align: center;"
            + "                 display: grid;"
            + "                 align-items: center;"
            + "                 -webkit-border-radius:60px;"
            + "                 -moz-border-radius:60px;"
            + "                 border-radius:60px"
            + "                 }"
            + "           #linkToNO_VERSION:hover {"
            + "                 background-color:#e74c3c;"
            + "                 opacity:1;filter:\"alpha(opacity=100)\";"
            + "                 -ms-filter:\"alpha(opacity=100)\";"
            + "                 }"
            //End Style for "Upper Button to go Miscellaneous test cases"
            + "        </style>"
            + "      </head>"
            + "      <body>"
            //Buttons in the down section NOT THE ONES IN THE UPPER SIDE
            + "        <a href=\"#\" id=\"scroll\" title=\"Scroll to Top\" style=\"display: none;\">Top<span></span></a>"
            + "        <a href=\"#\" id=\"downlinkToSummary\" title=\"Show summary\" style=\"display: none;\"><span style=\"color:white\">Show summary</span></a>"
            + "        <a href=\"#\" id=\"downlinkToNO_VERSION\" title=\"NO VERSION\" style=\"display: none;\"><span style=\"color:white\">View miscelaneus</span></a>"
            + "        <a href=\"#\" id=\"downlinkToV1_0\" title=\"1.0\" style=\"display: none;\"><span style=\"color:white\">1.0</span></a>"
            + "        <a href=\"#\" id=\"downlinkToV1_5\" title=\"1.5\" style=\"display: none;\"><span style=\"color:white\">1.5</span></a>"
            + "        <a href=\"#\" id=\"downlinkToV1_6\" title=\"1.6\" style=\"display: none;\"><span style=\"color:white\">1.6</span></a>"
            + "        <a href=\"#\" id=\"downlinkToV1_7\" title=\"1.7\" style=\"display: none;\"><span style=\"color:white\">1.7</span></a>"
            + "        <a href=\"#\" id=\"downlinkToV1_8\" title=\"1.8\" style=\"display: none;\"><span style=\"color:white\">1.8</span></a>"
            + "        <a href=\"#\" id=\"downlinkToV2_0\" title=\"2.0\" style=\"display: none;\"><span style=\"color:white\">2.0</span></a>"
            + "        <a href=\"#\" id=\"downlinkToV2_1\" title=\"2.1\" style=\"display: none;\"><span style=\"color:white\">2.1</span></a>"
            + "        <a href=\"#\" id=\"downlinkToV2_2\" title=\"2.2\" style=\"display: none;\"><span style=\"color:white\">2.2</span></a>"
            + "        <a href=\"#\" id=\"downlinkToV2_3\" title=\"2.3\" style=\"display: none;\"><span style=\"color:white\">2.3</span></a>"

            + "        <div class='row text-center' style='background-color: #20b2aa;'>"
            + "          <div class='col-12 border border-white'>"
            + "            <img src='data:image/png;base64,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' alt='Logo' background-color: #000000;> </th>"
            + "          </div>"
            + "         </div>"
            + "     <div class=\"row \" style='background-color: #20b2aa;'>\n"
            + "        <div class='col-4 text-center border border-white'>\n"
            + "            <h6> ZIOMD APIs functional automation results</h6>\n"
            + "        </div>\n"
            + "       <div class='col-4 text-center border border-white'>\n"
            + "           <h6> Endpoint :" + config.getEnv()  +" </h6>\n"
            + "       </div>\n"
            + "        <div class='col-1 text-center border border-white'>\n"
            + "            <h6><a href='https://git.irhythmtech.org/projects/TA/repos/ziomd-web-service-automation/'><b>Repository</b></a></h6>\n"
            + "        </div>\n"
            + "        <div class='col-3 text-center border border-white'>\n"
            + "            <h6>"+ ZonedDateTime.now().toInstant() +"</h6>\n"
            + "        </div>\n"
            + "       </div>"
            + "       <div class='row text-center' style='background-color: #20b2aa;'>"
            + "          <div class='col-12'>"
            + "           <h6> Execution Summary </h6>"
            + "       </div>"
            + "    </div>              "
            + "    <div class=\"row \" style='background-color: #20b2aa;'>\n"
            + "        <div class='col-3 text-center border border-white'>\n"
            + "            <h6>Web Service Version</h6>\n"
            + "        </div>\n"
            + "        <div class='col-2 text-center border border-white'>\n"
            + "            <h6>Build</h6>\n"
            + "        </div>\n"
            + "        <div class='col-2 text-center border border-white'>\n"
            + "            <h6>Total Cases</h6>\n"
            + "        </div>\n"
            + "        <div class='col-2 text-center border border-white'>\n"
            + "            <h6>Pass Rate </h6>\n"
            + "        </div>\n"
            + "        <div class='col-3 text-center border border-white'>\n"
            + "            <h6>Failure Rate</h6>\n"
            + "        </div>\n"
            + "    </div>"
            + "    <div class=\"row \">\n"
            + "        <div class='col-3 text-center border border-white'>\n"
            + "            <h6>" + config.getWebServiceVersion() + "</h6>\n"
            + "        </div>\n"
            + "        <div class='col-2 text-center border border-white'>\n"
            + "            <h6>" + config.getBuildId() + "</h6>\n"
            + "        </div>\n"
            + "        <div class='col-2 text-center border border-white'>\n"
            + "            <h6> " + String.valueOf(lstTCNamesInScope.size()) +"</h6>\n"
            + "        </div>\n"
            + "        <div class='col-2 text-center border border-white'>\n"
            + "            <h6>"+ String.format(PERCENTAGE_DECIMALS, passRate)+" % </h6>\n"
            + "        </div>\n"
            + "        <div class='col-3 text-center border border-white'>\n"
            + "            <h6> "+ String.format(PERCENTAGE_DECIMALS, failRate) +" % </h6>\n"
            + "        </div>\n"
            + "    </div>"
            + "        <div class='graph'>"
            + "          <div class='failed'>"
            + "            <div class='label'><b>Failed</b></div>"
            + "            <div class='bar' style='width: " + String.valueOf(failedChartSize) + "px; height: 25px;'>" + failed + "</div>"
            + "          </div>"
            + "          <div class='passed'>"
            + "            <div class='label'><b>Passed</b></div>"
            + "            <div class='bar' style='width: " + String.valueOf(passedChartSize) + "px; height: 25px;'>" + passed + "</div>"
            + "          </div>"
            + "        </div>"

            //show ALL page buttons
            + "        <div class='btn-group-horizontal' style=\"margin: 15px 0px 15px 0px;\">"
            + "        <a href='#' id=\"linkToSummary\" title=\"Show summary\" style=\"display: inline-block;\"><span style=\"color:white;padding-top: 15px;\">Show summary</span></a>"
            + "        <a href='#' id=\"linkToNO_VERSION\" title=\"No Verison\" style=\"display: inline-block;\"><span style=\"color:white;padding-top: 15px;\">View miscellaneous test cases</span></a>"
            + "        <a href='#' id=\"linkToV1_0\" title=\"1.0\" style=\"display: inline-block;\"><span style=\"color:white;padding-top: 15px;\">1.0</span></a>"
            + "        <a href='#' id=\"linkToV1_5\" title=\"1.5\" style=\"display: inline-block;\"><span style=\"color:white;padding-top: 15px;\">1.5</span></a>"
            + "        <a href='#' id=\"linkToV1_6\" title=\"1.6\" style=\"display: inline-block;\"><span style=\"color:white;padding-top: 15px;\">1.6</span></a>"
            + "        <a href='#' id=\"linkToV1_7\" title=\"1.7\" style=\"display: inline-block;\"><span style=\"color:white;padding-top: 15px;\">1.7</span></a>"
            + "        <a href='#' id=\"linkToV1_8\" title=\"1.8\" style=\"display: inline-block;\"><span style=\"color:white;padding-top: 15px;\">1.8</span></a>"
            + "        <a href='#' id=\"linkToV2_0\" title=\"2.0\" style=\"display: inline-block;\"><span style=\"color:white;padding-top: 15px;\">2.0</span></a>"
            + "        <a href='#' id=\"linkToV2_1\" title=\"2.1\" style=\"display: inline-block;\"><span style=\"color:white;padding-top: 15px;\">2.1</span></a>"
            + "        <a href='#' id=\"linkToV2_2\" title=\"2.2\" style=\"display: inline-block;\"><span style=\"color:white;padding-top: 15px;\">2.2</span></a>"
            + "        <a href='#' id=\"linkToV2_3\" title=\"2.3\" style=\"display: inline-block;\"><span style=\"color:white;padding-top: 15px;\">2.3</span></a>"
            + "        </div>";


    HashMap<String, JSONArray> versionList = new HashMap<>();
    versionList.put(V1_0.name(), V1_0_Results);
    versionList.put(V1_5.name(), V1_5_Results);
    versionList.put(V1_6.name(), V1_6_Results);
    versionList.put(V1_7.name(), V1_7_Results);
    versionList.put(V1_8.name(), V1_8_Results);
    versionList.put(V2_0.name(), V2_0_Results);
    versionList.put(V2_1.name(), V2_1_Results);
    versionList.put(V2_2.name(), V2_2_Results);
    versionList.put(V2_3.name(), V2_3_Results);
    versionList.put(NO_VERSION.name(), versionless_Results);


    for(Map.Entry<String, JSONArray> entry : versionList.entrySet()) {
      JSONArray version = entry.getValue();
      JSONArray stepsObjects = SelectVersionJsonArray(entry.getKey());
      stepNumber = 0;
      //show headers for this version
      html += "        <table cellpadding='5' border='1' style='display:none' id='" +  entry.getKey() +"'>"
              + "          <thead>"
              + "            <tr bgcolor='#9fc5e8'>"
              + "              <th style='width: 8%'>Test Case</th>"
              + "              <th style='width: 12%'>Expected assertions</th>"
              + "              <th style='width: 30%'>Steps</th>"
              + "              <th style='width: 30%'>Raw response</th>"
              + "              <th style='width: 5%'>Result</th>"
              + "              <th style='width: 15%'>Description</th>"
              + "            </tr>"
              + "          </thead>"
              + "          <tbody>";
      //show actual results
      for (Object resultObject : version) {
        JSONObject result = (JSONObject) resultObject;
        String bgColor = result.get(RESULT_JSONNODE).equals(FAIL_RESULT) ? "FF6347" : "9ACD32"; //sets background color
        String description = "		<td align = 'left' style = 'width:10%'>" + result.get(DESCRIPTION_VALUE) + "</td>"; //description of test case
        html += "<tr>"
                + "				<td align = 'center' bgcolor=" + bgColor + " style = 'width:15%'>" + result.get(TEST_CASE_JSONNODE) + "</td>" //populate test case name column, with pass/fail color
                + "				<td >" + "<div class=\"ex2\"><span>" + result.get(ERROR_JSONNODE) + "</td></span></div>"
                + buildNestedTable(stepsObjects)  //populate the request/response columns
                + "<td align = 'center' style = 'width:5%'>" + result.get(RESULT_JSONNODE) + "</td>"
                + description
                + "</tr>";
      }
    }

    //show summary by component (left side of page)
    html += "</tbody></table>"
            + " <div id=\"Summary\" style='display:block' align='center'>"
            + "        <table cellpadding='5' border='1' align='left' id='summaryTable' class='tablesorter' style=\"width:40%; float:left; margin:5%; margin-top: 2%; border-collapse: collapse; cursor: pointer;\n\">"
            + "          <thead>"
            + "            <tr bgcolor='#9fc5e8'>"
            + "              <th style='width: 25%; class=\"spnTooltip\"'>Component<span class=\"spnTooltip\"> <strong>Sort Component column</strong></th>"
            + "              <th style='width: 35%; class=\"spnTooltip\"'>Test Case   <span class=\"spnTooltip\">Sort TC column</span></th>"
            + "              <th style='width: 30%; class=\"spnTooltip\"'>Web Service Version<span class=\"spnTooltip\"> <strong>Sort Version column</strong></th>"
            + "              <th style='width: 10%; class=\"spnTooltip\"'>Result   <span class=\"spnTooltip\">Sort Result column</span></th>"
            + "            </tr>"
            + "          </thead>"
            + "          <tbody>"
    ;
    populateExecutedTCAndBuildSummaryTable();
    ArrayList testCasesByComponent = new ArrayList();
    for (String compoment: lstComponentsInScope) {
      testCasesByComponent.add(GetTestCasesByComponent(compoment));
    }
    //show statistics of test cases (right side of page)

    html += "</tbody></table>"
            + "ALL TEST CASES" //label for what is being displayed in statistics table
            + buildStatisticsTable(testCasesByComponent);

    //closer
    html += "</div>"
            + "</body></html>";
    return html;
  }

  private String buildNestedTable(JSONArray stepsObjects) {

    String html = "<td align = 'left' colspan = '2'><table style = width:100% border-collapse:collapse border=1 bordercolor='#90ccc8'>";
    while (stepNumber <= stepsObjects.size()) {
      JSONObject step = (JSONObject) stepsObjects.get(stepNumber);
      if (!step.containsKey("END")) {
        if (step.get(STEP_NAME_KEY).equals(STEP_SPLUNK_VALUE)){
          html += "<tr border=1px>"
                  + "         <td width='50%'>"+"</br> <div class=\"ex1\"> STEP: " + step.get(STEP_NAME_KEY) + "</br></br>Input Parameters Request.-</br></br>" + EARLIEST_TIME_KEY + ": \"" + step.get(EARLIEST_TIME_KEY) + "\"</br>" + LATEST_TIME_KEY + ": \"" + step.get(LATEST_TIME_KEY) + "\"</br>Search value: \"" + step.get("search") + "\"</pre></div></td>"
                  + "         <td width='50%'>"+"</br> <div class=\"ex1\"> STEP: " + step.get(STEP_NAME_KEY) + "</br>" + "</br>Rows result:</br>" + step.get(BODY_RESPONSE_KEY) + "</pre></div></td>"
                  + "</tr>";

        }else if(step.get(STEP_NAME_KEY).equals(STEP_DATABASE_SERVICE)){
          html += "<tr border=1px>"
                  + "         <td width='50%'>"+"</br> <div class=\"ex1\"> STEP: " + step.get(STEP_DATABASE_QUERY) + "</br></br>DataBase query parameters: </br>" + "</br>" + DATABASE_NAME_DESCRIPTION + ": \"" + step.get(DB_SERVERNAME) + "\"</br></br>Search value: </br>" + TABLE_PARAMETER_DESCRIPTION + ": \"" + step.get(TABLE_PARAMETER) + "\"" + "</br>" +  COLUMN_PARAMETER_DESCRIPTION  + ": \"" + step.get(COLUMN_PARAMETER) + "\"</br>" + SEARCH_PARAMETER_DESCRIPTION + ": \"" + step.get(SEARCH_PARAMETER) +"\"" + "</br></br>"+ QUERY_DESCRIPTION + ": \"" + step.get(DB_QUERY) + "\"</pre></div></td>" + "         " +
                  "           <td width='50%'>"+"</br> <div class=\"ex1\"> STEP: " + step.get(STEP_DATABASE_RESULT) + "</br>" + "</br>Columns results from query</br></br>" + step.get(BODY_RESPONSE_KEY) + "</pre></div></td>" + "</tr>";
        }else{
          html += "<tr border=1px>"
                  + "         <td width='50%'>"+"</br> <div class=\"ex1\">STEP: " + step.get(STEP_NAME_KEY) + "</br>HEADERS REQUEST" + step.get(HEADER_REQUEST_KEY) + " </br></br>REQUEST</br></br>"+ step.get(BODY_REQUEST_KEY) +"</div></td>"
                  + "         <td width='50%'>"+"</br> <div class=\"ex1\"> STEP: " + step.get(STEP_NAME_KEY) + "</br></br>" + "</br>HEADERS RESPONSE</br></br>" + step.get(HEADER_RESPONSE_KEY) + "</br>" + "BODY RESPONSE</br></br><pre>" + step.get(BODY_RESPONSE_KEY)+"</pre></div></td>"
                  + "</tr>";
        }
        stepNumber++;
      }else{
        stepNumber++;
        html+="</table>";
        break;
      }
    }return html;}


  private String buildStatisticsTable(ArrayList executedTestCases){
    int totalPass = 0;
    int totalFail = 0;
    float passRateComponents;
    String component = "";
    String html = "     <table cellpadding='5' border='1' align='right' id='statisticsTable' class='tablesorter' style=\"width:40%; float:right; margin:5%; margin-top: 2%; border-collapse: collapse;\">"
            + "          <thead>"
            + "            <tr bgcolor='#9fc5e8'>"
            + "              <th style='width: 30%'>Component</th>"
            + "              <th style='width: 20%'>Executed tests</th>"
            + "              <th style='width: 15%'>Pass</th>"
            + "              <th style='width: 15%'>Fail</th>"
            + "              <th style='width: 15%'>Pass rate</th>"
            + "            </tr>"
            + "          </thead>"
            + "          <tbody>";
    System.out.println("EXECUTED"+ String.valueOf(executedTestCases));
    for(Object componentObject : executedTestCases){
      ArrayList componentArray = (ArrayList) componentObject;
      int totalTestCases = componentArray.size();
      for(Object testCaseObj : componentArray){
        HashMap<String, String> testCase = (HashMap<String, String>) testCaseObj;
        component = testCase.get(COMPONENT);
        totalPass += testCase.get(RESULT).equals("Pass") ? 1 : 0;
        totalFail += testCase.get(RESULT).equals("Fail") ? 1 : 0;
      }
      passRateComponents = totalTestCases !=0 ? ((float)totalPass*100) / totalTestCases : 0;
      if(totalFail + totalPass == 0 && passRateComponents == 0) {// means the component is empty and will not be added in the summary
        System.out.println("EMPTY component " + component + " totalPass " + totalPass + " totalfail " + totalFail);
        continue;
      }
      String bgColorComponent = "9ACD32";
      if(passRateComponents!=100)
        bgColorComponent = passRateComponents < 70 ? "FF6347" : "FFC300";

      html    += "            <tr>"
              + "              <td>"+component+"</td>"
              + "              <td>"+totalTestCases+"</td>"
              + "              <td>"+totalPass+"</td>"
              + "              <td>"+totalFail+"</td>"
              + "              <td bgcolor=" + bgColorComponent + ">"+String.format(PERCENTAGE_DECIMALS, passRateComponents)+"%</td>"
              + "             </tr>";
      finalTotal+=totalTestCases;
      finalPass+=totalPass;
      finalFail+=totalFail;
      totalPass=0;
      totalFail=0;
    }
    finalPassRate = ((float)finalPass *100)/finalTotal;
    String bgColorTotal = "9ACD32";
    if(finalPassRate!=100)
      bgColorTotal = finalPassRate < 70 ? "FF6347" : "FFC300";
    html    += "            <tr>"
            + "              <td><b>TOTAL</b></td>"
            + "              <td><b>"+finalTotal+"</b></td>"
            + "              <td><b>"+finalPass+"</b></td>"
            + "              <td><b>"+finalFail+"</b></td>"
            + "              <td bgcolor=" + bgColorTotal + "><b>"+String.format(PERCENTAGE_DECIMALS,finalPassRate)+"%</b></td>"
            + "             </tr>"
            +"            </tbody></table>";
    return html;
  }


  private void populateExecutedTCAndBuildSummaryTable(){
    executedTestCasesGlobal = new ArrayList();
    Map<String, String> testCaseMap = null;
    for (Object resultObject : resultObjects) {
      JSONObject result = (JSONObject) resultObject;
      String bgColor = result.get(RESULT_JSONNODE).equals(FAIL_RESULT) ? "FF6347" : "9ACD32";
      String testCaseId = (String) result.get(TEST_CASE_JSONNODE);
      String testCaseName = testCaseId.replaceAll(JAMA_REMOVE_HYPERLINK_REGEX, "").trim(); //strip hyperlink tags for the purposes of determining component
      String version = RetrievJamaVersion(result);

      String resultSt = (String) result.get(RESULT_JSONNODE);
      testCaseMap = new HashMap<>();
      testCaseMap.put(ID, testCaseName);
      testCaseMap.put(RESULT, resultSt);
      String component = "";

      for (String componentName : lstComponentsInScope) {
        if (testCaseName.matches("^(" + componentName + ID_VERIFICATION_PLAN + ").*$") ||
                testCaseName.matches(componentName + V15_REQUIREMENT_REGEX)) {
          component = CapitalizedComponentName(componentName);
          testCaseMap.put(COMPONENT, component);
        }
      }

      if(testCaseName.matches("^(" + ComponentsInScopeRegEx() + ")" + ID_VERIFICATION_PLAN + ".*$") ||
              testCaseName.matches("^(" + ComponentsInScopeRegEx() + ")" + V15_REQUIREMENT_REGEX + ".*$")){
        html +=
                "<tr>"
                        + "       <td>"+component+"</td>"
                        + "		<td align = 'center' bgcolor=" + bgColor + ">" +  result.get(TEST_CASE_JSONNODE) + "</td>"
                        + "   <td align = 'center'>" +  version + "</td>"
                        + "		<td align = 'center'>" + result.get(RESULT_JSONNODE) + "</td>"
                        + "</tr>";
        executedTestCasesGlobal.add(testCaseMap);
      }
    }
  }

  /**
   * Select the testSteps of the version
   * @param version String with the version of the test cases
   * @returns stepsObjects JSONArray with the testSteps of the version
   */
  private JSONArray SelectVersionJsonArray(String version){
    JSONArray stepsObjects;
    switch (version) {
      case ("V1_0"): {
        stepsObjects = V1_0_Steps;
        break;
      }
      case ("V1_5"): {
        stepsObjects = V1_5_Steps;
        break;
      }
      case ("V1_6"): {
        stepsObjects = V1_6_Steps;
        break;
      }
      case ("V1_7"): {
        stepsObjects = V1_7_Steps;
        break;
      }
      case ("V1_8"): {
        stepsObjects = V1_8_Steps;
        break;
      }
      case ("V2_0"): {
        stepsObjects = V2_0_Steps;
        break;
      }
      case ("V2_1"): {
        stepsObjects = V2_1_Steps;
        break;
      }
      case ("V2_2"): {
        stepsObjects = V2_2_Steps;
        break;
      }
      case ("V2_3"): {
        stepsObjects = V2_3_Steps;
        break;
      }
      default:
        stepsObjects = versionless_Steps;
    }
    return stepsObjects;
  }
}