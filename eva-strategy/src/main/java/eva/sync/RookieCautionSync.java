package eva.sync;

public class RookieCautionSync implements SyncScoringStrategy {

    public static final int ROOKIE_BELOW_SORTIES = 3;
    public static final double ROOKIE_FACTOR = 0.8;

    @Override
    public double score(PilotProfile pilot) {
        double base = 100.0 - pilot.stress();
        return pilot.sorties() < ROOKIE_BELOW_SORTIES ? base * ROOKIE_FACTOR : base;
    }

    @Override
    public String name() {
        return "rookie-caution";
    }
}
