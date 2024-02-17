package de.aivot.egov.bayernid.providers;

import de.aivot.egov.providers.EgovConfigProvider;
import de.aivot.egov.providers.EgovConfigProviderFactory;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

import java.util.LinkedHashMap;
import java.util.Map;

public class BayernIdConfigProviderFactory implements EgovConfigProviderFactory {
    public static String PROVIDER_ID = "bayernid";
    private BayernIdConfigProvider config;

    @Override
    public EgovConfigProvider create(KeycloakSession session) {
        return config;
    }

    @Override
    public void init(Config.Scope config) {
        this.config = new BayernIdConfigProvider(config);
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

        ret.put("Aktiv", config.isEnabled() ? "Ja" : "Nein");

        ret.put("Service Provider Name", config.getSpName());
        ret.put("Service Provider Beschreibung", config.getSpDescription());

        ret.put("Organisationsname", config.getOrgName());
        ret.put("Organisationsbeschreibung", config.getOrgDescription());
        ret.put("Organisationswebseite", config.getOrgUrl());

        ret.put("Name des technischen Kontakts", config.getTechnicalContactName());
        ret.put("E-Mail-Adresse des technischen Kontakts", config.getTechnicalContactEmail());
        ret.put("Name des support Kontakts", config.getSupportContactName());
        ret.put("E-Mail-Adresse des support Kontakts", config.getSupportContactEmail());

        ret.put("Anzeigenahme des Fachverfahrens", config.getDisplayName());
        ret.put("Beschreibung des Fachverfahrens", config.getDisplayDescription());

        return ret;
    }
}
