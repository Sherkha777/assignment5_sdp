package eva.power;

public record PowerDemand(double requiredMw, double batteryMw, double batteryPercent) {

    public PowerDemand {
        if (requiredMw < 0 || batteryMw < 0) {
            throw new IllegalArgumentException("power values cannot be negative");
        }
        if (batteryPercent < 0 || batteryPercent > 100) {
            throw new IllegalArgumentException("battery percent must be between 0 and 100");
        }
    }
}
