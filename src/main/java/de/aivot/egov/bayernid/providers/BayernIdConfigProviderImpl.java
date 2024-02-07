package de.aivot.egov.bayernid.providers;

import de.aivot.egov.providers.EgovConfigProvider;
import org.keycloak.models.KeycloakSession;

public class BayernIdConfigProviderImpl implements EgovConfigProvider {
    public BayernIdConfigProviderImpl(KeycloakSession sessions) {
    }

    @Override
    public void close() {

    }
}
