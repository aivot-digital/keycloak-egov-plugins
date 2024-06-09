package de.aivot.egov.bayernid.broker.bayernid.generators;

import de.aivot.egov.bayernid.broker.bayernid.enums.BayernIdAttribute;
import de.aivot.egov.bayernid.providers.BayernIdConfigProvider;
import de.aivot.egov.bayernid.providers.BayernIdConfigProviderFactory;
import de.aivot.egov.providers.EgovConfigProvider;
import jakarta.ws.rs.NotFoundException;
import org.jboss.logging.Logger;
import org.keycloak.models.KeycloakSession;
import org.keycloak.saml.SamlProtocolExtensionsAwareBuilder;
import org.keycloak.saml.common.exceptions.ProcessingException;
import org.keycloak.saml.common.util.StaxUtil;
import org.keycloak.services.resources.KeycloakApplication;
import org.keycloak.sessions.AuthenticationSessionModel;

import javax.xml.stream.XMLStreamWriter;

public class BayernIdAuthenticationRequestExtensionGenerator implements SamlProtocolExtensionsAwareBuilder.NodeGenerator {
    private final static Logger logger = Logger.getLogger(BayernIdAuthenticationRequestExtensionGenerator.class);

    private static final String AKDB_NAMESPACE_URI = "https://www.akdb.de/request/2018/09";
    private static final String AKDB_NAMESPACE_PREFIX = "akdb";
    private static final String[][] ATTRIBUTES = new String[][]{
            {"Version", "2"},
    };

    private static final String CLASSIC_UI_NAMESPACE_URI = "https://www.akdb.de/request/2018/09/classic-ui/v1";
    private static final String CLASSIC_UI_NAMESPACE_PREFIX = "classic-ui";

    @Override
    public void write(XMLStreamWriter writer) throws ProcessingException {
        StaxUtil.writeStartElement(writer, AKDB_NAMESPACE_PREFIX, "AuthenticationRequest", AKDB_NAMESPACE_URI);
        StaxUtil.writeNameSpace(writer, AKDB_NAMESPACE_PREFIX, AKDB_NAMESPACE_URI);
        for (String[] attribute : ATTRIBUTES) {
            StaxUtil.writeAttribute(writer, attribute[0], attribute[1]);
        }

        writeRequestedAttributes(writer);
        writeDisplayInformation(writer);

        StaxUtil.writeEndElement(writer);
    }

    private void writeRequestedAttributes(XMLStreamWriter writer) throws ProcessingException {
        StaxUtil.writeStartElement(writer, AKDB_NAMESPACE_PREFIX, "RequestedAttributes", AKDB_NAMESPACE_URI);

        for (BayernIdAttribute attribute : BayernIdAttribute.values()) {
            StaxUtil.writeStartElement(writer, AKDB_NAMESPACE_PREFIX, "RequestedAttribute", AKDB_NAMESPACE_URI);
            StaxUtil.writeAttribute(writer, "Name", attribute.getName());
            StaxUtil.writeAttribute(writer, "FriendlyName", attribute.getFriendlyName());
            StaxUtil.writeEndElement(writer);
        }

        StaxUtil.writeEndElement(writer);
    }

    private void writeDisplayInformation(XMLStreamWriter writer) throws ProcessingException {

        EgovConfigProvider configProviderRaw;
        try (var session = KeycloakApplication.getSessionFactory().create()) {
            configProviderRaw = session
                    .getProvider(
                            EgovConfigProvider.class,
                            BayernIdConfigProviderFactory.PROVIDER_ID
                    );
        }

        if (!(configProviderRaw instanceof BayernIdConfigProvider configProvider)) {
            logger.error("Config provider not found");
            throw new NotFoundException("Config provider not found");
        }

        StaxUtil.writeStartElement(writer, AKDB_NAMESPACE_PREFIX, "DisplayInformation", AKDB_NAMESPACE_URI);

        StaxUtil.writeStartElement(writer, CLASSIC_UI_NAMESPACE_PREFIX, "Version", CLASSIC_UI_NAMESPACE_URI);
        StaxUtil.writeNameSpace(writer, CLASSIC_UI_NAMESPACE_PREFIX, CLASSIC_UI_NAMESPACE_URI);

        StaxUtil.writeStartElement(writer, CLASSIC_UI_NAMESPACE_PREFIX, "OrganizationDisplayName", CLASSIC_UI_NAMESPACE_URI);

        StaxUtil.writeCData(writer, configProvider.getDisplayName());

        StaxUtil.writeEndElement(writer);
        StaxUtil.writeEndElement(writer);
        StaxUtil.writeEndElement(writer);
    }
}
