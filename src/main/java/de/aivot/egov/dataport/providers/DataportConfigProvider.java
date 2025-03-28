package de.aivot.egov.dataport.providers;

import de.aivot.egov.providers.EgovConfigProvider;
import org.keycloak.Config;

public class DataportConfigProvider implements EgovConfigProvider {

    public DataportConfigProvider(Config.Scope config) {
    }

    @Override
    public void close() {

    }
}
