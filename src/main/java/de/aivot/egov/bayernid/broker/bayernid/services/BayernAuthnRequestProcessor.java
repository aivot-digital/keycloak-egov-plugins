package de.aivot.egov.bayernid.broker.bayernid.services;

import de.aivot.egov.bayernid.broker.bayernid.enums.BayernIdAccessLevel;
import de.aivot.egov.bayernid.broker.bayernid.generators.BayernIdAuthenticationRequestExtensionGenerator;
import org.jboss.logging.Logger;
import org.keycloak.dom.saml.v2.protocol.AuthnContextComparisonType;
import org.keycloak.dom.saml.v2.protocol.AuthnRequestType;
import org.keycloak.dom.saml.v2.protocol.ExtensionsType;
import org.keycloak.dom.saml.v2.protocol.RequestedAuthnContextType;
import org.keycloak.sessions.AuthenticationSessionModel;

import java.util.Optional;

public class BayernAuthnRequestProcessor {
    private final static Logger logger = Logger.getLogger(BayernAuthnRequestProcessor.class);

    public AuthnRequestType processBeforeSendingLoginRequest(AuthnRequestType authnRequest, AuthenticationSessionModel clientSession) {
        var accessLevel = clientSession
                .getClient()
                .getClientScopes(true)
                .keySet()
                .stream()
                .peek(scopeKey -> logger.info("Checking Scope: " + scopeKey))
                .map(BayernIdAccessLevel::fromScopeValue)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst()
                .orElseGet(() -> {
                    logger.warn("No access level found for client, defaulting to STORK_QAA_LEVEL_1");
                    return BayernIdAccessLevel.STORK_QAA_LEVEL_1;
                });

        var authnContext = new RequestedAuthnContextType();
        authnContext.addAuthnContextClassRef(accessLevel.getBayernIdValue());
        authnContext.setComparison(AuthnContextComparisonType.MINIMUM);
        authnRequest.setRequestedAuthnContext(authnContext);

        ExtensionsType extension = authnRequest.getExtensions();
        if (extension == null) {
            extension = new ExtensionsType();
            authnRequest.setExtensions(extension);
        }

        extension.addExtension(new BayernIdAuthenticationRequestExtensionGenerator());

        return authnRequest;
    }
}
