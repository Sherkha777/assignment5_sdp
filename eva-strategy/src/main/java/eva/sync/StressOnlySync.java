package eva.sync;

public class StressOnlySync implements SyncScoringStrategy {

    @Override
    public double score(PilotProfile pilot) {
        return 100.0 - pilot.stress();
    }

    @Override
    public String name() {
        return "stress-only";
    }
}
