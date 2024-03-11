package de.aivot.egov.dataport.broker.dataport;

import de.aivot.egov.dataport.broker.dataport.services.DataportAuthnRequestProcessor;
import de.aivot.egov.dataport.providers.DataportConfigProvider;
import de.aivot.egov.dataport.providers.DataportConfigProviderFactory;
import de.aivot.egov.providers.EgovConfigProvider;
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

public class DataportSamlAuthenticationPreprocessor implements SamlAuthenticationPreprocessor {
    private final static Logger logger = Logger.getLogger(DataportSamlAuthenticationPreprocessor.class);
    private final DataportAuthnRequestProcessor dataportAuthnRequestProcessor = new DataportAuthnRequestProcessor();
    private DataportConfigProvider dataportConfigProvider;

    @Override
    public SamlAuthenticationPreprocessor create(KeycloakSession session) {
        var provider = session
                .getProvider(
                        EgovConfigProvider.class,
                        DataportConfigProviderFactory.PROVIDER_ID
                );
        if (provider instanceof DataportConfigProvider) {
            dataportConfigProvider = (DataportConfigProvider) provider;
        }
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
        return "dataport-saml-preprocessor";
    }

    @Override
    public AuthnRequestType beforeProcessingLoginRequest(AuthnRequestType authnRequest, AuthenticationSessionModel authSession) {
        if (isThisPreprocessorResponsible(authnRequest)) {
            logger.info("Before processing Dataport login request");
        }
        return SamlAuthenticationPreprocessor.super.beforeProcessingLoginRequest(authnRequest, authSession);
    }

    @Override
    public LogoutRequestType beforeProcessingLogoutRequest(LogoutRequestType logoutRequest, UserSessionModel authSession, AuthenticatedClientSessionModel clientSession) {
        if (isThisPreprocessorResponsible(logoutRequest)) {
            logger.info("Before processing Dataport logout request");
        }
        return SamlAuthenticationPreprocessor.super.beforeProcessingLogoutRequest(logoutRequest, authSession, clientSession);
    }

    @Override
    public AuthnRequestType beforeSendingLoginRequest(AuthnRequestType authnRequest, AuthenticationSessionModel clientSession) {
        var updatedAuthnRequest = authnRequest;
        if (isThisPreprocessorResponsible(authnRequest)) {
            logger.info("Before sending Dataport login request");
            updatedAuthnRequest = dataportAuthnRequestProcessor.processBeforeSendingLoginRequest(authnRequest, clientSession);
        }
        return SamlAuthenticationPreprocessor.super.beforeSendingLoginRequest(updatedAuthnRequest, clientSession);
    }

    @Override
    public LogoutRequestType beforeSendingLogoutRequest(LogoutRequestType logoutRequest, UserSessionModel authSession, AuthenticatedClientSessionModel clientSession) {
        if (isThisPreprocessorResponsible(logoutRequest)) {
            logger.info("Before sending Dataport logout request");
        }
        return SamlAuthenticationPreprocessor.super.beforeSendingLogoutRequest(logoutRequest, authSession, clientSession);
    }

    @Override
    public StatusResponseType beforeProcessingLoginResponse(StatusResponseType statusResponse, AuthenticationSessionModel authSession) {
        if (isThisPreprocessorResponsible(statusResponse)) {
            logger.info("Before processing Dataport login response");
        }
        return SamlAuthenticationPreprocessor.super.beforeProcessingLoginResponse(statusResponse, authSession);
    }

    @Override
    public StatusResponseType beforeSendingResponse(StatusResponseType statusResponse, AuthenticatedClientSessionModel clientSession) {
        if (isThisPreprocessorResponsible(statusResponse)) {
            logger.info("Before sending Dataport login response");
        }
        return SamlAuthenticationPreprocessor.super.beforeSendingResponse(statusResponse, clientSession);
    }

    private boolean isThisPreprocessorResponsible(AuthnRequestType authnRequest) {
        if (dataportConfigProvider != null && !dataportConfigProvider.isEnabled()) {
            return false;
        }
        String host = authnRequest.getDestination().getHost();
        return isHostDataportHost(host);
    }

    private boolean isThisPreprocessorResponsible(LogoutRequestType logoutRequest) {
        if (dataportConfigProvider != null && !dataportConfigProvider.isEnabled()) {
            return false;
        }
        String host = logoutRequest.getDestination().getHost();
        return isHostDataportHost(host);
    }

    private boolean isThisPreprocessorResponsible(StatusResponseType logoutRequest) {
        if (dataportConfigProvider != null && !dataportConfigProvider.isEnabled()) {
            return false;
        }
        URI destination;
        try {
            destination = URI.create(logoutRequest.getDestination());
        } catch (IllegalArgumentException e) {
            logger.error("Invalid Dataport destination URI: " + logoutRequest.getDestination(), e);
            return false;
        }
        logger.info("Dataport StatusResponseType Destination: " + destination);
        String host = destination.getHost();
        return isHostDataportHost(host);
    }

    private boolean isHostDataportHost(String host) {
        return host != null && (host.equalsIgnoreCase("idp.serviceportal.bremen.de")
                || host.equalsIgnoreCase("idp.serviceportal-stage.bremen.de")
                || host.equalsIgnoreCase("idp.serviceportal.hamburg.de")
                || host.equalsIgnoreCase("idp.serviceportal-stage.hamburg.de")
                || host.equalsIgnoreCase("idp.serviceportal.schleswig-holstein.de")
                || host.equalsIgnoreCase("idp.serviceportal-stage.schleswig-holstein.de")
        );
    }
}
