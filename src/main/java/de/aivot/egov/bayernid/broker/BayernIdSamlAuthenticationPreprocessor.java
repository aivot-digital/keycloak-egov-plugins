package de.aivot.egov.bayernid.broker;

import de.aivot.egov.bayernid.providers.BayernIdConfigProvider;
import de.aivot.egov.bayernid.providers.BayernIdConfigProviderFactory;
import de.aivot.egov.providers.EgovConfigProvider;
import de.aivot.egov.shared.broker.EGovAuthnContextLevelEnum;
import de.aivot.egov.shared.broker.EGovRequestedAttribute;
import de.aivot.egov.shared.broker.EGovPreprocessorBase;
import org.keycloak.models.KeycloakSession;
import org.keycloak.protocol.saml.preprocessor.SamlAuthenticationPreprocessor;
import org.keycloak.saml.SamlProtocolExtensionsAwareBuilder;

import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

public class BayernIdSamlAuthenticationPreprocessor extends EGovPreprocessorBase<BayernIdConfigProvider> {
    /**
     * A list of hostnames that are considered as BayernID hosts.
     * This list is checked, to determine if the current request is a BayernID request and needs modification.
     */
    private final static List<String> HostList = List.of(
            "id.bayernportal.de",
            "pre-id.bayernportal.de",
            "infra-pre-id.bayernportal.de"
    );

    /**
     * The ID of this preprocessor.
     */
    private final static String ID = "bayernid-saml-preprocessor";


    @Override
    public SamlAuthenticationPreprocessor create(KeycloakSession keycloakSession) {
        return this;
    }

    @Override
    public String getId() {
        return ID;
    }

    @Nonnull
    @Override
    protected BayernIdConfigProvider getConfigProvider(@Nonnull KeycloakSession session) {
        return (BayernIdConfigProvider) session.getProvider(
                EgovConfigProvider.class,
                BayernIdConfigProviderFactory.PROVIDER_ID
        );
    }

    @Nonnull
    @Override
    protected List<String> getTargetHosts() {
        return HostList;
    }

    @Nonnull
    @Override
    protected List<EGovRequestedAttribute> getRequestedAttributes(@Nonnull KeycloakSession session, @Nonnull String idpAlias) {
        // Get the requested attributes from the attribute mapper of the IDP
        return session
                .identityProviders()
                .getMappersByAliasStream(idpAlias)
                .map(EGovRequestedAttribute::fromIdentityProviderMapperModel)
                .toList();
    }

    @Nonnull
    @Override
    protected EGovAuthnContextLevelEnum[] getEGovAuthnContextLevelEnums() {
        return BayernIdAccessLevelEnum.values();
    }

    @Nonnull
    @Override
    protected EGovAuthnContextLevelEnum getDefaultEGovAuthnContextLevelEnum() {
        return BayernIdAccessLevelEnum.STORK_QAA_LEVEL_1;
    }

    @Nonnull
    @Override
    protected Optional<SamlProtocolExtensionsAwareBuilder.NodeGenerator> getNodeGenerator(BayernIdConfigProvider configProvider, List<EGovRequestedAttribute> requestedAttributes) {
        return Optional.of(new BayernIdAuthenticationRequestExtensionGenerator(
                requestedAttributes,
                configProvider
        ));
    }
}
