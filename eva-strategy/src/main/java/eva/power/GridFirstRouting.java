package eva.power;

public class GridFirstRouting implements PowerRoutingStrategy {

    private final GridStatusSource grid;

    public GridFirstRouting(GridStatusSource grid) {
        this.grid = grid;
    }

    @Override
    public PowerPlan plan(PowerDemand demand) {
        double gridMw = Math.min(demand.requiredMw(), grid.availableMegawatts());
        double batteryMw = Math.min(demand.requiredMw() - gridMw, demand.batteryMw());
        return new PowerPlan(gridMw, batteryMw, Math.max(0.0, demand.requiredMw() - gridMw - batteryMw));
    }

    @Override
    public String name() {
        return "grid-first";
    }
}
