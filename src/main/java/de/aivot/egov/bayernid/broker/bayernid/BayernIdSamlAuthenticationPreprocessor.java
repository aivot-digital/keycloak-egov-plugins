package de.aivot.egov.bayernid.broker.bayernid;

import de.aivot.egov.bayernid.broker.bayernid.services.BayernAuthnRequestProcessor;
import de.aivot.egov.bayernid.providers.BayernIdConfigProvider;
import de.aivot.egov.bayernid.providers.BayernIdConfigProviderFactory;
import de.aivot.egov.providers.EgovConfigProvider;
import org.keycloak.Config;
import org.keycloak.dom.saml.v2.protocol.AuthnRequestType;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.protocol.saml.preprocessor.SamlAuthenticationPreprocessor;
import org.keycloak.sessions.AuthenticationSessionModel;

import java.util.List;

public class BayernIdSamlAuthenticationPreprocessor implements SamlAuthenticationPreprocessor {
    /**
     * A list of hostnames that are considered as BayernID hosts.
     * This list is checked, to determine if the current request is a BayernID request and needs modification.
     */
    private final static List<String> BayernIdHostList = List.of(
            "id.bayernportal.de",
            "pre-id.bayernportal.de",
            "infra-pre-id.bayernportal.de"
    );

    /**
     * The ID of this preprocessor.
     */
    private final static String ID = "bayernid-saml-preprocessor";

    /**
     * The config provider for this preprocessor.
     */
    private BayernIdConfigProvider bayernIdConfig;

    @Override
    public SamlAuthenticationPreprocessor create(KeycloakSession session) {
        // Extract the BayernID config provider from the session on creation.
        var provider = session
                .getProvider(
                        EgovConfigProvider.class,
                        BayernIdConfigProviderFactory.PROVIDER_ID
                );
        if (provider instanceof BayernIdConfigProvider configProvider) {
            bayernIdConfig = configProvider;
        }

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
        // Check if this preprocessor is responsible for the request.
        if (bayernIdConfig != null && bayernIdConfig.getIsEnabled() && isHostEnabled(authnRequest)) {
            var updatedAuthnRequest = BayernAuthnRequestProcessor
                    .processBeforeSendingLoginRequest(
                            authnRequest,
                            clientSession,
                            bayernIdConfig
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
        return BayernIdHostList.contains(host);
    }
}
