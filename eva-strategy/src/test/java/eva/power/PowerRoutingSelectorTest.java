package eva.power;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

class PowerRoutingSelectorTest {

    private final GridStatusSource weakGrid = new FakeGridStatusSource(6);
    private final GridStatusSource strongGrid = new FakeGridStatusSource(20);
    private final PowerRoutingStrategy gridFirst = new GridFirstRouting(strongGrid);
    private final PowerRoutingStrategy batteryFirst = new BatteryFirstRouting(strongGrid);
    private final PowerRoutingStrategy balanced = new BalancedRouting(strongGrid);

    private PowerRoutingSelector selectorFor(GridStatusSource grid) {
        return new PowerRoutingSelector(grid, gridFirst, batteryFirst, balanced);
    }

    @Test
    void gridThatCannotCoverTheDemandSelectsBatteryFirst() {
        assertSame(batteryFirst, selectorFor(weakGrid).select(new PowerDemand(10, 8, 80)));
    }

    @Test
    void healthyGridWithLowBatterySelectsGridFirst() {
        assertSame(gridFirst, selectorFor(strongGrid).select(new PowerDemand(10, 2, 20)));
    }

    @Test
    void healthyGridWithHealthyBatterySelectsBalanced() {
        assertSame(balanced, selectorFor(strongGrid).select(new PowerDemand(10, 8, 80)));
    }

    @Test
    void weakGridBeatsLowBattery() {
        assertSame(batteryFirst, selectorFor(weakGrid).select(new PowerDemand(10, 2, 5)));
    }

    @Test
    void gridExactlyEqualToDemandCountsAsEnough() {
        assertSame(balanced, selectorFor(new FakeGridStatusSource(10)).select(new PowerDemand(10, 8, 80)));
    }

    @Test
    void batteryExactlyAtThirtyPercentIsNotLow() {
        assertSame(balanced, selectorFor(strongGrid).select(new PowerDemand(10, 8, 30)));
        assertSame(gridFirst, selectorFor(strongGrid).select(new PowerDemand(10, 8, 29.9)));
    }
}
