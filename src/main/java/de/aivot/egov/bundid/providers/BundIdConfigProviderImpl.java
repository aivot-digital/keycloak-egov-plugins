package de.aivot.egov.bundid.providers;

import de.aivot.egov.providers.EgovConfigProvider;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;

public class BundIdConfigProviderImpl implements EgovConfigProvider {
    private final String spName;
    private final String spDescription;
    private final String orgName;
    private final String orgDescription;
    private final String orgUrl;
    private final String technicalContactName;
    private final String technicalContactEmail;
    private final String supportContactName;
    private final String supportContactEmail;

    public BundIdConfigProviderImpl(Config.Scope config) {
        spName = config.get("sp-name", "Unbenannter Serviceprovider");
        spDescription = config.get("sp-description", "Unbenannter Serviceprovider");
        orgName = config.get("org-name", "Unbenannte Behörde");
        orgDescription = config.get("org-description", "Unbenannte Behörde");
        orgUrl = config.get("org-url", "https://www.example.org");
        technicalContactName = config.get("technical-contact-name", "Unbenannter Technischer Kontakt");
        technicalContactEmail = config.get("technical-contact-email", "tech@example.org");
        supportContactName = config.get("support-contact-name", "Unbenannter Support");
        supportContactEmail = config.get("support-contact-email", "support@example.org");
    }

    @Override
    public void close() {

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
}
