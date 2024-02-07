package de.aivot.egov.bayernid.rest;

import de.aivot.egov.bundid.broker.bundid.BundIdSamlAuthenticationPreprocessor;
import de.aivot.utils.XmlDocumentUtils;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;
import org.keycloak.broker.saml.SAMLIdentityProvider;
import org.keycloak.broker.saml.SAMLIdentityProviderConfig;
import org.keycloak.broker.saml.SAMLIdentityProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.saml.common.util.DocumentUtil;
import org.keycloak.services.resource.RealmResourceProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class BundIdSPMetadataResourceProvider implements RealmResourceProvider {
    private final static Logger logger = Logger.getLogger(BundIdSamlAuthenticationPreprocessor.class);

    private final KeycloakSession session;

    public BundIdSPMetadataResourceProvider(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public Object getResource() {
        return this;
    }

    @GET
    @Path("/{id}/metadata")
    @Produces("text/xml; charset=utf-8")
    public Response get(@PathParam("id") String idpId) {
        var providerConfig = session.getContext().getRealm().getIdentityProviderByAlias(idpId);

        if (providerConfig == null) {
            logger.error("Identity provider not found");
            throw new NotFoundException("Identity provider not found");
        }

        SAMLIdentityProvider provider;
        SAMLIdentityProviderConfig samlIdentityProviderConfig;
        try {
            samlIdentityProviderConfig = new SAMLIdentityProviderConfig(providerConfig);
            var factory = new SAMLIdentityProviderFactory();
            provider = factory.create(session, samlIdentityProviderConfig);
        } catch (Exception e) {
            logger.error("Error creating SAML Identity Provider", e);
            throw new BadRequestException("Identity provider is not a SAML identity provider", e);
        }

        var response = provider.export(session.getContext().getUri(), session.getContext().getRealm(), null);

        // TODO: Get config from environment

        var content = (String) response.getEntity();
        response.close();

        Document doc;
        try {
            doc = DocumentUtil.getDocument(content);
            logger.info(DocumentUtil.getDocumentAsString(doc));
        } catch (Exception e) {
            logger.error("Error parsing SAML metadata", e);
            throw new RuntimeException(e);
        }

        var utils = XmlDocumentUtils.create(doc);
        var entityDescriptor = (Element) doc.getFirstChild();
        var sPSSODescriptor = (Element) entityDescriptor.getElementsByTagName(entityDescriptor.getPrefix() + ":SPSSODescriptor").item(0);
        var attributeConsumingService = (Element) sPSSODescriptor.getElementsByTagName(entityDescriptor.getPrefix() + ":AttributeConsumingService").item(0);
        if (attributeConsumingService == null) {
            attributeConsumingService = utils.addElement(
                    sPSSODescriptor,
                    entityDescriptor.getPrefix(),
                    "AttributeConsumingService",
                    null,
                    "index", "0",
                    "isDefault", "true"
            );
        }

        entityDescriptor.removeAttribute("ID");

        utils.addElement(
                attributeConsumingService,
                entityDescriptor.getPrefix(),
                "ServiceName",
                samlIdentityProviderConfig.getAttributeConsumingServiceName(),
                "xml:lang", "de"
        );
        utils.addElement(
                attributeConsumingService,
                entityDescriptor.getPrefix(),
                "ServiceDescription",
                samlIdentityProviderConfig.getAttributeConsumingServiceName(),
                "xml:lang", "de"
        );

        var organization = utils.addElement(
                entityDescriptor,
                entityDescriptor.getPrefix(),
                "Organization",
                null
        );
        utils.addElement(
                organization,
                entityDescriptor.getPrefix(),
                "OrganizationName",
                "Bayerisches Landesamt für Steuern",
                "xml:lang", "de"
        );
        utils.addElement(
                organization,
                entityDescriptor.getPrefix(),
                "OrganizationDisplayName",
                "Bayerisches Landesamt für Steuern",
                "xml:lang", "de"
        );
        utils.addElement(
                organization,
                entityDescriptor.getPrefix(),
                "OrganizationURL",
                "Bayerisches Landesamt für Steuern",
                "xml:lang", "de"
        );

        var contactTechnical = utils.addElement(entityDescriptor, entityDescriptor.getPrefix(), "ContactPerson", null, "contactType", "technical");
        utils.addElement(
                contactTechnical,
                entityDescriptor.getPrefix(),
                "GivenName",
                "Bayerisches Landesamt für Steuern"
        );
        utils.addElement(
                contactTechnical,
                entityDescriptor.getPrefix(),
                "EmailAddress",
                "Bayerisches Landesamt für Steuern"
        );

        var contactSupport = utils.addElement(entityDescriptor, entityDescriptor.getPrefix(), "ContactPerson", null, "contactType", "support");
        utils.addElement(
                contactSupport,
                entityDescriptor.getPrefix(),
                "GivenName",
                "Bayerisches Landesamt für Steuern"
        );
        utils.addElement(
                contactSupport,
                entityDescriptor.getPrefix(),
                "EmailAddress",
                "Bayerisches Landesamt für Steuern"
        );

        String responseContent;
        try {
            responseContent = DocumentUtil.getDocumentAsString(doc);
        } catch (Exception e) {
            logger.error("Error serializing SAML metadata", e);
            throw new RuntimeException(e);
        }

        return Response
                .ok(responseContent)
                .build();
    }

    @Override
    public void close() {
    }
}
