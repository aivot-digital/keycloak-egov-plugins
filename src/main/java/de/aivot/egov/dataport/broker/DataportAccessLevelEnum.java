package de.aivot.egov.dataport.broker;

import de.aivot.egov.shared.broker.EGovAuthnContextLevelEnum;

import javax.annotation.Nonnull;
import java.util.Optional;

public enum DataportAccessLevelEnum implements EGovAuthnContextLevelEnum {
    STORK_QAA_LEVEL_1("level1", "http://eidas.europa.eu/LoA/low"),
    STORK_QAA_LEVEL_3("level3", "http://eidas.europa.eu/LoA/substantial"),
    STORK_QAA_LEVEL_4("level4", "http://eidas.europa.eu/LoA/high");

    private final String scopeValue;
    private final String dataportLevel;

    DataportAccessLevelEnum(String scopeValue, String dataportLevel) {
        this.scopeValue = scopeValue;
        this.dataportLevel = dataportLevel;
    }

    public String getDataportLevel() {
        return dataportLevel;
    }

    public static Optional<DataportAccessLevelEnum> fromScopeValue(String scopeValue) {
        for (DataportAccessLevelEnum dataportAccessLevel : DataportAccessLevelEnum.values()) {
            if (dataportAccessLevel.scopeValue.equals(scopeValue)) {
                return Optional.of(dataportAccessLevel);
            }
        }
        return Optional.empty();
    }

    @Nonnull
    @Override
    public String getScopeKey() {
        return scopeValue;
    }

    @Nonnull
    @Override
    public String getAuthnContextClassRef() {
        return dataportLevel;
    }
}
