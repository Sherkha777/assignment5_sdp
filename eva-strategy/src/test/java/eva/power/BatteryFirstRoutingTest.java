package eva.power;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BatteryFirstRoutingTest {

    @Test
    void drawsFromTheBatteryBeforeTouchingTheGrid() {
        PowerPlan plan = new BatteryFirstRouting(new FakeGridStatusSource(12)).plan(new PowerDemand(10, 15, 90));
        assertEquals(10.0, plan.batteryMw(), 1e-9);
        assertEquals(0.0, plan.gridMw(), 1e-9);
    }

    @Test
    void gridCoversWhatTheBatteryCannot() {
        PowerPlan plan = new BatteryFirstRouting(new FakeGridStatusSource(12)).plan(new PowerDemand(10, 4, 20));
        assertEquals(4.0, plan.batteryMw(), 1e-9);
        assertEquals(6.0, plan.gridMw(), 1e-9);
    }

    @Test
    void hasAName() {
        assertEquals("battery-first", new BatteryFirstRouting(new FakeGridStatusSource(0)).name());
    }
}
