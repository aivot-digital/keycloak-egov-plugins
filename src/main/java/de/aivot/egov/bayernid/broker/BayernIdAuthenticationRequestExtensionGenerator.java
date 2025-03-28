package de.aivot.egov.bayernid.broker;

import de.aivot.egov.bayernid.providers.BayernIdConfigProvider;
import de.aivot.egov.shared.broker.EGovRequestedAttribute;
import org.keycloak.saml.SamlProtocolExtensionsAwareBuilder;
import org.keycloak.saml.common.exceptions.ProcessingException;
import org.keycloak.saml.common.util.StaxUtil;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLStreamWriter;
import java.util.List;

/**
 * This class is responsible for extending the BayernID authentication request.
 * It adds the requested attributes and the display information to the SAML request.
 */
public class BayernIdAuthenticationRequestExtensionGenerator implements SamlProtocolExtensionsAwareBuilder.NodeGenerator {
    private static final String AKDB_NAMESPACE_URI = "https://www.akdb.de/request/2018/09";
    private static final String AKDB_NAMESPACE_PREFIX = "akdb";
    private static final String[][] ATTRIBUTES = new String[][]{
            {"Version", "2"},
    };

    private static final String CLASSIC_UI_NAMESPACE_URI = "https://www.akdb.de/request/2018/09/classic-ui/v1";
    private static final String CLASSIC_UI_NAMESPACE_PREFIX = "classic-ui";

    private final List<EGovRequestedAttribute> EGovRequestedAttributes;
    private final BayernIdConfigProvider configProvider;

    public BayernIdAuthenticationRequestExtensionGenerator(
            @Nonnull List<EGovRequestedAttribute> EGovRequestedAttributes,
            @Nonnull BayernIdConfigProvider configProvider
    ) {
        this.EGovRequestedAttributes = EGovRequestedAttributes;
        this.configProvider = configProvider;
    }

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

        for (EGovRequestedAttribute attribute : EGovRequestedAttributes) {
            StaxUtil.writeStartElement(writer, AKDB_NAMESPACE_PREFIX, "RequestedAttribute", AKDB_NAMESPACE_URI);
            StaxUtil.writeAttribute(writer, "Name", attribute.name());
            StaxUtil.writeAttribute(writer, "FriendlyName", attribute.friendlyName());
            StaxUtil.writeEndElement(writer);
        }

        StaxUtil.writeEndElement(writer);
    }

    private void writeDisplayInformation(XMLStreamWriter writer) throws ProcessingException {
        StaxUtil.writeStartElement(writer, AKDB_NAMESPACE_PREFIX, "DisplayInformation", AKDB_NAMESPACE_URI);

        StaxUtil.writeStartElement(writer, CLASSIC_UI_NAMESPACE_PREFIX, "Version", CLASSIC_UI_NAMESPACE_URI);
        StaxUtil.writeNameSpace(writer, CLASSIC_UI_NAMESPACE_PREFIX, CLASSIC_UI_NAMESPACE_URI);

        StaxUtil.writeStartElement(writer, CLASSIC_UI_NAMESPACE_PREFIX, "OrganizationDisplayName", CLASSIC_UI_NAMESPACE_URI);
        StaxUtil.writeCData(writer, configProvider.getDisplayName());
        StaxUtil.writeEndElement(writer);

        StaxUtil.writeStartElement(writer, CLASSIC_UI_NAMESPACE_PREFIX, "OnlineServiceId", CLASSIC_UI_NAMESPACE_URI);
        StaxUtil.writeCData(writer, configProvider.getBmiId());
        StaxUtil.writeEndElement(writer);

        StaxUtil.writeEndElement(writer);
        StaxUtil.writeEndElement(writer);
    }
}
