package eva.sync;

import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertSame;

class SyncStrategySelectorTest {

    private final SyncScoringStrategy standard = new StressOnlySync();
    private final SyncScoringStrategy fatigue = new FatigueAdjustedSync(new FakeBiometricSource(0));
    private final SyncScoringStrategy veteran = new VeteranBonusSync();
    private final SyncStrategySelector selector = new SyncStrategySelector(standard, fatigue, veteran);

    @Test
    void daytimeRookieGetsTheStandardStrategy() {
        assertSame(standard, selector.select(new PilotProfile("Shinji", 30, 2), LocalTime.of(14, 0)));
    }

    @Test
    void daytimeVeteranGetsTheVeteranStrategy() {
        assertSame(veteran, selector.select(new PilotProfile("Rei", 30, 10), LocalTime.of(14, 0)));
    }

    @Test
    void nineSortiesIsStillARookie() {
        assertSame(standard, selector.select(new PilotProfile("Rei", 30, 9), LocalTime.of(14, 0)));
    }

    @Test
    void nightShiftGetsTheFatigueStrategy() {
        assertSame(fatigue, selector.select(new PilotProfile("Shinji", 30, 2), LocalTime.of(23, 30)));
        assertSame(fatigue, selector.select(new PilotProfile("Shinji", 30, 2), LocalTime.of(3, 0)));
    }

    @Test
    void nightBeatsExperience() {
        assertSame(fatigue, selector.select(new PilotProfile("Rei", 30, 40), LocalTime.of(2, 0)));
    }

    @Test
    void nightBoundariesAreInclusiveAtTenAndExclusiveAtSix() {
        PilotProfile rookie = new PilotProfile("Shinji", 30, 0);
        assertSame(fatigue, selector.select(rookie, LocalTime.of(22, 0)));
        assertSame(standard, selector.select(rookie, LocalTime.of(21, 59)));
        assertSame(fatigue, selector.select(rookie, LocalTime.of(5, 59)));
        assertSame(standard, selector.select(rookie, LocalTime.of(6, 0)));
    }
}
