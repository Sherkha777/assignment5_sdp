package eva.sync;

public class FakeBiometricSource implements BiometricSource {

    private final double hours;

    public FakeBiometricSource(double hours) {
        this.hours = hours;
    }

    @Override
    public double hoursAwake(String pilotName) {
        return hours;
    }
}
