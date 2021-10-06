package dataProvaider;

import org.testng.annotations.DataProvider;
import support.ArrayObjects;
import static support.ConstantData.DATA_ACCOUNTS_CSV_FILE;

public class TestDataDevices {
    ArrayObjects arrayObjects = new ArrayObjects();

    /**
     *
     */
    @DataProvider(name = "Unregistered_Device_Test_P0")
    public Object[][] Unregistered_Device_Test_P0() {
        return arrayObjects.dataArrayLength7(DATA_ACCOUNTS_CSV_FILE, "Unregistered_Device_Test_P0");
    }

}
