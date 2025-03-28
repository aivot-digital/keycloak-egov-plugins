package de.aivot.egov.dataport.providers;

import de.aivot.egov.providers.EgovConfigProvider;
import de.aivot.egov.providers.EgovConfigProviderFactory;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

import java.util.LinkedHashMap;
import java.util.Map;

public class DataportConfigProviderFactory implements EgovConfigProviderFactory {
    public static String PROVIDER_ID = "dataport";
    private DataportConfigProvider config;

    @Override
    public EgovConfigProvider create(KeycloakSession session) {
        return config;
    }

    @Override
    public void init(Config.Scope config) {
        this.config = new DataportConfigProvider(config);
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {

    }

    @Override
    public void close() {

    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public Map<String, String> getOperationalInfo() {
        return new LinkedHashMap<String, String>();
    }
}
