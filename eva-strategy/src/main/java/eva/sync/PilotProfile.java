package eva.sync;

public record PilotProfile(String name, double stress, int sorties) {

    public PilotProfile {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("pilot name is required");
        }
        if (stress < 0 || stress > 100) {
            throw new IllegalArgumentException("stress must be between 0 and 100");
        }
        if (sorties < 0) {
            throw new IllegalArgumentException("sorties cannot be negative");
        }
    }
}
