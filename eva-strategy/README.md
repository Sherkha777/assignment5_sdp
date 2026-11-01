# Assignment 5 - Strategy pattern: EVA launch, second part

Amangeldin Sherkhan, group SE-2536
Domain: the same Evangelion EVA launch theme as Assignment 4. This assignment covers two decisions in that launch, how to score a pilot and how to route power. Only the code in this repository is new.

## 1. Run it

JDK 17+ and Maven are needed.

| What | Command |
| --- | --- |
| Run all tests | `mvn test` |
| Run the demo console | `mvn -q compile exec:java` |

## 2. The two strategy families

| | Family | Interface | Strategies | Different logic |
| --- | --- | --- | --- | --- |
| 1 | Pilot sync scoring | `SyncScoringStrategy` | `StressOnlySync`, `FatigueAdjustedSync`, `VeteranBonusSync` | stress alone, stress minus a penalty from a biometric reading, stress scaled down by experience |
| 2 | Power routing | `PowerRoutingStrategy` | `GridFirstRouting`, `BatteryFirstRouting`, `BalancedRouting` | grid first, battery first, even split with a second top-up pass |

Contexts: `SyncAssessor` and `PowerRouter`. Both take a strategy in the constructor, replace it with `setStrategy()`, and only call it. They contain no `if`, `switch` or `instanceof`.

Selectors (the only place with `if`):

| Selector | Rules in priority order |
| --- | --- |
| `SyncStrategySelector` | night shift 22:00-06:00 gives fatigue-adjusted; 10 or more sorties gives veteran-bonus; otherwise stress-only |
| `PowerRoutingSelector` | grid below the demand gives battery-first; battery charge below 30% gives grid-first; otherwise balanced |

## 3. Classes and responsibilities

| Class | Responsibility |
| --- | --- |
| `PilotProfile` | Holds the validated name, stress level, sortie count of one pilot. |
| `SyncScoringStrategy` | Contract for turning a pilot profile into a synchronisation score. |
| `StressOnlySync` | Scores synchronisation as one hundred minus the pilot stress. |
| `FatigueAdjustedSync` | Subtracts a penalty for every hour awake beyond rest from the stress score. |
| `VeteranBonusSync` | Shrinks the counted stress in proportion to sorties flown, capped at twenty. |
| `BiometricSource` | Hides where the hours-awake reading of a pilot comes from. |
| `SimulatedBiometricSource` | Supplies repeatable hours-awake readings that the demo can adjust. |
| `SyncAssessor` | Context forwarding each assessment to the current scoring strategy, replaceable at runtime. |
| `SyncStrategySelector` | Picks the scoring strategy from time of day plus pilot experience. |
| `PowerDemand` | Holds the requested megawatts with the battery capacity plus battery charge percentage. |
| `PowerPlan` | Holds the megawatts taken from grid plus battery, with any uncovered shortfall. |
| `PowerRoutingStrategy` | Contract for splitting one power demand between grid plus battery. |
| `GridFirstRouting` | Takes grid power first, adding battery power for the remainder. |
| `BatteryFirstRouting` | Takes battery power first, adding grid power for the remainder. |
| `BalancedRouting` | Aims for an even split, then tops up from whichever source has capacity left. |
| `GridStatusSource` | Hides where the available grid megawatts reading comes from. |
| `SimulatedGridStatusSource` | Reports an adjustable grid megawatt figure for the demo. |
| `PowerRouter` | Context forwarding each routing request to the current power strategy, replaceable at runtime. |
| `PowerRoutingSelector` | Picks the routing strategy from grid capacity plus battery charge. |
| `RoutingMode` | Names the routing variants for the factory lookup. |
| `PowerRouters` | Builds routers by mode name from a lookup table. |
| `DemoApp` | Runs the console menu demonstrating comparison, selection, runtime switching. |
| `Main` | Starts the demo console. |

## 4. Why Strategy, not State or Template Method

**Not State.** In State the object moves itself from one behaviour to the next as its own condition changes, and the states know the transitions. Here nothing moves by itself. A `SyncAssessor` never decides to become fatigue-adjusted after a score. The choice comes from outside, from the Selector or from the operator in the demo, and the strategies know nothing about each other. The three strategies are also all valid at the same time for the same input, which is not true for states.

**Not Template Method.** Template Method keeps one fixed skeleton in a base class and lets subclasses override single steps, fixed at compile time by inheritance. My strategies share no skeleton. `FatigueAdjustedSync` reads a biometric source, `VeteranBonusSync` reads the sortie count, `StressOnlySync` reads neither, so each one computes the whole result in its own way. The context also holds the algorithm by composition, so `setStrategy()` can swap it while the program runs, which inheritance cannot do.

**So it is Strategy:** a family of interchangeable algorithms behind one small interface, one context that delegates to the current one, and a client or Selector that picks. Adding a new algorithm means adding a new class. The contexts and the existing strategies stay untouched (Open/Closed). To make a new strategy selectable automatically, only the Selector gets a new rule.

## 5. Refactoring with Git history (power routing family)

| | Commit |
| --- | --- |
| Before: all variants in one class with a `switch` | `https://github.com/<your-username>/<your-repo>/commit/c5aa655bbb99a06d75264cc80a0d786871bd0676` |
| After: refactored into Strategy | `https://github.com/<your-username>/<your-repo>/commit/5bbf6ffbac4b98d3c42ded44da4a2dec21acaaa6` |

Replace `<your-username>/<your-repo>` with the real repository after pushing. Push this repository as it is, with its history, so the hashes stay the same.

The same tests pass on both commits. `PowerRoutingBehaviorTest` is byte-for-byte identical in both and reaches the router only through `PowerRouters.forMode(...)`, which exists in both versions. Check with:

```
git checkout c5aa655bbb99a06d75264cc80a0d786871bd0676
mvn test
git checkout 5bbf6ffbac4b98d3c42ded44da4a2dec21acaaa6
mvn test
git checkout main
```

## 6. Isolation from change (Clean Code chapter 10)

Two strategies need outside data. Each source sits behind my own interface, like PortfolioManager and StockExchange in the lecture:

- `BiometricSource` hides the hours-awake sensor. `FatigueAdjustedSync` depends only on this interface. Production-like code uses `SimulatedBiometricSource`, tests use `FakeBiometricSource` with a fixed value.
- `GridStatusSource` hides the city grid reading. The three routing strategies plus `PowerRoutingSelector` depend only on this interface. The demo uses `SimulatedGridStatusSource`, tests use `FakeGridStatusSource`.

Swapping a source means writing one new class that implements the interface. No strategy changes.

## 7. Tests (JUnit 5)

- One test class per strategy: `StressOnlySyncTest`, `FatigueAdjustedSyncTest`, `VeteranBonusSyncTest`, `GridFirstRoutingTest`, `BatteryFirstRoutingTest`, `BalancedRoutingTest`.
- Selector rules, including the boundaries: `SyncStrategySelectorTest`, `PowerRoutingSelectorTest`.
- Parameterized tests that run the same check on every strategy of a family: `SyncStrategyContractTest` (score stays between 0 and 100) and `PowerRoutingContractTest` (no negative value, no source over-used, grid plus battery plus shortfall equals the demand).
- Contexts: `SyncAssessorTest`, `PowerRouterTest` (constructor strategy, `setStrategy()` at runtime, a stub strategy of unknown type).
- Demo console: `DemoAppTest`.

## 8. Demo script

Run `mvn -q compile exec:java`.

1. Option 1, Shinji, stress 30, sorties 2: the same pilot through all three sync strategies.
2. Option 8, set Shinji to 14 hours awake. Option 2, Shinji, 30, 2, time 23:00: the selector chooses fatigue-adjusted (night). Try 12:00 with 15 sorties: it chooses veteran-bonus.
3. Option 3: choose a strategy by hand, then choose another one. The same assessor object is switched at runtime.
4. Option 4, required 10, battery 8, charge 80: the same demand through all three routing strategies.
5. Option 5: the selector chooses balanced. Option 7, set the grid to 4 MW. Option 5 again: the selector now chooses battery-first.
6. Option 6: choose a routing strategy by hand, then switch to another.

## 9. How to add a strategy (live coding)

New sync strategy: create a class implementing `SyncScoringStrategy`, add a rule in `SyncStrategySelector.select` if it must be picked automatically. New routing strategy: implement `PowerRoutingStrategy`, add a rule in `PowerRoutingSelector.select`. The contexts never change.
