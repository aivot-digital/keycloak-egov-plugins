package de.aivot.egov.bayernid.broker;

import de.aivot.egov.shared.broker.EGovAuthnContextLevelEnum;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * These are the access levels that can be requested from BayernID.
 * They are used to determine the level of assurance that is required for the authentication.
 * An access level is determined by the given scope during the authentication request.
 * The corresponding BayernID value is then inserted into the SAML request.
 */
public enum BayernIdAccessLevelEnum implements EGovAuthnContextLevelEnum {
    STORK_QAA_LEVEL_1("level1", "STORK-QAA-Level-1"),
    STORK_QAA_LEVEL_3("level3", "STORK-QAA-Level-3"),
    STORK_QAA_LEVEL_4("level4", "STORK-QAA-Level-4");

    @Nonnull
    private final String scopeValue;
    @Nonnull
    private final String bayernIdValue;

    BayernIdAccessLevelEnum(
            @Nonnull String scopeValue,
            @Nonnull String bayernIdValue
    ) {
        this.scopeValue = scopeValue;
        this.bayernIdValue = bayernIdValue;
    }

    /**
     * Get the BayernID value for the SAML request.
     */
    @Nonnull
    public String getBayernIdValue() {
        return bayernIdValue;
    }

    /**
     * Try to determine the corresponding BayernIdAccessLevel from the given scope value.
     * If no corresponding BayernIdAccessLevel is found, an empty Optional is returned.
     *
     * @param scopeValue The scope value that is used to determine the access level.
     * @return The corresponding BayernIdAccessLevel if it exists.
     */
    @Nonnull
    public static Optional<BayernIdAccessLevelEnum> fromScopeValue(String scopeValue) {
        for (BayernIdAccessLevelEnum bayernIdAccessLevel : BayernIdAccessLevelEnum.values()) {
            if (bayernIdAccessLevel.scopeValue.equals(scopeValue)) {
                return Optional.of(bayernIdAccessLevel);
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
        return bayernIdValue;
    }
}
