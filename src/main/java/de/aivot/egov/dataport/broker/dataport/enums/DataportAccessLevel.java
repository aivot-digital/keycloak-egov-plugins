package de.aivot.egov.dataport.broker.dataport.enums;

import java.util.Optional;

public enum DataportAccessLevel {
    STORK_QAA_LEVEL_1("level1", "http://eidas.europa.eu/LoA/low"),
    STORK_QAA_LEVEL_3("level3", "http://eidas.europa.eu/LoA/substantial"),
    STORK_QAA_LEVEL_4("level4", "http://eidas.europa.eu/LoA/high");

    private final String scopeValue;
    private final String dataportLevel;

    DataportAccessLevel(String scopeValue, String dataportLevel) {
        this.scopeValue = scopeValue;
        this.dataportLevel = dataportLevel;
    }

    public String getDataportLevel() {
        return dataportLevel;
    }

    public static Optional<DataportAccessLevel> fromScopeValue(String scopeValue) {
        for (DataportAccessLevel dataportAccessLevel : DataportAccessLevel.values()) {
            if (dataportAccessLevel.scopeValue.equals(scopeValue)) {
                return Optional.of(dataportAccessLevel);
            }
        }
        return Optional.empty();
    }
}
