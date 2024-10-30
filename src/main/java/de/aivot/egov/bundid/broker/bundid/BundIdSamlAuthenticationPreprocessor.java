package de.aivot.egov.bundid.broker.bundid;

import de.aivot.egov.bundid.broker.bundid.services.BundIdAuthnRequestProcessor;
import de.aivot.egov.bundid.providers.BundIdConfigProvider;
import de.aivot.egov.bundid.providers.BundIdConfigProviderFactory;
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

public class BundIdSamlAuthenticationPreprocessor implements SamlAuthenticationPreprocessor {
    private final static Logger logger = Logger.getLogger(BundIdSamlAuthenticationPreprocessor.class);
    private final BundIdAuthnRequestProcessor bundIdAuthnRequestProcessor = new BundIdAuthnRequestProcessor();
    private BundIdConfigProvider bundIdConfigProvider;

    @Override
    public SamlAuthenticationPreprocessor create(KeycloakSession session) {
        var provider = session
                .getProvider(
                        EgovConfigProvider.class,
                        BundIdConfigProviderFactory.PROVIDER_ID
                );
        if (provider instanceof BundIdConfigProvider) {
            bundIdConfigProvider = (BundIdConfigProvider) provider;
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
        return "bundid-saml-preprocessor";
    }

    @Override
    public AuthnRequestType beforeProcessingLoginRequest(AuthnRequestType authnRequest, AuthenticationSessionModel authSession) {
        if (isThisPreprocessorResponsible(authnRequest)) {
            logger.info("Before processing BundID login request");
        }
        return SamlAuthenticationPreprocessor.super.beforeProcessingLoginRequest(authnRequest, authSession);
    }

    @Override
    public LogoutRequestType beforeProcessingLogoutRequest(LogoutRequestType logoutRequest, UserSessionModel authSession, AuthenticatedClientSessionModel clientSession) {
        if (isThisPreprocessorResponsible(logoutRequest)) {
            logger.info("Before processing BundID logout request");
        }
        return SamlAuthenticationPreprocessor.super.beforeProcessingLogoutRequest(logoutRequest, authSession, clientSession);
    }

    @Override
    public AuthnRequestType beforeSendingLoginRequest(AuthnRequestType authnRequest, AuthenticationSessionModel clientSession) {
        var updatedAuthnRequest = authnRequest;
        if (isThisPreprocessorResponsible(authnRequest)) {
            logger.info("Before sending BundID login request");
            updatedAuthnRequest = bundIdAuthnRequestProcessor.processBeforeSendingLoginRequest(authnRequest, clientSession);
        }
        return SamlAuthenticationPreprocessor.super.beforeSendingLoginRequest(updatedAuthnRequest, clientSession);
    }

    @Override
    public LogoutRequestType beforeSendingLogoutRequest(LogoutRequestType logoutRequest, UserSessionModel authSession, AuthenticatedClientSessionModel clientSession) {
        if (isThisPreprocessorResponsible(logoutRequest)) {
            logger.info("Before sending BundID logout request");
        }
        return SamlAuthenticationPreprocessor.super.beforeSendingLogoutRequest(logoutRequest, authSession, clientSession);
    }

    @Override
    public StatusResponseType beforeProcessingLoginResponse(StatusResponseType statusResponse, AuthenticationSessionModel authSession) {
        if (isThisPreprocessorResponsible(statusResponse)) {
            logger.info("Before processing BundID login response");
        }
        return SamlAuthenticationPreprocessor.super.beforeProcessingLoginResponse(statusResponse, authSession);
    }

    @Override
    public StatusResponseType beforeSendingResponse(StatusResponseType statusResponse, AuthenticatedClientSessionModel clientSession) {
        if (isThisPreprocessorResponsible(statusResponse)) {
            logger.info("Before sending BundID login response");
        }
        return SamlAuthenticationPreprocessor.super.beforeSendingResponse(statusResponse, clientSession);
    }

    private boolean isThisPreprocessorResponsible(AuthnRequestType authnRequest) {
        if (bundIdConfigProvider != null && !bundIdConfigProvider.isEnabled()) {
            return false;
        }
        String host = authnRequest.getDestination().getHost();
        return isHostBundIdHost(host);
    }

    private boolean isThisPreprocessorResponsible(LogoutRequestType logoutRequest) {
        if (bundIdConfigProvider != null && !bundIdConfigProvider.isEnabled()) {
            return false;
        }
        String host = logoutRequest.getDestination().getHost();
        return isHostBundIdHost(host);
    }

    private boolean isThisPreprocessorResponsible(StatusResponseType logoutRequest) {
        if (bundIdConfigProvider != null && !bundIdConfigProvider.isEnabled()) {
            return false;
        }
        URI destination;
        try {
            destination = URI.create(logoutRequest.getDestination());
        } catch (IllegalArgumentException e) {
            logger.error("Invalid BundID destination URI: " + logoutRequest.getDestination(), e);
            return false;
        }
        logger.info("BundID StatusResponseType Destination: " + destination);
        String host = destination.getHost();
        return isHostBundIdHost(host);
    }

    private boolean isHostBundIdHost(String host) {
        return host != null && (host.equalsIgnoreCase("id.bund.de") || host.equalsIgnoreCase("int.id.bund.de"));
    }
}
