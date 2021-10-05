package support;

import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import config.Configuration;
import static support.ConstantData.*;
import static support.ConstantData.JAMA_ITEM_REGEX;
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

}
