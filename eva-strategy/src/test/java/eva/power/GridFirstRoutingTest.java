package eva.power;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GridFirstRoutingTest {

    @Test
    void drawsFromTheGridBeforeTouchingTheBattery() {
        PowerPlan plan = new GridFirstRouting(new FakeGridStatusSource(12)).plan(new PowerDemand(10, 8, 90));
        assertEquals(10.0, plan.gridMw(), 1e-9);
        assertEquals(0.0, plan.batteryMw(), 1e-9);
    }

    @Test
    void batteryCoversWhatTheGridCannot() {
        PowerPlan plan = new GridFirstRouting(new FakeGridStatusSource(6)).plan(new PowerDemand(10, 8, 90));
        assertEquals(6.0, plan.gridMw(), 1e-9);
        assertEquals(4.0, plan.batteryMw(), 1e-9);
    }

    @Test
    void hasAName() {
        assertEquals("grid-first", new GridFirstRouting(new FakeGridStatusSource(0)).name());
    }
}
