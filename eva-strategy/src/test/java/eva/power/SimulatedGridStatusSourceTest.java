package eva.power;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimulatedGridStatusSourceTest {

    @Test
    void reportsTheConfiguredPowerAndFollowsChanges() {
        SimulatedGridStatusSource source = new SimulatedGridStatusSource(12);
        assertEquals(12.0, source.availableMegawatts());
        source.setAvailableMegawatts(4);
        assertEquals(4.0, source.availableMegawatts());
    }
}
