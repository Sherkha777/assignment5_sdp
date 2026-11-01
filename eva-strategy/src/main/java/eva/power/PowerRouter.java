package eva.power;

import java.util.Objects;

public class PowerRouter {

    private PowerRoutingStrategy strategy;

    public PowerRouter(PowerRoutingStrategy strategy) {
        this.strategy = Objects.requireNonNull(strategy);
    }

    public void setStrategy(PowerRoutingStrategy strategy) {
        this.strategy = Objects.requireNonNull(strategy);
    }

    public PowerPlan route(PowerDemand demand) {
        return strategy.plan(demand);
    }

    public String strategyName() {
        return strategy.name();
    }
}
