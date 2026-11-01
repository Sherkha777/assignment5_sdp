package eva.sync;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VeteranBonusSyncTest {

    private final VeteranBonusSync strategy = new VeteranBonusSync();

    @Test
    void rookieFeelsTheFullStress() {
        assertEquals(60.0, strategy.score(new PilotProfile("Shinji", 40, 0)));
    }

    @Test
    void experienceReducesTheStressThatCounts() {
        assertEquals(68.0, strategy.score(new PilotProfile("Rei", 40, 10)), 1e-9);
    }

    @Test
    void reliefStopsGrowingAtTheCap() {
        assertEquals(strategy.score(new PilotProfile("Rei", 40, 20)),
                strategy.score(new PilotProfile("Rei", 40, 200)));
    }
}
