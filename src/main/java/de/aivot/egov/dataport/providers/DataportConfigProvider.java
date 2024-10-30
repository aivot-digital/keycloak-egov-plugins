package de.aivot.egov.dataport.providers;

import de.aivot.egov.providers.EgovConfigProvider;
import org.keycloak.Config;

public class DataportConfigProvider implements EgovConfigProvider {
    private final Boolean isEnabled;

    public DataportConfigProvider(Config.Scope config) {
        isEnabled = config.getBoolean("enabled", true);
    }

    @Override
    public void close() {

    }

    public Boolean isEnabled() {
        return isEnabled;
    }
}
