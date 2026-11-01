package eva.sync;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SyncStrategyContractTest {

    static Stream<Arguments> everyStrategyWithEveryPilot() {
        List<SyncScoringStrategy> strategies = List.of(
                new StressOnlySync(),
                new FatigueAdjustedSync(new FakeBiometricSource(30)),
                new VeteranBonusSync(),
                new RookieCautionSync());
        List<PilotProfile> pilots = List.of(
                new PilotProfile("Calm", 0, 0),
                new PilotProfile("Tired", 55, 4),
                new PilotProfile("Veteran", 90, 50),
                new PilotProfile("Wreck", 100, 0));
        return strategies.stream().flatMap(s -> pilots.stream().map(p -> Arguments.of(s, p)));
    }

    @ParameterizedTest
    @MethodSource("everyStrategyWithEveryPilot")
    void scoreIsNeverNegativeAndNeverAboveHundred(SyncScoringStrategy strategy, PilotProfile pilot) {
        double score = strategy.score(pilot);
        assertTrue(score >= 0.0, strategy.name() + " gave " + score);
        assertTrue(score <= 100.0, strategy.name() + " gave " + score);
    }
}
