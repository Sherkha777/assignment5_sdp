package eva.sync;

public class FatigueAdjustedSync implements SyncScoringStrategy {

    public static final double RESTED_HOURS = 8.0;
    public static final double PENALTY_PER_HOUR = 2.5;

    private final BiometricSource biometrics;

    public FatigueAdjustedSync(BiometricSource biometrics) {
        this.biometrics = biometrics;
    }

    @Override
    public double score(PilotProfile pilot) {
        double overtime = Math.max(0.0, biometrics.hoursAwake(pilot.name()) - RESTED_HOURS);
        return Math.max(0.0, 100.0 - pilot.stress() - overtime * PENALTY_PER_HOUR);
    }

    @Override
    public String name() {
        return "fatigue-adjusted";
    }
}
