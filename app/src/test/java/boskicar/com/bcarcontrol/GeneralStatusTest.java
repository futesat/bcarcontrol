package boskicar.com.bcarcontrol;

import org.junit.Test;
import static org.junit.Assert.*;

public class GeneralStatusTest {
    @Test
    public void testEnumValues() {
        assertEquals(GeneralStatus.OrderType.STOP, GeneralStatus.OrderType.valueOf("STOP"));
        assertEquals(GeneralStatus.Status.ON, GeneralStatus.Status.valueOf("ON"));
    }

    @Test
    public void testSettersAndGetters() {
        GeneralStatus status = new GeneralStatus();
        status.setEngineControl(GeneralStatus.Status.ON);
        assertEquals(GeneralStatus.Status.ON, status.getEngineControl());

        status.setLastFBOrderSpeed(100);
        assertEquals(100, status.getLastFBOrderSpeed());

        status.setSteeringWheelControl(GeneralStatus.Status.OFF);
        assertEquals(GeneralStatus.Status.OFF, status.getSteeringWheelControl());

        status.setMobileControl(GeneralStatus.Status.BLINK);
        assertEquals(GeneralStatus.Status.BLINK, status.getMobileControl());

        status.setFans(GeneralStatus.Status.ON);
        assertEquals(GeneralStatus.Status.ON, status.getFans());

        status.setLights(GeneralStatus.Status.OFF);
        assertEquals(GeneralStatus.Status.OFF, status.getLights());

        status.setLastFBOrderDate("2023-01-01");
        assertEquals("2023-01-01", status.getLastFBOrderDate());

        status.setLastFBOrderType(GeneralStatus.OrderType.FORDWARD);
        assertEquals(GeneralStatus.OrderType.FORDWARD, status.getLastFBOrderType());

        status.setLastLROrderDate("2023-01-02");
        assertEquals("2023-01-02", status.getLastLROrderDate());

        status.setLastLROrderType(GeneralStatus.OrderType.STOP);
        assertEquals(GeneralStatus.OrderType.STOP, status.getLastLROrderType());

        status.setLastLRDesiredAngle(45);
        assertEquals(45, status.getLastLRDesiredAngle());
    }

    @Test
    public void testToString() {
        GeneralStatus status = new GeneralStatus();
        status.setLastFBOrderSpeed(50);
        status.setLastFBOrderDate("2023-10-27");
        String result = status.toString();

        assertTrue(result.contains("lastFBOrderSpeed=50"));
        assertTrue(result.contains("lastFBOrderDate=2023-10-27"));
        assertTrue(result.contains("GeneralStatus ["));
    }
}
