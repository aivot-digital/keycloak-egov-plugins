package de.aivot.egov.bundid.providers;

import de.aivot.egov.providers.EgovConfigProvider;
import org.keycloak.Config;

public class BundIdConfigProvider implements EgovConfigProvider {
    private final String bmiId;
    private final String displayName;

    public BundIdConfigProvider(Config.Scope config) {
        bmiId = config.get("bmi-id", "BMI-X0000");
        displayName = config.get("display-name", "Unbenanntes Fachverfahren");
    }

    @Override
    public void close() {

    }

    public String getBmiId() {
        return bmiId;
    }

    public String getDisplayName() {
        return displayName;
    }
}
