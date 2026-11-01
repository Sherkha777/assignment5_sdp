package eva.sync;

public interface SyncScoringStrategy {

    double score(PilotProfile pilot);

    String name();
}
