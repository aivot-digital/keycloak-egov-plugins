package de.aivot.egov.bundid.broker;

import de.aivot.egov.bundid.providers.BundIdConfigProvider;
import de.aivot.egov.shared.broker.EGovRequestedAttribute;
import org.keycloak.saml.SamlProtocolExtensionsAwareBuilder;
import org.keycloak.saml.common.exceptions.ProcessingException;
import org.keycloak.saml.common.util.StaxUtil;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLStreamWriter;
import java.util.List;

/**
 * This class is responsible for extending the BundID authentication request.
 * It adds the requested attributes to the SAML request.
 */
public class BundIdAuthenticationRequestExtensionGenerator implements SamlProtocolExtensionsAwareBuilder.NodeGenerator {
    private static final String NAMESPACE_URI = "https://www.akdb.de/request/2018/09";
    private static final String NAMESPACE_PREFIX = "akdb";
    private static final String[][] ATTRIBUTES = new String[][]{
            {"Version", "2"},
    };

    private static final String CLASSIC_UI_NAMESPACE_URI = "https://www.akdb.de/request/2018/09/classic-ui/v1";
    private static final String CLASSIC_UI_NAMESPACE_PREFIX = "classic-ui";

    private final List<EGovRequestedAttribute> EGovRequestedAttributes;
    private final BundIdConfigProvider configProvider;

    public BundIdAuthenticationRequestExtensionGenerator(
            @Nonnull List<EGovRequestedAttribute> EGovRequestedAttributes,
            @Nonnull BundIdConfigProvider configProvider
    ) {
        this.EGovRequestedAttributes = EGovRequestedAttributes;
        this.configProvider = configProvider;
    }

    @Override
    public void write(XMLStreamWriter writer) throws ProcessingException {
        StaxUtil.writeStartElement(writer, NAMESPACE_PREFIX, "AuthenticationRequest", NAMESPACE_URI);
        StaxUtil.writeNameSpace(writer, NAMESPACE_PREFIX, NAMESPACE_URI);
        for (String[] attribute : ATTRIBUTES) {
            StaxUtil.writeAttribute(writer, attribute[0], attribute[1]);
        }

        writeRequestedAttributes(writer);
        writeDisplayInformation(writer);

        StaxUtil.writeEndElement(writer);
    }

    private void writeRequestedAttributes(XMLStreamWriter writer) throws ProcessingException {
        StaxUtil.writeStartElement(writer, NAMESPACE_PREFIX, "RequestedAttributes", NAMESPACE_URI);

        for (var bundIdAttribute : EGovRequestedAttributes) {
            StaxUtil.writeStartElement(writer, NAMESPACE_PREFIX, "RequestedAttribute", NAMESPACE_URI);
            StaxUtil.writeAttribute(writer, "Name", bundIdAttribute.name());
            StaxUtil.writeAttribute(writer, "FriendlyName", bundIdAttribute.friendlyName());
            StaxUtil.writeEndElement(writer);
        }

        StaxUtil.writeEndElement(writer);
    }

    private void writeDisplayInformation(XMLStreamWriter writer) throws ProcessingException {
        StaxUtil.writeStartElement(writer, NAMESPACE_PREFIX, "DisplayInformation", NAMESPACE_URI);

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
