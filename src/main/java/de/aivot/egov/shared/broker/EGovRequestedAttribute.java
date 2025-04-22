package de.aivot.egov.shared.broker;

import org.keycloak.broker.saml.mappers.XPathAttributeMapper;
import org.keycloak.models.IdentityProviderMapperModel;

import jakarta.annotation.Nonnull;

/**
 * A model that represents a requested attribute from an identity provider.
 * These are passend to the request extension generator to include them in the SAML request.
 *
 * @param name         The name of the attribute.
 * @param friendlyName The friendly name of the attribute.
 */
public record EGovRequestedAttribute(
        @Nonnull
        String name,
        @Nonnull
        String friendlyName
) {
    /**
     * Create a new requested attribute based on an identity provider mapper model
     *
     * @param model The identity provider mapper model that contains the requested attribute.
     * @return The requested attribute.
     */
    public static EGovRequestedAttribute fromIdentityProviderMapperModel(IdentityProviderMapperModel model) {
        var config = model
                .getConfig();

        var name = config
                .get(XPathAttributeMapper.ATTRIBUTE_NAME);

        var friendlyName = config
                .get(XPathAttributeMapper.ATTRIBUTE_FRIENDLY_NAME);

        return new EGovRequestedAttribute(name, friendlyName);
    }
}
