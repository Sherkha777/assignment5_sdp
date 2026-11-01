package eva.power;

public class BatteryFirstRouting implements PowerRoutingStrategy {

    private final GridStatusSource grid;

    public BatteryFirstRouting(GridStatusSource grid) {
        this.grid = grid;
    }

    @Override
    public PowerPlan plan(PowerDemand demand) {
        double batteryMw = Math.min(demand.requiredMw(), demand.batteryMw());
        double gridMw = Math.min(demand.requiredMw() - batteryMw, grid.availableMegawatts());
        return new PowerPlan(gridMw, batteryMw, Math.max(0.0, demand.requiredMw() - gridMw - batteryMw));
    }

    @Override
    public String name() {
        return "battery-first";
    }
}
