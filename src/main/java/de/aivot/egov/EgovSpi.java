package de.aivot.egov;

import de.aivot.egov.providers.EgovConfigProvider;
import de.aivot.egov.providers.EgovConfigProviderFactory;
import org.keycloak.provider.Spi;

public class EgovSpi implements Spi {
    public static final String SPI_ID = "egov";

    @Override
    public boolean isInternal() {
        return false;
    }

    @Override
    public String getName() {
        return SPI_ID;
    }

    @Override
    public Class<EgovConfigProvider> getProviderClass() {
        return EgovConfigProvider.class;
    }

    @Override
    public Class<EgovConfigProviderFactory> getProviderFactoryClass() {
        return EgovConfigProviderFactory.class;
    }
}
