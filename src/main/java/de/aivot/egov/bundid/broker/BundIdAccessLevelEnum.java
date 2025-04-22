package de.aivot.egov.bundid.broker;

import de.aivot.egov.shared.broker.EGovAuthnContextLevelEnum;

import jakarta.annotation.Nonnull;
import java.util.Optional;

/**
 * These are the access levels that can be requested from BundID.
 * They are used to determine the level of assurance that is required for the authentication.
 * An access level is determined by the given scope during the authentication request.
 * The corresponding BundID value is then inserted into the SAML request.
 */
public enum BundIdAccessLevelEnum implements EGovAuthnContextLevelEnum {
    STORK_QAA_LEVEL_1("level1", "STORK-QAA-Level-1"),
    STORK_QAA_LEVEL_3("level3", "STORK-QAA-Level-3"),
    STORK_QAA_LEVEL_4("level4", "STORK-QAA-Level-4");

    @Nonnull
    private final String scopeValue;
    @Nonnull
    private final String bundIdValue;

    BundIdAccessLevelEnum(
            @Nonnull
            String scopeValue,
            @Nonnull
            String bundIdValue
    ) {
        this.scopeValue = scopeValue;
        this.bundIdValue = bundIdValue;
    }

    /**
     * Get the BundID value for the SAML request.
     */
    @Nonnull
    public String getBundIdValue() {
        return bundIdValue;
    }

    /**
     * Try to determine the corresponding BundIdAccessLevel from the given scope value.
     * If no corresponding BundIdAccessLevel is found, an empty Optional is returned.
     *
     * @param scopeValue The scope value that is used to determine the access level.
     * @return The corresponding BundIdAccessLevel if it exists.
     */
    public static Optional<BundIdAccessLevelEnum> fromScopeValue(String scopeValue) {
        for (BundIdAccessLevelEnum bundIdAccessLevel : BundIdAccessLevelEnum.values()) {
            if (bundIdAccessLevel.scopeValue.equals(scopeValue)) {
                return Optional.of(bundIdAccessLevel);
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
        return bundIdValue;
    }
}
