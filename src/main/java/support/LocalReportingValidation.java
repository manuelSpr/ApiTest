package support;

import support.Configuration;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import static support.ConstantData.*;


public class LocalReportingValidation {

  private static float passRate;
  private static float failRate;
  private static int passed;
  private static int failed;

  public void saveReport(Configuration config){
    String report = EmailBody(config);
    try (PrintWriter out = new PrintWriter("local_report_validation.html")) {
      out.println(report);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }


  /**
   * Read the JSON file and build the HTML body
   * @return emailBody as string
   */
  private String EmailBody(Configuration config) {
    SysUtil utils = new SysUtil();
    JSONArray resultObjects = utils.ReadJsonFile(TESTS_RESULTS_JSON_FILE);
    passed = 0;
    failed = 0;
    for (Object resultObject : resultObjects) {
      JSONObject result = (JSONObject) resultObject;
      if (result.get(RESULT_JSONNODE).equals(PASS_RESULT)) {
        passed = passed + 1;
      }else if (result.get(RESULT_JSONNODE).equals(FAIL_RESULT)) {
        failed = failed + 1;
      }
    }
    int passedChartSize = ((passed * 500)/ resultObjects.size());
    int failedChartSize = ((failed * 500)/ resultObjects.size());
    passRate =  ((passed * 100) / resultObjects.size());
    failRate =  ((failed * 100) / resultObjects.size());
    StringBuilder html = new StringBuilder("<!DOCTYPE html>"
            + "<html>"
            + "		<head>"
            + "        <style>"
            + "          table, div{"
            + "            font-size:13px;"
            + "          }"
            + "          table th{"
            + "            background-color: #20b2aa;"
            + "          }"
            + "          .report_header{"
            + "            font-size:15px;"
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
            + "        </style>"
            + "      </head>"
            + "      <body>"
            + "        <table style = 'width:100%' border='0' cellpadding='10'>"
            + "          <tr class='report_header'>"
            + "            <th colspan='3'><b>ZIOMD APIs Validation automation results</b></th>"
            + "            <th colspan='3'><b>Endpoint: " + config.getEnv() + "</b></th>"
            + "            <th colspan='3'><a href='https://git.irhythmtech.org/projects/TA/repos/ziomd-web-service-automation/'><b>Repository</b></a></th>"
            + "          </tr>"
            + "          <tr class='report_header'>"
            + "            <th colspan='9' align = 'center'><b>Execution Summary</b></th>"
            + "          </tr>"
            + "          <tr>"
            + "            <th colspan='2.25' align = 'center'><b>Build</b></th>"
            + "            <th colspan='2.25' align = 'center'><b>Total Cases</b></th>"
            + "            <th colspan='2.25' align = 'center'><b>Pass Rate</b></th>"
            + "            <th colspan='2.25' align = 'center'><b>Failure Rate</b></th>"
            + "          </tr>"
            + "          <tr>"
            + "            <td colspan='2.25' align = 'center'>" + config.getBuildId() + "</th>"
            + "            <td colspan='2.25' align = 'center'>" + String.valueOf(resultObjects.size()) + "</td>"
            + "            <td colspan='2.25' align = 'center'>" + String.format(PERCENTAGE_DECIMALS, passRate) + "%</td>"
            + "            <td colspan='2.25' align = 'center'>" + String.format(PERCENTAGE_DECIMALS, failRate) + "%</td>"
            + "          </tr>"
            + "        </table>"
            + "        <div class='graph'>"
            + "          <div class='failed'>"
            + "            <div class='label'><b>Failed</b></div>"
            + "            <div class='bar' style='width: " + String.valueOf(failedChartSize) + "px; height: 25px;'>" + failed + "</div>"
            + "          </div>"
            + "          <div class='passed'>"
            + "            <div class='label'><b>Passed</b></div>"
            + "            <div class='bar' style='width: " + String.valueOf(passedChartSize) + "px; height: 25px; word-break:break-all;'>" + passed + "</div>"
            + "          </div>"
            + "        </div>"
            + "        <table cellpadding='5' border='1' style='width:100%; word-break:break-all;'>"
            + "          <thead>"
            + "            <tr bgcolor='#9fc5e8'>"
            + "              <th>Test Case</th>"
            + "              <th>Expected Assertions</th>"
            + "              <th>Request</th>"
            + "              <th>Raw response</th>"
            + "              <th>Result</th>"
            + "              <th>Description</th>"
            + "            </tr>"
            + "          </thead>"
            + "          <tbody>")
            ;
    for (Object resultObject : resultObjects) {
      JSONObject result = (JSONObject) resultObject;
      String bgColor = "000000";
      bgColor = result.get(RESULT_JSONNODE).equals(FAIL_RESULT) ? "FF634" : "9ACD32";
      html.append("<tr>" + "				<td align = 'center' bgcolor=").append(bgColor).append(" width='10%'>").append(result.get(TEST_CASE_JSONNODE)).append("</td>").append("				<td width='7.5%'>").append(result.get(ERROR_JSONNODE)).append("</td>").append("				<td width='35%'>").append("HEADERS REQUEST</br></br>").append(result.get(HEADER_REQUEST_KEY)).append(" </br></br> BODY REQUEST </br></br>").append(result.get(BODY_REQUEST_KEY)).append("</td>").append("       <td width='35%'>").append("HEADERS RESPONSE</br></br>").append(result.get(HEADER_RESPONSE_KEY)).append("</br>").append("BODY RESPONSE</br></br>").append(result.get(BODY_RESPONSE_KEY)).append("</td>").append("		<td align = 'center' width='2.5%'>").append(result.get(RESULT_JSONNODE)).append("</td>").append("		<td align = 'center' width='10%'>").append(result.get(DESCRIPTION_VALUE)).append("</td>").append("</tr>");
    }

    html.append("</tbody></table>" + "</body></html>");
    return html.toString();
  }

}