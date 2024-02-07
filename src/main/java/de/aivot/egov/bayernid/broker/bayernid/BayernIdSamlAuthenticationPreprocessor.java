package de.aivot.egov.bayernid.broker.bayernid;

import de.aivot.egov.bayernid.broker.bayernid.services.BayernAuthnRequestProcessor;
import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.dom.saml.v2.protocol.AuthnRequestType;
import org.keycloak.dom.saml.v2.protocol.LogoutRequestType;
import org.keycloak.dom.saml.v2.protocol.StatusResponseType;
import org.keycloak.models.AuthenticatedClientSessionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.UserSessionModel;
import org.keycloak.protocol.saml.preprocessor.SamlAuthenticationPreprocessor;
import org.keycloak.sessions.AuthenticationSessionModel;

import java.net.URI;

public class BayernIdSamlAuthenticationPreprocessor implements SamlAuthenticationPreprocessor {
    private final static Logger logger = Logger.getLogger(BayernIdSamlAuthenticationPreprocessor.class);
    private final BayernAuthnRequestProcessor bayernIdAuthnRequestProcessor = new BayernAuthnRequestProcessor();

    @Override
    public SamlAuthenticationPreprocessor create(KeycloakSession session) {
        return this;
    }

    @Override
    public void init(Config.Scope config) {
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
    }

    @Override
    public void close() {
    }

    @Override
    public String getId() {
        return "bayernid-saml-preprocessor";
    }

    @Override
    public AuthnRequestType beforeProcessingLoginRequest(AuthnRequestType authnRequest, AuthenticationSessionModel authSession) {
        if (isThisPreprocessorResponsible(authnRequest)) {
            logger.info("Before processing BayernID login request");
        }
        return SamlAuthenticationPreprocessor.super.beforeProcessingLoginRequest(authnRequest, authSession);
    }

    @Override
    public LogoutRequestType beforeProcessingLogoutRequest(LogoutRequestType logoutRequest, UserSessionModel authSession, AuthenticatedClientSessionModel clientSession) {
        if (isThisPreprocessorResponsible(logoutRequest)) {
            logger.info("Before processing BayernID logout request");
        }
        return SamlAuthenticationPreprocessor.super.beforeProcessingLogoutRequest(logoutRequest, authSession, clientSession);
    }

    @Override
    public AuthnRequestType beforeSendingLoginRequest(AuthnRequestType authnRequest, AuthenticationSessionModel clientSession) {
        var updatedAuthnRequest = authnRequest;
        if (isThisPreprocessorResponsible(authnRequest)) {
            logger.info("Before sending BayernID login request");
            updatedAuthnRequest = bayernIdAuthnRequestProcessor.processBeforeSendingLoginRequest(authnRequest, clientSession);
        }
        return SamlAuthenticationPreprocessor.super.beforeSendingLoginRequest(updatedAuthnRequest, clientSession);
    }

    @Override
    public LogoutRequestType beforeSendingLogoutRequest(LogoutRequestType logoutRequest, UserSessionModel authSession, AuthenticatedClientSessionModel clientSession) {
        if (isThisPreprocessorResponsible(logoutRequest)) {
            logger.info("Before sending BayernID logout request");
        }
        return SamlAuthenticationPreprocessor.super.beforeSendingLogoutRequest(logoutRequest, authSession, clientSession);
    }

    @Override
    public StatusResponseType beforeProcessingLoginResponse(StatusResponseType statusResponse, AuthenticationSessionModel authSession) {
        if (isThisPreprocessorResponsible(statusResponse)) {
            logger.info("Before processing BayernID login response");
        }
        return SamlAuthenticationPreprocessor.super.beforeProcessingLoginResponse(statusResponse, authSession);
    }

    @Override
    public StatusResponseType beforeSendingResponse(StatusResponseType statusResponse, AuthenticatedClientSessionModel clientSession) {
        if (isThisPreprocessorResponsible(statusResponse)) {
            logger.info("Before sending BayernID login response");
        }
        return SamlAuthenticationPreprocessor.super.beforeSendingResponse(statusResponse, clientSession);
    }

    private boolean isThisPreprocessorResponsible(AuthnRequestType authnRequest) {
        String host = authnRequest.getDestination().getHost();
        return isHostBayernIdHost(host);
    }

    private boolean isThisPreprocessorResponsible(LogoutRequestType logoutRequest) {
        String host = logoutRequest.getDestination().getHost();
        return isHostBayernIdHost(host);
    }

    private boolean isThisPreprocessorResponsible(StatusResponseType logoutRequest) {
        URI destination;
        try {
            destination = URI.create(logoutRequest.getDestination());
        } catch (IllegalArgumentException e) {
            logger.error("Invalid BayernID destination URI: " + logoutRequest.getDestination(), e);
            return false;
        }
        logger.info("BayernID StatusResponseType Destination: " + destination);
        String host = destination.getHost();
        return isHostBayernIdHost(host);
    }

    private boolean isHostBayernIdHost(String host) {
        return host != null && (host.equals("id.bayernportal.de") || host.equals("int.id.bayernportal.de")); // TODO: Host für Testumgebung anpassen
    }
}
