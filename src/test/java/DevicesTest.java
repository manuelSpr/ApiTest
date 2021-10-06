import dataProvaider.TestDataDevices;
import org.testng.annotations.Test;

public class DevicesTest {

    @Test(dataProvider = "Unregistered_Device_Test_P0", dataProviderClass = TestDataDevices.class)
    public void Unregistered_Device_Test_P0() {

    }

}
