package de.aivot.egov.dataport.broker.dataport.services;

import de.aivot.egov.dataport.broker.dataport.enums.DataportAccessLevel;
import org.jboss.logging.Logger;
import org.keycloak.OAuth2Constants;
import org.keycloak.dom.saml.v2.protocol.AuthnContextComparisonType;
import org.keycloak.dom.saml.v2.protocol.AuthnRequestType;
import org.keycloak.dom.saml.v2.protocol.RequestedAuthnContextType;
import org.keycloak.sessions.AuthenticationSessionModel;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Optional;

public class DataportAuthnRequestProcessor {
    private final static Logger logger = Logger.getLogger(DataportAuthnRequestProcessor.class);

    public AuthnRequestType processBeforeSendingLoginRequest(AuthnRequestType authnRequest, AuthenticationSessionModel clientSession) {
        var scopeParam = clientSession.getClientNote(OAuth2Constants.SCOPE);
        var scopes = scopeParam != null ? Arrays.asList(scopeParam.split("\\s+")) : new LinkedList<String>();

        var accessLevel = scopes
                .stream()
                .peek(scopeKey -> logger.info("Checking Scope: " + scopeKey))
                .map(DataportAccessLevel::fromScopeValue)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst()
                .orElseGet(() -> {
                    logger.warn("No access level found for client, defaulting to STORK_QAA_LEVEL_1");
                    return DataportAccessLevel.STORK_QAA_LEVEL_1;
                });

        var authnContext = new RequestedAuthnContextType();
        authnContext.addAuthnContextClassRef(accessLevel.getDataportLevel());
        authnContext.setComparison(AuthnContextComparisonType.MINIMUM);
        authnRequest.setRequestedAuthnContext(authnContext);

        return authnRequest;
    }
}
