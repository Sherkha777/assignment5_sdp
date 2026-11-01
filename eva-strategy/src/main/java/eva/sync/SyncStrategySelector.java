package eva.sync;

import java.time.LocalTime;

public class SyncStrategySelector {

    public static final int VETERAN_SORTIES = 10;
    public static final LocalTime NIGHT_STARTS = LocalTime.of(22, 0);
    public static final LocalTime NIGHT_ENDS = LocalTime.of(6, 0);

    private final SyncScoringStrategy standard;
    private final SyncScoringStrategy fatigue;
    private final SyncScoringStrategy veteran;

    public SyncStrategySelector(SyncScoringStrategy standard, SyncScoringStrategy fatigue,
                                SyncScoringStrategy veteran) {
        this.standard = standard;
        this.fatigue = fatigue;
        this.veteran = veteran;
    }

    public SyncScoringStrategy select(PilotProfile pilot, LocalTime now) {
        boolean night = !now.isBefore(NIGHT_STARTS) || now.isBefore(NIGHT_ENDS);
        if (night) {
            return fatigue;
        }
        if (pilot.sorties() >= VETERAN_SORTIES) {
            return veteran;
        }
        return standard;
    }
}
