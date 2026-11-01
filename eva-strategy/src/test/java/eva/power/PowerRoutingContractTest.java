package eva.power;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PowerRoutingContractTest {

    static Stream<Arguments> everyStrategyWithEveryDemand() {
        List<Double> gridSizes = List.of(0.0, 3.0, 10.0, 50.0);
        List<PowerDemand> demands = List.of(
                new PowerDemand(0, 0, 0),
                new PowerDemand(10, 2, 15),
                new PowerDemand(10, 10, 100),
                new PowerDemand(25, 40, 60));
        return gridSizes.stream().flatMap(size -> {
            GridStatusSource grid = new FakeGridStatusSource(size);
            List<PowerRoutingStrategy> strategies = List.of(
                    new GridFirstRouting(grid), new BatteryFirstRouting(grid), new BalancedRouting(grid));
            return strategies.stream().flatMap(s -> demands.stream().map(d -> Arguments.of(s, d, size)));
        });
    }

    @ParameterizedTest
    @MethodSource("everyStrategyWithEveryDemand")
    void planIsNeverNegativeAndNeverOverUsesASource(PowerRoutingStrategy strategy, PowerDemand demand, double gridSize) {
        PowerPlan plan = strategy.plan(demand);
        assertTrue(plan.gridMw() >= 0, strategy.name());
        assertTrue(plan.batteryMw() >= 0, strategy.name());
        assertTrue(plan.shortfallMw() >= 0, strategy.name());
        assertTrue(plan.gridMw() <= gridSize + 1e-9, strategy.name());
        assertTrue(plan.batteryMw() <= demand.batteryMw() + 1e-9, strategy.name());
        assertEquals(demand.requiredMw(), plan.total() + plan.shortfallMw(), 1e-9, strategy.name());
    }
}
