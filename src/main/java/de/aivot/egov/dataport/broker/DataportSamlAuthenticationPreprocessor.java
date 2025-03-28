package de.aivot.egov.dataport.broker;

import de.aivot.egov.dataport.providers.DataportConfigProvider;
import de.aivot.egov.dataport.providers.DataportConfigProviderFactory;
import de.aivot.egov.providers.EgovConfigProvider;
import de.aivot.egov.shared.broker.EGovAuthnContextLevelEnum;
import de.aivot.egov.shared.broker.EGovPreprocessorBase;
import de.aivot.egov.shared.broker.EGovRequestedAttribute;
import org.keycloak.models.KeycloakSession;
import org.keycloak.protocol.saml.preprocessor.SamlAuthenticationPreprocessor;
import org.keycloak.saml.SamlProtocolExtensionsAwareBuilder;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

public class DataportSamlAuthenticationPreprocessor extends EGovPreprocessorBase<DataportConfigProvider> {
    private final static String ID = "dataport-saml-preprocessor";
    private final static List<String> HostList = List.of(
            "idp.serviceportal.bremen.de",
            "idp.serviceportal-stage.bremen.de",
            "idp.serviceportal.hamburg.de",
            "idp.serviceportal-stage.hamburg.de",
            "idp.serviceportal.schleswig-holstein.de",
            "idp.serviceportal-stage.schleswig-holstein.de"
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
    protected DataportConfigProvider getConfigProvider(@Nonnull KeycloakSession session) {
        return (DataportConfigProvider) session
                .getProvider(
                        EgovConfigProvider.class,
                        DataportConfigProviderFactory.PROVIDER_ID
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
        return List.of();
    }

    @Nonnull
    @Override
    protected EGovAuthnContextLevelEnum[] getEGovAuthnContextLevelEnums() {
        return DataportAccessLevelEnum.values();
    }

    @Nonnull
    @Override
    protected EGovAuthnContextLevelEnum getDefaultEGovAuthnContextLevelEnum() {
        return DataportAccessLevelEnum.STORK_QAA_LEVEL_1;
    }

    @Nonnull
    @Override
    protected Optional<SamlProtocolExtensionsAwareBuilder.NodeGenerator> getNodeGenerator(DataportConfigProvider configProvider, List<EGovRequestedAttribute> requestedAttributes) {
        return Optional.empty();
    }
}
