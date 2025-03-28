package de.aivot.egov.bundid.broker.bundid;

import de.aivot.egov.bundid.broker.bundid.services.BundIdAuthnRequestProcessor;
import de.aivot.egov.bundid.providers.BundIdConfigProvider;
import de.aivot.egov.bundid.providers.BundIdConfigProviderFactory;
import de.aivot.egov.providers.EgovConfigProvider;
import org.keycloak.Config;
import org.keycloak.dom.saml.v2.protocol.AuthnRequestType;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.protocol.saml.preprocessor.SamlAuthenticationPreprocessor;
import org.keycloak.services.resources.KeycloakApplication;
import org.keycloak.sessions.AuthenticationSessionModel;

import java.util.List;

public class BundIdSamlAuthenticationPreprocessor implements SamlAuthenticationPreprocessor {
    /**
     * A list of hostnames that are considered as BundID hosts.
     * This list is checked, to determine if the current request is a BundID request and needs modification.
     */
    private final static List<String> BundIdHostList = List.of(
            "id.bund.de",
            "int.id.bund.de"
    );

    /**s
     * The ID of this preprocessor.
     */
    private final static String ID = "bundid-saml-preprocessor";

    @Override
    public SamlAuthenticationPreprocessor create(KeycloakSession session) {
        return this;
    }

    @Override
    public void init(Config.Scope config) {
        // Do nothing here
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // Do nothing here
    }

    @Override
    public void close() {
        // Do nothing here
    }

    @Override
    public String getId() {
        return ID;
    }


    @Override
    public AuthnRequestType beforeSendingLoginRequest(AuthnRequestType authnRequest, AuthenticationSessionModel clientSession) {
        // Extract the BundID config provider from the session on creation.
        BundIdConfigProvider configProvider;
        try (var session = KeycloakApplication.getSessionFactory().create()) {
            configProvider = (BundIdConfigProvider) session
                    .getProvider(
                            EgovConfigProvider.class,
                            BundIdConfigProviderFactory.PROVIDER_ID
                    );
        }

        // Check if this preprocessor is responsible for the request.
        if (configProvider != null && configProvider.getIsEnabled() && isHostEnabled(authnRequest)) {
            var updatedAuthnRequest = BundIdAuthnRequestProcessor
                    .processBeforeSendingLoginRequest(
                            authnRequest,
                            clientSession,
                            configProvider
                    );
            return SamlAuthenticationPreprocessor.super.beforeSendingLoginRequest(updatedAuthnRequest, clientSession);
        } else {
            return SamlAuthenticationPreprocessor.super.beforeSendingLoginRequest(authnRequest, clientSession);
        }
    }

    /**
     * Determines if this preprocessor is responsible for the given request.
     *
     * @param authnRequest The request to check.
     * @return True if this preprocessor is responsible for the request, false otherwise.
     */
    private boolean isHostEnabled(AuthnRequestType authnRequest) {
        var host = authnRequest.getDestination().getHost();
        if (host == null) {
            return false;
        }
        return BundIdHostList.contains(host);
    }
}
