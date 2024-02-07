package de.aivot.egov.providers;

import org.keycloak.provider.ProviderFactory;
import org.keycloak.provider.ServerInfoAwareProviderFactory;

public interface EgovConfigProviderFactory extends ProviderFactory<EgovConfigProvider>, ServerInfoAwareProviderFactory {

}
