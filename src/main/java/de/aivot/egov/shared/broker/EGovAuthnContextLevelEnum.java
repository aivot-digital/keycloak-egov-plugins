package de.aivot.egov.shared.broker;

import jakarta.annotation.Nonnull;

/**
 * An interface that is used to define the access levels that can be requested from an identity provider.
 * This interface should be implemented by an enum that defines the access levels.
 */
public interface EGovAuthnContextLevelEnum {
    /**
     * Get the scope key that is used to determine the access level used to build the SAML request.
     *
     * @return The scope key.
     */
    @Nonnull
    String getScopeKey();

    /**
     * Get the AuthnContextClassRef that is used as a value in the SAML request.
     *
     * @return The AuthnContextClassRef.
     */
    @Nonnull
    String getAuthnContextClassRef();
}
