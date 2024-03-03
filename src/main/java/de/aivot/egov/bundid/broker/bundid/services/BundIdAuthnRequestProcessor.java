package de.aivot.egov.bundid.broker.bundid.services;

import de.aivot.egov.bundid.broker.bundid.enums.BundIdAccessLevel;
import de.aivot.egov.bundid.broker.bundid.generators.BundIdAuthenticationRequestExtensionGenerator;
import org.jboss.logging.Logger;
import org.keycloak.OAuth2Constants;
import org.keycloak.dom.saml.v2.protocol.AuthnContextComparisonType;
import org.keycloak.dom.saml.v2.protocol.AuthnRequestType;
import org.keycloak.dom.saml.v2.protocol.ExtensionsType;
import org.keycloak.dom.saml.v2.protocol.RequestedAuthnContextType;
import org.keycloak.sessions.AuthenticationSessionModel;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Optional;

public class BundIdAuthnRequestProcessor {
    private final static Logger logger = Logger.getLogger(BundIdAuthnRequestProcessor.class);

    public AuthnRequestType processBeforeSendingLoginRequest(AuthnRequestType authnRequest, AuthenticationSessionModel clientSession) {
        var scopeParam = clientSession.getClientNote(OAuth2Constants.SCOPE);
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

        var authnContext = new RequestedAuthnContextType();
        authnContext.addAuthnContextClassRef(accessLevel.getBundIdValue());
        authnContext.setComparison(AuthnContextComparisonType.MINIMUM);
        authnRequest.setRequestedAuthnContext(authnContext);

        ExtensionsType extension = authnRequest.getExtensions();
        if (extension == null) {
            extension = new ExtensionsType();
            authnRequest.setExtensions(extension);
        }

        extension.addExtension(new BundIdAuthenticationRequestExtensionGenerator());

        return authnRequest;
    }
}
