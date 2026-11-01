package eva.power;

public class SimulatedGridStatusSource implements GridStatusSource {

    private double availableMw;

    public SimulatedGridStatusSource(double availableMw) {
        this.availableMw = availableMw;
    }

    public void setAvailableMegawatts(double availableMw) {
        this.availableMw = availableMw;
    }

    @Override
    public double availableMegawatts() {
        return availableMw;
    }
}
