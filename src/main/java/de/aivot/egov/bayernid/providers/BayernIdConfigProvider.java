package de.aivot.egov.bayernid.providers;

import de.aivot.egov.providers.EgovConfigProvider;
import org.keycloak.Config;

/**
 * Configuration provider for BayernID.
 * This contains all data that is needed to configure the interaction with the BayernID.
 */
public class BayernIdConfigProvider implements EgovConfigProvider {
    private final Boolean isEnabled;
    private final String spName;
    private final String spDescription;
    private final String orgName;
    private final String orgDescription;
    private final String orgUrl;
    private final String technicalContactName;
    private final String technicalContactEmail;
    private final String supportContactName;
    private final String supportContactEmail;
    private final String displayName;
    private final String displayDescription;

    public BayernIdConfigProvider(Config.Scope config) {
        isEnabled = config.getBoolean("enabled", true);
        spName = config.get("sp-name", "Unbenannter Serviceprovider");
        spDescription = config.get("sp-description", "Unbenannter Serviceprovider");
        orgName = config.get("org-name", "Unbenannte Organisation");
        orgDescription = config.get("org-description", "Unbenannte Organisation");
        orgUrl = config.get("org-url", "https://www.example.org");
        technicalContactName = config.get("technical-contact-name", "Erika Musterfrau");
        technicalContactEmail = config.get("technical-contact-email", "tech@example.org");
        supportContactName = config.get("support-contact-name", "Unbenannter Support");
        supportContactEmail = config.get("support-contact-email", "support@example.org");
        displayName = config.get("display-name", "Unbenanntes Fachverfahren");
        displayDescription = config.get("display-description", "Beschreibung des unbenannten Fachverfahrens");
    }

    @Override
    public void close() {
        // Do nothing here
    }

    public String getSpName() {
        return spName;
    }

    public String getSpDescription() {
        return spDescription;
    }

    public String getOrgName() {
        return orgName;
    }

    public String getOrgDescription() {
        return orgDescription;
    }

    public String getOrgUrl() {
        return orgUrl;
    }

    public String getTechnicalContactName() {
        return technicalContactName;
    }

    public String getTechnicalContactEmail() {
        return technicalContactEmail;
    }

    public String getSupportContactName() {
        return supportContactName;
    }

    public String getSupportContactEmail() {
        return supportContactEmail;
    }

    public Boolean isEnabled() {
        return isEnabled;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDisplayDescription() {
        return displayDescription;
    }
}
