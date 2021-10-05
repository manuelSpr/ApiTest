package support;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import static support.SysUtil.GetCSVFilesPath;

public class CSVReaders {

    private static final String DELIMITER = ";";
    private static final String CSV_EXTENSION = ".csv";
    private static final String CSV_FILES_PATH = GetCSVFilesPath();

    /**
     * Read CSV file and return a set of data(rows) that belongs to an specific data provider
     * @param csvFile             File path
     * @param dataProviderName    Data Provider to look by
     * @return                    Main Data Provider to execute the whole suite
     */
    public static List<Object[]> testCsvDataDriven(String csvFile, String dataProviderName){
        List<Object[]> testData = null;
        BufferedReader br = null;
        try{
            br = new BufferedReader(new FileReader(CSV_FILES_PATH + csvFile));
            System.out.println("Success to Read CSV");
            SysUtil sysUtils = new SysUtil();
            testData = sysUtils.GetRunnableTestData(br, dataProviderName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return testData;
    }

    /**
     *
     * @param testDataDetail
     * @return
     */
    public static String[] GetCSVInputData(String[] testDataDetail){
        int index = 0;
        for (String field:testDataDetail) {
            if(field.matches("^.*_[A-Za-z]*$")){
                String[] fieldStrings = field.split("_");
                testDataDetail[index] = GetCsvData(fieldStrings[fieldStrings.length-1], field);
            }
            index++;
        }
        return testDataDetail;
    }

    /**
     *
     * @param csvFile
     * @param nameData
     * @return
     */
    private static String GetCsvData(String csvFile, String nameData){
        String data = nameData;
        BufferedReader br = null;
        String line = "";
        try{
            br = new BufferedReader(new FileReader(CSV_FILES_PATH + csvFile + CSV_EXTENSION));
            System.out.println("Getting " +nameData + " from " + csvFile + ".csv file" );
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] TestDataDetail = line.split(DELIMITER);
                if (TestDataDetail.length > 0) {
                    if (TestDataDetail[0].equals(nameData)) {
                        data = TestDataDetail[1];
                        break;
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
        return data;
    }
}
