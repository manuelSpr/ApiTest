package support;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import support.User;
import static support.CSVReaders.testCsvDataDriven;

public class ArrayObjects {

    /**
     * Remove " symbol from strings in the array
     * @param testDataDetail
     * @return
     */
    private static Object[] FormatString(Object[] testDataDetail){
        for (int i = 0; i < testDataDetail.length; i++){
            testDataDetail[i] = testDataDetail[i].toString().trim();
            if (testDataDetail[i].toString().startsWith("\"")) {
                testDataDetail[i] = testDataDetail[i].toString().substring(1);
            }
            if (testDataDetail[i].toString().endsWith("\"")) {
                testDataDetail[i] = testDataDetail[i].toString().substring(0, testDataDetail[i].toString().length() - 1);
            }
            testDataDetail[i] = testDataDetail[i].toString().trim();
        }
        return testDataDetail;
    }

    /**
     * the second element on the Array is obligatory to be user object, Retrieve an array objects from an specific size file related to a specific data provider
     * @param CSVName
     * @param dataProviderName
     * @return
     */
    public Object[][]dataArrayLength6(String CSVName, String dataProviderName){
        List<Object[]> objects = testCsvDataDriven(CSVName, dataProviderName);
        Object[][] dataProvider = new Object[objects.size()][6];
        int currentPosition = 0;
        for (Object[] testObject: objects) {
            testObject = FormatString(testObject);
            ObjectMapper mapper = new ObjectMapper();
            try {
                dataProvider[currentPosition][0] = testObject[1].toString();
                dataProvider[currentPosition][1] = mapper.readValue(testObject[2].toString(), User.class);
                dataProvider[currentPosition][2] = testObject[3].toString();
                dataProvider[currentPosition][3] = testObject[4].toString();
                dataProvider[currentPosition][4] = testObject[5].toString();
                dataProvider[currentPosition][5] = testObject[6].toString();
            } catch (Exception  e) {
                e.printStackTrace();
            }
            currentPosition ++;
        }
        return dataProvider;
    }

    /**
     * the second element on the Array is obligatory to be user object, Retrieve an array objects from an specific size file related to a specific data provider
     * @param CSVName
     * @param dataProviderName
     * @return
     */
    public Object[][]dataArrayLength7(String CSVName, String dataProviderName){
        List<Object[]> objects = testCsvDataDriven(CSVName, dataProviderName);
        Object[][] dataProvider = new Object[objects.size()][7];
        int currentPosition = 0;
        for (Object[] testObject: objects) {
            testObject = FormatString(testObject);
            ObjectMapper mapper = new ObjectMapper();
            try {
                dataProvider[currentPosition][0] = testObject[1].toString();
                dataProvider[currentPosition][1] = mapper.readValue(testObject[2].toString(), User.class);
                dataProvider[currentPosition][2] = testObject[3].toString();
                dataProvider[currentPosition][3] = testObject[4].toString();
                dataProvider[currentPosition][4] = testObject[5].toString();
                dataProvider[currentPosition][5] = testObject[6].toString();
                dataProvider[currentPosition][6] = testObject[7].toString();
            } catch (Exception  e) {
                e.printStackTrace();
            }
            currentPosition ++;
        }
        return dataProvider;
    }

    /**
     * the second element on the Array is obligatory to be user object, Retrieve an array objects from an specific size file related to a specific data provider
     * @param CSVName
     * @param dataProviderName
     * @return
     */
    public Object[][]dataArrayLength8(String CSVName, String dataProviderName){
        List<Object[]> objects = testCsvDataDriven(CSVName, dataProviderName);
        Object[][] dataProvider = new Object[objects.size()][8];
        int currentPosition = 0;
        for (Object[] testObject: objects) {
            testObject = FormatString(testObject);
            ObjectMapper mapper = new ObjectMapper();
            try {
                dataProvider[currentPosition][0] = testObject[1].toString();
                dataProvider[currentPosition][1] = mapper.readValue(testObject[2].toString(), User.class);
                dataProvider[currentPosition][2] = testObject[3].toString();
                dataProvider[currentPosition][3] = testObject[4].toString();
                dataProvider[currentPosition][4] = testObject[5].toString();
                dataProvider[currentPosition][5] = testObject[6].toString();
                dataProvider[currentPosition][6] = testObject[7].toString();
                dataProvider[currentPosition][7] = testObject[8].toString();
            } catch (Exception  e) {
                e.printStackTrace();
            }
            currentPosition ++;
        }
        return dataProvider;
    }

}
