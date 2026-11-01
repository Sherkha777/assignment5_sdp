package eva.power;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PowerRoutingBehaviorTest {

    private PowerPlan route(RoutingMode mode, double gridMw, double required, double batteryMw) {
        return PowerRouters.forMode(mode, new FakeGridStatusSource(gridMw))
                .route(new PowerDemand(required, batteryMw, 80));
    }

    @Test
    void gridFirstTakesAsMuchGridPowerAsPossible() {
        PowerPlan plan = route(RoutingMode.GRID_FIRST, 6, 10, 8);
        assertEquals(6.0, plan.gridMw(), 1e-9);
        assertEquals(4.0, plan.batteryMw(), 1e-9);
        assertEquals(0.0, plan.shortfallMw(), 1e-9);
    }

    @Test
    void batteryFirstTakesAsMuchBatteryPowerAsPossible() {
        PowerPlan plan = route(RoutingMode.BATTERY_FIRST, 6, 10, 8);
        assertEquals(2.0, plan.gridMw(), 1e-9);
        assertEquals(8.0, plan.batteryMw(), 1e-9);
        assertEquals(0.0, plan.shortfallMw(), 1e-9);
    }

    @Test
    void balancedSplitsTheLoadEvenlyWhenBothCanCarryIt() {
        PowerPlan plan = route(RoutingMode.BALANCED, 6, 10, 8);
        assertEquals(5.0, plan.gridMw(), 1e-9);
        assertEquals(5.0, plan.batteryMw(), 1e-9);
    }

    @Test
    void balancedTopsUpFromTheGridWhenTheBatteryRunsShort() {
        PowerPlan plan = route(RoutingMode.BALANCED, 20, 10, 3);
        assertEquals(7.0, plan.gridMw(), 1e-9);
        assertEquals(3.0, plan.batteryMw(), 1e-9);
    }

    @ParameterizedTest
    @EnumSource(RoutingMode.class)
    void reportsTheShortfallWhenBothSourcesAreTooSmall(RoutingMode mode) {
        PowerPlan plan = route(mode, 3, 10, 2);
        assertEquals(3.0, plan.gridMw(), 1e-9);
        assertEquals(2.0, plan.batteryMw(), 1e-9);
        assertEquals(5.0, plan.shortfallMw(), 1e-9);
    }

    @ParameterizedTest
    @EnumSource(RoutingMode.class)
    void everyModeCoversTheDemandExactlyWhenSupplyIsEnough(RoutingMode mode) {
        PowerPlan plan = route(mode, 12, 10, 9);
        assertEquals(10.0, plan.total(), 1e-9);
        assertEquals(0.0, plan.shortfallMw(), 1e-9);
        assertTrue(plan.gridMw() >= 0 && plan.batteryMw() >= 0);
    }

    @ParameterizedTest
    @EnumSource(RoutingMode.class)
    void zeroDemandNeedsNoPower(RoutingMode mode) {
        PowerPlan plan = route(mode, 12, 0, 9);
        assertEquals(0.0, plan.total(), 1e-9);
    }
}
