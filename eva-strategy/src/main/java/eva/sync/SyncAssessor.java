package eva.sync;

import java.util.Objects;

public class SyncAssessor {

    private SyncScoringStrategy strategy;

    public SyncAssessor(SyncScoringStrategy strategy) {
        this.strategy = Objects.requireNonNull(strategy);
    }

    public void setStrategy(SyncScoringStrategy strategy) {
        this.strategy = Objects.requireNonNull(strategy);
    }

    public double assess(PilotProfile pilot) {
        return strategy.score(pilot);
    }

    public String strategyName() {
        return strategy.name();
    }
}
