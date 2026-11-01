package eva.power;

public interface PowerRoutingStrategy {

    PowerPlan plan(PowerDemand demand);

    String name();
}
