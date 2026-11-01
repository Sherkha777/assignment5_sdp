package eva.sync;

import java.util.HashMap;
import java.util.Map;

public class SimulatedBiometricSource implements BiometricSource {

    private final Map<String, Double> overrides = new HashMap<>();

    public void setHoursAwake(String pilotName, double hours) {
        overrides.put(pilotName, hours);
    }

    @Override
    public double hoursAwake(String pilotName) {
        return overrides.getOrDefault(pilotName, (double) Math.abs(pilotName.hashCode() % 20));
    }
}
