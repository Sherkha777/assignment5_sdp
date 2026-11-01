package eva.power;

public class FakeGridStatusSource implements GridStatusSource {

    private final double availableMw;

    public FakeGridStatusSource(double availableMw) {
        this.availableMw = availableMw;
    }

    @Override
    public double availableMegawatts() {
        return availableMw;
    }
}
