package eva.power;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

public final class PowerRouters {

    private static final Map<RoutingMode, Function<GridStatusSource, PowerRoutingStrategy>> CATALOG =
            new EnumMap<>(RoutingMode.class);

    static {
        CATALOG.put(RoutingMode.GRID_FIRST, GridFirstRouting::new);
        CATALOG.put(RoutingMode.BATTERY_FIRST, BatteryFirstRouting::new);
        CATALOG.put(RoutingMode.BALANCED, BalancedRouting::new);
    }

    private PowerRouters() {
    }

    public static PowerRouter forMode(RoutingMode mode, GridStatusSource grid) {
        return new PowerRouter(CATALOG.get(mode).apply(grid));
    }
}
