package de.aivot.egov.bundid.broker.bundid.generators;

import de.aivot.egov.bundid.broker.bundid.enums.BundIdAttribute;
import org.keycloak.saml.SamlProtocolExtensionsAwareBuilder;
import org.keycloak.saml.common.exceptions.ProcessingException;
import org.keycloak.saml.common.util.StaxUtil;

import javax.xml.stream.XMLStreamWriter;

public class BundIdAuthenticationRequestExtensionGenerator implements SamlProtocolExtensionsAwareBuilder.NodeGenerator {
    private static final String NAMESPACE_URI = "https://www.akdb.de/request/2018/09";
    private static final String NAMESPACE_PREFIX = "akdb";
    private static final String[][] ATTRIBUTES = new String[][]{
            {"Version", "2"},
    };

    @Override
    public void write(XMLStreamWriter writer) throws ProcessingException {
        StaxUtil.writeStartElement(writer, NAMESPACE_PREFIX,  "AuthenticationRequest", NAMESPACE_URI);
        StaxUtil.writeNameSpace(writer, NAMESPACE_PREFIX, NAMESPACE_URI);
        for (String[] attribute : ATTRIBUTES) {
            StaxUtil.writeAttribute(writer, attribute[0], attribute[1]);
        }

        StaxUtil.writeStartElement(writer, NAMESPACE_PREFIX, "RequestedAttributes", NAMESPACE_URI);

        for (BundIdAttribute bundIdAttribute : BundIdAttribute.values()) {
            StaxUtil.writeStartElement(writer, NAMESPACE_PREFIX, "RequestedAttribute", NAMESPACE_URI);
            StaxUtil.writeAttribute(writer, "Name", bundIdAttribute.getName());
            StaxUtil.writeAttribute(writer, "FriendlyName", bundIdAttribute.getFriendlyName());
            StaxUtil.writeEndElement(writer);
        }

        StaxUtil.writeEndElement(writer);
        StaxUtil.writeEndElement(writer);
    }
}
