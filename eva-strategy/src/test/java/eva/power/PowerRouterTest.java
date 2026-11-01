package eva.power;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PowerRouterTest {

    private final GridStatusSource grid = new FakeGridStatusSource(6);
    private final PowerDemand demand = new PowerDemand(10, 8, 80);

    @Test
    void callsTheStrategyFromTheConstructor() {
        PowerRouter router = new PowerRouter(new GridFirstRouting(grid));
        assertEquals(6.0, router.route(demand).gridMw(), 1e-9);
        assertEquals("grid-first", router.strategyName());
    }

    @Test
    void strategyCanBeReplacedWhileRunning() {
        PowerRouter router = new PowerRouter(new GridFirstRouting(grid));
        router.setStrategy(new BatteryFirstRouting(grid));
        assertEquals(8.0, router.route(demand).batteryMw(), 1e-9);
        assertEquals("battery-first", router.strategyName());
    }

    @Test
    void runsAnyStrategyWithoutKnowingItsType() {
        PowerRouter router = new PowerRouter(new PowerRoutingStrategy() {
            @Override
            public PowerPlan plan(PowerDemand demand) {
                return new PowerPlan(1, 2, 3);
            }

            @Override
            public String name() {
                return "stub";
            }
        });
        assertEquals(3.0, router.route(demand).shortfallMw());
        assertEquals("stub", router.strategyName());
    }

    @Test
    void refusesAMissingStrategy() {
        assertThrows(NullPointerException.class, () -> new PowerRouter(null));
        PowerRouter router = new PowerRouter(new GridFirstRouting(grid));
        assertThrows(NullPointerException.class, () -> router.setStrategy(null));
    }
}
