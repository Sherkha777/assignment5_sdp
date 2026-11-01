package eva.sync;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RookieCautionSyncTest {

    private final RookieCautionSync strategy = new RookieCautionSync();

    @Test
    void rookieScoreIsCutByTwentyPercent() {
        assertEquals(56.0, strategy.score(new PilotProfile("Shinji", 30, 0)), 0.0001);
    }

    @Test
    void pilotWithThreeSortiesIsNoRookie() {
        assertEquals(70.0, strategy.score(new PilotProfile("Shinji", 30, 3)), 0.0001);
    }

    @Test
    void hasAName() {
        assertEquals("rookie-caution", strategy.name());
    }
}
