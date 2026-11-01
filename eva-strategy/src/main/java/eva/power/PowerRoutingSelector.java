package eva.power;

public class PowerRoutingSelector {

    public static final double LOW_BATTERY_PERCENT = 30.0;

    private final GridStatusSource grid;
    private final PowerRoutingStrategy gridFirst;
    private final PowerRoutingStrategy batteryFirst;
    private final PowerRoutingStrategy balanced;

    public PowerRoutingSelector(GridStatusSource grid, PowerRoutingStrategy gridFirst,
                                PowerRoutingStrategy batteryFirst, PowerRoutingStrategy balanced) {
        this.grid = grid;
        this.gridFirst = gridFirst;
        this.batteryFirst = batteryFirst;
        this.balanced = balanced;
    }

    public PowerRoutingStrategy select(PowerDemand demand) {
        if (grid.availableMegawatts() < demand.requiredMw()) {
            return batteryFirst;
        }
        if (demand.batteryPercent() < LOW_BATTERY_PERCENT) {
            return gridFirst;
        }
        return balanced;
    }
}
