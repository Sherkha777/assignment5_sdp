package eva.app;

import eva.power.BalancedRouting;
import eva.power.BatteryFirstRouting;
import eva.power.GridFirstRouting;
import eva.power.PowerDemand;
import eva.power.PowerPlan;
import eva.power.PowerRouter;
import eva.power.PowerRoutingSelector;
import eva.power.PowerRoutingStrategy;
import eva.power.SimulatedGridStatusSource;
import eva.sync.FatigueAdjustedSync;
import eva.sync.PilotProfile;
import eva.sync.RookieCautionSync;
import eva.sync.SimulatedBiometricSource;
import eva.sync.StressOnlySync;
import eva.sync.SyncAssessor;
import eva.sync.SyncScoringStrategy;
import eva.sync.SyncStrategySelector;
import eva.sync.VeteranBonusSync;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalTime;
import java.util.List;

public class DemoApp {

    private final BufferedReader in;
    private final PrintStream out;

    private final SimulatedBiometricSource biometrics = new SimulatedBiometricSource();
    private final SimulatedGridStatusSource grid = new SimulatedGridStatusSource(12.0);

    private final List<SyncScoringStrategy> syncStrategies = List.of(
            new StressOnlySync(), new FatigueAdjustedSync(biometrics), new VeteranBonusSync(),
            new RookieCautionSync());
    private final List<PowerRoutingStrategy> powerStrategies = List.of(
            new GridFirstRouting(grid), new BatteryFirstRouting(grid), new BalancedRouting(grid));

    private final SyncStrategySelector syncSelector =
            new SyncStrategySelector(syncStrategies.get(0), syncStrategies.get(1), syncStrategies.get(2));
    private final PowerRoutingSelector powerSelector =
            new PowerRoutingSelector(grid, powerStrategies.get(0), powerStrategies.get(1), powerStrategies.get(2));

    private final SyncAssessor assessor = new SyncAssessor(syncStrategies.get(0));
    private final PowerRouter router = new PowerRouter(powerStrategies.get(0));

    public DemoApp(BufferedReader in, PrintStream out) {
        this.in = in;
        this.out = out;
    }

    public void run() throws IOException {
        boolean running = true;
        while (running) {
            printMenu();
            String choice = prompt("> ");
            if (choice == null) {
                break;
            }
            try {
                running = handle(choice.trim());
            } catch (NumberFormatException e) {
                out.println("that was not a number");
            } catch (IllegalArgumentException | java.time.DateTimeException e) {
                out.println("refused: " + e.getMessage());
            }
        }
        out.println("console closed");
    }

    private boolean handle(String choice) throws IOException {
        switch (choice) {
            case "1" -> compareSync();
            case "2" -> autoSync();
            case "3" -> manualSync();
            case "4" -> comparePower();
            case "5" -> autoPower();
            case "6" -> manualPower();
            case "7" -> setGrid();
            case "8" -> setHoursAwake();
            case "0" -> {
                return false;
            }
            default -> out.println("unknown option");
        }
        return true;
    }

    private void printMenu() {
        out.println();
        out.println("=== NERV strategy console ===");
        out.println("1  sync score: same pilot through all strategies");
        out.println("2  sync score: automatic choice by the selector");
        out.println("3  sync score: manual choice, switch at runtime");
        out.println("4  power routing: same demand through all strategies");
        out.println("5  power routing: automatic choice by the selector");
        out.println("6  power routing: manual choice, switch at runtime");
        out.println("7  set simulated grid power (MW)");
        out.println("8  set simulated hours awake for a pilot");
        out.println("0  exit");
    }

    private PilotProfile readPilot() throws IOException {
        String name = prompt("pilot name: ");
        double stress = Double.parseDouble(prompt("stress 0-100: ").trim());
        int sorties = Integer.parseInt(prompt("sorties flown: ").trim());
        return new PilotProfile(name, stress, sorties);
    }

    private PowerDemand readDemand() throws IOException {
        double required = Double.parseDouble(prompt("required MW: ").trim());
        double battery = Double.parseDouble(prompt("battery MW available: ").trim());
        double percent = Double.parseDouble(prompt("battery charge %: ").trim());
        return new PowerDemand(required, battery, percent);
    }

    private void compareSync() throws IOException {
        PilotProfile pilot = readPilot();
        for (SyncScoringStrategy strategy : syncStrategies) {
            out.printf("%-17s -> %.1f%n", strategy.name(), strategy.score(pilot));
        }
    }

    private void autoSync() throws IOException {
        PilotProfile pilot = readPilot();
        LocalTime now = LocalTime.parse(prompt("time of day HH:mm: ").trim());
        SyncScoringStrategy chosen = syncSelector.select(pilot, now);
        assessor.setStrategy(chosen);
        out.printf("selector chose %s -> %.1f%n", assessor.strategyName(), assessor.assess(pilot));
    }

    private void manualSync() throws IOException {
        out.println("current strategy: " + assessor.strategyName());
        for (int i = 0; i < syncStrategies.size(); i++) {
            out.println((i + 1) + "  " + syncStrategies.get(i).name());
        }
        int index = Integer.parseInt(prompt("choose strategy: ").trim()) - 1;
        assessor.setStrategy(syncStrategies.get(index));
        PilotProfile pilot = readPilot();
        out.printf("%s -> %.1f%n", assessor.strategyName(), assessor.assess(pilot));
    }

    private void comparePower() throws IOException {
        PowerDemand demand = readDemand();
        out.println("grid offers " + grid.availableMegawatts() + " MW");
        for (PowerRoutingStrategy strategy : powerStrategies) {
            out.println(String.format("%-14s -> ", strategy.name()) + describe(strategy.plan(demand)));
        }
    }

    private void autoPower() throws IOException {
        PowerDemand demand = readDemand();
        router.setStrategy(powerSelector.select(demand));
        out.println("selector chose " + router.strategyName() + " -> " + describe(router.route(demand)));
    }

    private void manualPower() throws IOException {
        out.println("current strategy: " + router.strategyName());
        for (int i = 0; i < powerStrategies.size(); i++) {
            out.println((i + 1) + "  " + powerStrategies.get(i).name());
        }
        int index = Integer.parseInt(prompt("choose strategy: ").trim()) - 1;
        router.setStrategy(powerStrategies.get(index));
        out.println(router.strategyName() + " -> " + describe(router.route(readDemand())));
    }

    private void setGrid() throws IOException {
        grid.setAvailableMegawatts(Double.parseDouble(prompt("grid MW: ").trim()));
        out.println("grid now offers " + grid.availableMegawatts() + " MW");
    }

    private void setHoursAwake() throws IOException {
        String name = prompt("pilot name: ");
        biometrics.setHoursAwake(name, Double.parseDouble(prompt("hours awake: ").trim()));
        out.println(name + " has been awake " + biometrics.hoursAwake(name) + " h");
    }

    private String describe(PowerPlan plan) {
        return String.format("grid %.1f MW, battery %.1f MW, shortfall %.1f MW",
                plan.gridMw(), plan.batteryMw(), plan.shortfallMw());
    }

    private String prompt(String text) throws IOException {
        out.print(text);
        out.flush();
        return in.readLine();
    }
}
