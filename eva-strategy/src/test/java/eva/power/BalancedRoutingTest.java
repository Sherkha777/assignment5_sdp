package eva.power;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BalancedRoutingTest {

    @Test
    void splitsEvenlyWhenBothSourcesAreLargeEnough() {
        PowerPlan plan = new BalancedRouting(new FakeGridStatusSource(12)).plan(new PowerDemand(10, 9, 70));
        assertEquals(5.0, plan.gridMw(), 1e-9);
        assertEquals(5.0, plan.batteryMw(), 1e-9);
    }

    @Test
    void gridMakesUpTheGapOfASmallBattery() {
        PowerPlan plan = new BalancedRouting(new FakeGridStatusSource(20)).plan(new PowerDemand(10, 3, 10));
        assertEquals(7.0, plan.gridMw(), 1e-9);
        assertEquals(3.0, plan.batteryMw(), 1e-9);
    }

    @Test
    void batteryMakesUpTheGapOfAWeakGrid() {
        PowerPlan plan = new BalancedRouting(new FakeGridStatusSource(2)).plan(new PowerDemand(10, 9, 70));
        assertEquals(2.0, plan.gridMw(), 1e-9);
        assertEquals(8.0, plan.batteryMw(), 1e-9);
    }

    @Test
    void hasAName() {
        assertEquals("balanced", new BalancedRouting(new FakeGridStatusSource(0)).name());
    }
}
