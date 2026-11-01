package eva.sync;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StressOnlySyncTest {

    private final StressOnlySync strategy = new StressOnlySync();

    @Test
    void scoreIsHundredMinusStress() {
        assertEquals(70.0, strategy.score(new PilotProfile("Shinji", 30, 0)));
    }

    @Test
    void sortiesDoNotChangeTheScore() {
        assertEquals(70.0, strategy.score(new PilotProfile("Shinji", 30, 25)));
    }

    @Test
    void hasAName() {
        assertEquals("stress-only", strategy.name());
    }
}
