package testStep;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static support.SysUtil.getConfiguration;
import static support.ConstantData.*;

public class JamaSteps {
    private static final Pattern JAMA_VERSION_PATTERN = Pattern.compile(JAMA_VERSION_REGEX);

    /**
     * gets the tags from a JAMA test case, no need to login first
     * has no test context, will never be run visibly
     * @param itemNumber number associated with the test case
     * @return
     */
    public Response getJamaTags(String itemNumber){
        RequestSpecification request = new RequestSpecBuilder().setBaseUri(getConfiguration(CONFIGURATION_YAML).getJamaHost() + String.format(JAMA_ITEMS_ID_TAGS_RESOURCE, itemNumber)).build();
        Response response =   RestAssured.given().auth().preemptive().basic(JAMA_USERNAME, JAMA_PASSWORD).spec(request).get();
        System.out.println(JAMA_RESPONSE_BODY_STRING + response.getBody().asString());
        return response;
    }

    /**
     * extracts web service version tag from getJamaTags response
     * has no test context, will never be run visibly
     * @param response response of the JAMA test step to be parsed
     */
    public String parseJamaVersion(Response response){
        String body = response.getBody().asString();
        Matcher m = JAMA_VERSION_PATTERN.matcher(body);
        if (m.find()){
            return m.group(0);//assume only one version tag- there should only be one per test case
        }
        return null; //can't find anything
    }
}
