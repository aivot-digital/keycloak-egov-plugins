package de.aivot.egov.shared.broker;

import de.aivot.egov.providers.EgovConfigProvider;
import org.keycloak.Config;
import org.keycloak.OAuth2Constants;
import org.keycloak.dom.saml.v2.protocol.AuthnContextComparisonType;
import org.keycloak.dom.saml.v2.protocol.AuthnRequestType;
import org.keycloak.dom.saml.v2.protocol.ExtensionsType;
import org.keycloak.dom.saml.v2.protocol.RequestedAuthnContextType;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.protocol.saml.preprocessor.SamlAuthenticationPreprocessor;
import org.keycloak.saml.SamlProtocolExtensionsAwareBuilder;
import org.keycloak.services.resources.KeycloakApplication;
import org.keycloak.sessions.AuthenticationSessionModel;

import javax.annotation.Nonnull;
import java.net.URI;
import java.util.*;
import java.util.regex.Pattern;

public abstract class EGovPreprocessorBase<T extends EgovConfigProvider> implements SamlAuthenticationPreprocessor {
    private final static Pattern IdpAliasUriPattern = Pattern.compile("/realms/.+/broker/(.+)/endpoint");

    @Override
    public void init(Config.Scope config) {
        // Do nothing
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // Do nothing
    }

    @Override
    public void close() {
        // Do nothing
    }

    @Override
    public AuthnRequestType beforeSendingLoginRequest(AuthnRequestType authnRequest, AuthenticationSessionModel clientSession) {
        // Check if this preprocessor should be used for the request based on the target host.
        if (!checkHostMatches(authnRequest)) {
            return SamlAuthenticationPreprocessor.super.beforeSendingLoginRequest(authnRequest, clientSession);
        }

        // Extract the IDP alias from the sender URL to fetch the attribute mappers of the idp later in the process.
        var idpAlias = extractIdpAliasFromSenderUrl(authnRequest.getSenderURL());
        if (idpAlias.isEmpty()) {
            return SamlAuthenticationPreprocessor.super.beforeSendingLoginRequest(authnRequest, clientSession);
        }

        // Extract the config provider and the requested attributes from the active keycloak session.
        T configProvider;
        List<EGovRequestedAttribute> requestedAttributes;
        try (var session = KeycloakApplication.getSessionFactory().create()) {
            // Set the realm of the client session to the current session to prevent realm not initialized exception.
            session
                    .getContext()
                    .setRealm(clientSession.getRealm());

            configProvider = getConfigProvider(session);
            requestedAttributes = getRequestedAttributes(session, idpAlias.get());
        }

        // Set the requested authn context type based on the requested scopes and the list of available authn context levels.
        var requestedAuthnContextType = getRequestedAuthnContextType(clientSession);
        authnRequest.setRequestedAuthnContext(requestedAuthnContextType);

        // Create a new request extension generator with the requested attributes and the config provider.
        var requestExtensionGenerator = getNodeGenerator(configProvider, requestedAttributes);

        // Check if the request extension generator is present and add the extension to the AuthnRequest.
        if (requestExtensionGenerator.isPresent()) {
            // Get or create the extension for the AuthnRequest and add the request extension generator.
            var extension = authnRequest.getExtensions();
            if (extension == null) {
                extension = new ExtensionsType();
                authnRequest.setExtensions(extension);
            }
            extension.addExtension(requestExtensionGenerator.get());
        }

        // Process the login request with the new extension.
        return SamlAuthenticationPreprocessor.super.beforeSendingLoginRequest(authnRequest, clientSession);
    }

    /**
     * Get the requested authn context type based on the requested scopes and the list of available authn context levels.
     * The authn context level is determined by the first scope that matches the scope key of an authn context level.
     * If no scope matches, the default authn context level is used.
     *
     * @param clientSession The client session that contains the requested scopes.
     * @return The requested authn context type.
     */
    @Nonnull
    private RequestedAuthnContextType getRequestedAuthnContextType(@Nonnull AuthenticationSessionModel clientSession) {
        // Extract the scopes from the client session
        var scopeParam = clientSession.getClientNote(OAuth2Constants.SCOPE);
        var scopes = scopeParam != null ? Arrays.asList(scopeParam.split("\\s+")) : new LinkedList<String>();

        // Fetch a map of the authn context levels by their scope key.
        Map<String, EGovAuthnContextLevelEnum> enumMap = new HashMap<>();
        for (var level : getEGovAuthnContextLevelEnums()) {
            enumMap.put(level.getScopeKey(), level);
        }

        // Determine the access level based on the scopes. If no scope matches, the default access level is used.
        var accessLevel = scopes
                .stream()
                .map(enumMap::get)
                .filter(Objects::nonNull)
                .findFirst()
                .orElseGet(this::getDefaultEGovAuthnContextLevelEnum);

        // Create the requested authn context type with the access level.
        var authnContext = new RequestedAuthnContextType();
        authnContext.addAuthnContextClassRef(accessLevel.getAuthnContextClassRef());
        authnContext.setComparison(AuthnContextComparisonType.MINIMUM);

        return authnContext;
    }

    /**
     * Check if the host of the destination URL matches the target hosts.
     *
     * @param authnRequest The authn request that contains the destination URL.
     * @return True if the host matches, false otherwise.
     */
    private boolean checkHostMatches(@Nonnull AuthnRequestType authnRequest) {
        var host = authnRequest
                .getDestination()
                .getHost();

        if (host == null) {
            return false;
        }

        for (var targetHost : getTargetHosts()) {
            if (host.equalsIgnoreCase(targetHost)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Extract the IDP alias from the sender URL.
     *
     * @param senderURI The sender URL of the authn request.
     * @return The IDP alias if it is present in the sender URL, an empty optional otherwise.
     */
    @Nonnull
    private static Optional<String> extractIdpAliasFromSenderUrl(@Nonnull URI senderURI) {
        var matcher = IdpAliasUriPattern.matcher(senderURI.getPath());
        if (matcher.find()) {
            var idpAlias = matcher.group(1);
            return Optional.of(idpAlias);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Get the config provider from the active keycloak session.
     * The config provider contains data that is used to generate the request extension.
     * This config provider will be passed to the request extension generator.
     *
     * @param session The active keycloak session.
     * @return The config provider.
     */
    @Nonnull
    protected abstract T getConfigProvider(@Nonnull KeycloakSession session);

    /**
     * Get the list of target hosts.
     * If a request to one of these is made, the preprocessor is active and will generate a request extension.
     *
     * @return The list of target hosts.
     */
    @Nonnull
    protected abstract List<String> getTargetHosts();

    /**
     * Extract the requested attributes from the active keycloak session.
     * This should be used to extract the requested attributes from the mappers of the used identity provider.
     *
     * @param session The active keycloak session.
     * @return The list of requested attributes.
     */
    @Nonnull
    protected abstract List<EGovRequestedAttribute> getRequestedAttributes(@Nonnull KeycloakSession session, @Nonnull String idpAlias);

    /**
     * Get the list of available authn context levels.
     * This list is used to extract the requested authn context level from the requested scopes.
     *
     * @return The list of available authn context levels.
     */
    @Nonnull
    protected abstract EGovAuthnContextLevelEnum[] getEGovAuthnContextLevelEnums();

    /**
     * Get the default authn context level.
     * This level is used if no scope matches the available authn context levels.
     *
     * @return The default authn context level.
     */
    @Nonnull
    protected abstract EGovAuthnContextLevelEnum getDefaultEGovAuthnContextLevelEnum();

    /**
     * Get the node generator for the request extension.
     * This node generator is used to generate the request extension that is added to the SAML request.
     * The node generator is created based on the config provider and the requested attributes.
     * If no node generator is present, no extension is added to the SAML request.
     *
     * @param configProvider      The config provider that contains the data for the request extension.
     * @param requestedAttributes The requested attributes that are used to generate the request extension.
     * @return The node generator if it is present, an empty optional otherwise.
     */
    @Nonnull
    protected abstract Optional<SamlProtocolExtensionsAwareBuilder.NodeGenerator> getNodeGenerator(T configProvider, List<EGovRequestedAttribute> requestedAttributes);
}
