package de.aivot.egov.bundid.broker.bundid.enums;

import java.util.Optional;

public enum BundIdAccessLevel {
    STORK_QAA_LEVEL_1("bundid_level_1", "STORK-QAA-Level-1"),
    STORK_QAA_LEVEL_2("bundid_level_2", "STORK-QAA-Level-2"),
    STORK_QAA_LEVEL_3("bundid_level_3", "STORK-QAA-Level-3"),
    STORK_QAA_LEVEL_4("bundid_level_4", "STORK-QAA-Level-4");

    private final String scopeValue;
    private final String bundIdValue;

    BundIdAccessLevel(String scopeValue, String bundIdValue) {
        this.scopeValue = scopeValue;
        this.bundIdValue = bundIdValue;
    }

    public String getBundIdValue() {
        return bundIdValue;
    }

    public static Optional<BundIdAccessLevel> fromScopeValue(String scopeValue) {
        for (BundIdAccessLevel bundIdAccessLevel : BundIdAccessLevel.values()) {
            if (bundIdAccessLevel.scopeValue.equals(scopeValue)) {
                return Optional.of(bundIdAccessLevel);
            }
        }
        return Optional.empty();
    }
}
