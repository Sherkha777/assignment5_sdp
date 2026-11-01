package eva.sync;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FatigueAdjustedSyncTest {

    private final PilotProfile pilot = new PilotProfile("Asuka", 20, 3);

    @Test
    void restedPilotKeepsTheStressOnlyScore() {
        assertEquals(80.0, new FatigueAdjustedSync(new FakeBiometricSource(6)).score(pilot));
    }

    @Test
    void exactlyRestedHoursCostNothing() {
        assertEquals(80.0, new FatigueAdjustedSync(new FakeBiometricSource(8)).score(pilot));
    }

    @Test
    void everyHourBeyondRestedCostsTheFixedPenalty() {
        assertEquals(70.0, new FatigueAdjustedSync(new FakeBiometricSource(12)).score(pilot));
    }

    @Test
    void scoreStopsAtZero() {
        assertEquals(0.0, new FatigueAdjustedSync(new FakeBiometricSource(60)).score(pilot));
    }

    @Test
    void readsTheHoursOfTheRequestedPilot() {
        SimulatedBiometricSource source = new SimulatedBiometricSource();
        source.setHoursAwake("Asuka", 10);
        assertEquals(75.0, new FatigueAdjustedSync(source).score(pilot));
    }
}
