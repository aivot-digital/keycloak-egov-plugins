package de.aivot.egov.bundid.providers;

import de.aivot.egov.providers.EgovConfigProvider;
import de.aivot.egov.providers.EgovConfigProviderFactory;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class BundIdConfigProviderFactory implements EgovConfigProviderFactory {
    public static String PROVIDER_ID = "bundid";
    private BundIdConfigProvider config;

    @Override
    public EgovConfigProvider create(KeycloakSession session) {
        return config;
    }

    @Override
    public void init(Config.Scope config) {
        this.config = new BundIdConfigProvider(config);
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
        var ret = new LinkedHashMap<String, String>();

        ret.put("BMI ID", config.getBmiId());
        ret.put("Anzeigename", config.getDisplayName());

        return ret;
    }
}
