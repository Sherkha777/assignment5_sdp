package eva.sync;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SyncAssessorTest {

    private final PilotProfile pilot = new PilotProfile("Shinji", 40, 10);

    @Test
    void asksTheStrategyGivenInTheConstructor() {
        SyncAssessor assessor = new SyncAssessor(new StressOnlySync());
        assertEquals(60.0, assessor.assess(pilot));
        assertEquals("stress-only", assessor.strategyName());
    }

    @Test
    void strategyCanBeReplacedWhileRunning() {
        SyncAssessor assessor = new SyncAssessor(new StressOnlySync());
        assessor.setStrategy(new VeteranBonusSync());
        assertEquals(68.0, assessor.assess(pilot), 1e-9);
        assertEquals("veteran-bonus", assessor.strategyName());
    }

    @Test
    void refusesAMissingStrategy() {
        assertThrows(NullPointerException.class, () -> new SyncAssessor(null));
        SyncAssessor assessor = new SyncAssessor(new StressOnlySync());
        assertThrows(NullPointerException.class, () -> assessor.setStrategy(null));
    }
}
