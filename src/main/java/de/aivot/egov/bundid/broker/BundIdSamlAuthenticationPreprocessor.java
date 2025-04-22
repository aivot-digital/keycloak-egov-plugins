package de.aivot.egov.bundid.broker;

import de.aivot.egov.bundid.providers.BundIdConfigProvider;
import de.aivot.egov.bundid.providers.BundIdConfigProviderFactory;
import de.aivot.egov.providers.EgovConfigProvider;
import de.aivot.egov.shared.broker.EGovAuthnContextLevelEnum;
import de.aivot.egov.shared.broker.EGovPreprocessorBase;
import de.aivot.egov.shared.broker.EGovRequestedAttribute;
import org.keycloak.models.KeycloakSession;
import org.keycloak.protocol.saml.preprocessor.SamlAuthenticationPreprocessor;
import org.keycloak.saml.SamlProtocolExtensionsAwareBuilder;

import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

public class BundIdSamlAuthenticationPreprocessor extends EGovPreprocessorBase<BundIdConfigProvider> {
    private final static String ID = "bundid-saml-preprocessor";
    private final static List<String> HostList = List.of(
            "id.bund.de",
            "int.id.bund.de"
    );

    @Override
    public SamlAuthenticationPreprocessor create(KeycloakSession session) {
        return this;
    }

    @Override
    public String getId() {
        return ID;
    }

    @Nonnull
    @Override
    protected BundIdConfigProvider getConfigProvider(@Nonnull KeycloakSession session) {
        return (BundIdConfigProvider) session
                .getProvider(
                        EgovConfigProvider.class,
                        BundIdConfigProviderFactory.PROVIDER_ID
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
        return session
                .identityProviders()
                .getMappersByAliasStream(idpAlias)
                .map(EGovRequestedAttribute::fromIdentityProviderMapperModel)
                .toList();
    }

    @Nonnull
    @Override
    protected EGovAuthnContextLevelEnum[] getEGovAuthnContextLevelEnums() {
        return BundIdAccessLevelEnum.values();
    }

    @Nonnull
    @Override
    protected EGovAuthnContextLevelEnum getDefaultEGovAuthnContextLevelEnum() {
        return BundIdAccessLevelEnum.STORK_QAA_LEVEL_1;
    }

    @Nonnull
    @Override
    protected Optional<SamlProtocolExtensionsAwareBuilder.NodeGenerator> getNodeGenerator(BundIdConfigProvider configProvider, List<EGovRequestedAttribute> requestedAttributes) {
        return Optional.of(new BundIdAuthenticationRequestExtensionGenerator(
                requestedAttributes,
                configProvider
        ));
    }
}
