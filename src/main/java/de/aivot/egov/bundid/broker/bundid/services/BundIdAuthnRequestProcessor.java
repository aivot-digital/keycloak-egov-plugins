package de.aivot.egov.bundid.broker.bundid.services;

import de.aivot.egov.bundid.broker.bundid.enums.BundIdAccessLevel;
import de.aivot.egov.bundid.broker.bundid.generators.BundIdAuthenticationRequestExtensionGenerator;
import de.aivot.egov.bundid.providers.BundIdConfigProvider;
import de.aivot.egov.shared.models.RequestedAttribute;
import de.aivot.egov.shared.utils.IdpAliasUtils;
import org.jboss.logging.Logger;
import org.keycloak.OAuth2Constants;
import org.keycloak.dom.saml.v2.protocol.AuthnContextComparisonType;
import org.keycloak.dom.saml.v2.protocol.AuthnRequestType;
import org.keycloak.dom.saml.v2.protocol.ExtensionsType;
import org.keycloak.dom.saml.v2.protocol.RequestedAuthnContextType;
import org.keycloak.services.resources.KeycloakApplication;
import org.keycloak.sessions.AuthenticationSessionModel;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * This class is responsible for processing the AuthnRequest before sending it to BayernID.
 * It adds the requested access level to the AuthnRequest and adds the requested attributes to the extension.
 */
public class BundIdAuthnRequestProcessor {
    private final static Logger logger = Logger.getLogger(BundIdAuthnRequestProcessor.class);

    private final static String NameAttributeKey = "attribute.name";
    private final static String FriendlyNameAttributeKey = "attribute.friendly.name";

    public static AuthnRequestType processBeforeSendingLoginRequest(
            @Nonnull AuthnRequestType authnRequest,
            @Nonnull AuthenticationSessionModel clientSession,
            @Nonnull BundIdConfigProvider configProvider
    ) {
        // Extract the scope from the client session
        var scopeParam = clientSession
                .getClientNote(OAuth2Constants.SCOPE);
        var scopes = scopeParam != null ? Arrays.asList(scopeParam.split("\\s+")) : new LinkedList<String>();
        var accessLevel = scopes
                .stream()
                .peek(scopeKey -> logger.info("Checking Scope: " + scopeKey))
                .map(BundIdAccessLevel::fromScopeValue)
                .filter(Optional::isPresent)
                .map(java.util.Optional::get)
                .findFirst()
                .orElseGet(() -> {
                    logger.warn("No access level found for client, defaulting to STORK_QAA_LEVEL_1");
                    return BundIdAccessLevel.STORK_QAA_LEVEL_1;
                });

        // Add the requested access level to the AuthnRequest
        var authnContext = new RequestedAuthnContextType();
        authnContext.addAuthnContextClassRef(accessLevel.getBundIdValue());
        authnContext.setComparison(AuthnContextComparisonType.MINIMUM);
        authnRequest.setRequestedAuthnContext(authnContext);

        // Extract the requested attributes from the attribute mapper of the IDP from the session and realm
        List<RequestedAttribute> requestedAttributes;
        try (var session = KeycloakApplication.getSessionFactory().create()) {
            // Set the realm of the session to the realm of the client session to prevent issue with realm binding when using the session
            session
                    .getContext()
                    .setRealm(clientSession.getRealm());

            // Extract the IDP alias from the sender URL
            var idpAlias = IdpAliasUtils
                    .extractIdpAliasFromSenderUrl(authnRequest.getSenderURL());

            // Get the requested attributes from the attribute mapper of the IDP
            requestedAttributes = session
                    .identityProviders()
                    .getMappersByAliasStream(idpAlias)
                    .map(att -> {
                        var name = att.getConfig().get(NameAttributeKey);
                        var friendlyName = att.getConfig().get(FriendlyNameAttributeKey);
                        return new RequestedAttribute(name, friendlyName);
                    })
                    .toList();
        }

        // Create a new request extension generator with the requested attributes and the config provider
        var requestExtensionGenerator = new BundIdAuthenticationRequestExtensionGenerator(
                requestedAttributes,
                configProvider
        );

        // Get or create the extension for the AuthnRequest and add the request extension generator
        var extension = authnRequest.getExtensions();
        if (extension == null) {
            extension = new ExtensionsType();
            authnRequest.setExtensions(extension);
        }
        extension.addExtension(requestExtensionGenerator);

        return authnRequest;
    }
}
