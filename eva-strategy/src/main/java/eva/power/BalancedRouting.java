package eva.power;

public class BalancedRouting implements PowerRoutingStrategy {

    private final GridStatusSource grid;

    public BalancedRouting(GridStatusSource grid) {
        this.grid = grid;
    }

    @Override
    public PowerPlan plan(PowerDemand demand) {
        double available = grid.availableMegawatts();
        double firstPassGrid = Math.min(demand.requiredMw() / 2, available);
        double batteryMw = Math.min(demand.requiredMw() - firstPassGrid, demand.batteryMw());
        double gridMw = Math.min(demand.requiredMw() - batteryMw, available);
        return new PowerPlan(gridMw, batteryMw, Math.max(0.0, demand.requiredMw() - gridMw - batteryMw));
    }

    @Override
    public String name() {
        return "balanced";
    }
}
