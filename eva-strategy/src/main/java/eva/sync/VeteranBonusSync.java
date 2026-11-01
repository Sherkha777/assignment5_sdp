package eva.sync;

public class VeteranBonusSync implements SyncScoringStrategy {

    public static final int EXPERIENCE_CAP = 20;
    public static final double RELIEF_DIVISOR = 50.0;

    @Override
    public double score(PilotProfile pilot) {
        double relief = Math.min(pilot.sorties(), EXPERIENCE_CAP) / RELIEF_DIVISOR;
        return 100.0 - pilot.stress() * (1.0 - relief);
    }

    @Override
    public String name() {
        return "veteran-bonus";
    }
}
