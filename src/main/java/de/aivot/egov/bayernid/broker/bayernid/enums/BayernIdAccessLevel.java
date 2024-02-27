package de.aivot.egov.bayernid.broker.bayernid.enums;

import java.util.Optional;

public enum BayernIdAccessLevel {
    STORK_QAA_LEVEL_1("level1", "STORK-QAA-Level-1"),
    STORK_QAA_LEVEL_2("level2", "STORK-QAA-Level-2"),
    STORK_QAA_LEVEL_3("level3", "STORK-QAA-Level-3"),
    STORK_QAA_LEVEL_4("level4", "STORK-QAA-Level-4");

    private final String scopeValue;
    private final String bayernIdValue;

    BayernIdAccessLevel(String scopeValue, String bayernIdValue) {
        this.scopeValue = scopeValue;
        this.bayernIdValue = bayernIdValue;
    }

    public String getBayernIdValue() {
        return bayernIdValue;
    }

    public static Optional<BayernIdAccessLevel> fromScopeValue(String scopeValue) {
        for (BayernIdAccessLevel bayernIdAccessLevel : BayernIdAccessLevel.values()) {
            if (bayernIdAccessLevel.scopeValue.equals(scopeValue)) {
                return Optional.of(bayernIdAccessLevel);
            }
        }
        return Optional.empty();
    }
}
