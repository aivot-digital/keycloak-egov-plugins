package de.aivot.egov.bayernid.providers;

import de.aivot.egov.providers.EgovConfigProvider;
import de.aivot.egov.providers.EgovConfigProviderFactory;
import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

import java.util.HashMap;
import java.util.Map;

public class BayernIdConfigProviderFactoryImpl implements EgovConfigProviderFactory {
    public static String PROVIDER_ID = "bayernid";
    private final static Logger logger = Logger.getLogger(BayernIdConfigProviderFactoryImpl.class);

    @Override
    public EgovConfigProvider create(KeycloakSession session) {
        logger.info("Creating BayernIdConfigProvider");
        return new BayernIdConfigProviderImpl(session);
    }

    @Override
    public void init(Config.Scope config) {
        logger.info("Initializing BayernIdConfigProviderFactory");
        config.getPropertyNames().forEach(name -> logger.info("Property: " + name));
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
        var ret = new HashMap<String, String>();

        ret.put("bayernid", "bayernid");

        return ret;
    }
}
