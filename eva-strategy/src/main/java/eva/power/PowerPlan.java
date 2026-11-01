package eva.power;

public record PowerPlan(double gridMw, double batteryMw, double shortfallMw) {

    public double total() {
        return gridMw + batteryMw;
    }
}
