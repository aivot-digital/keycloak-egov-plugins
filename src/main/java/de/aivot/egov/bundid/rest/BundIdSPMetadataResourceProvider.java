package de.aivot.egov.bundid.rest;

import de.aivot.egov.bundid.providers.BundIdConfigProvider;
import de.aivot.egov.bundid.providers.BundIdConfigProviderFactory;
import de.aivot.egov.providers.EgovConfigProvider;
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
    private final static Logger logger = Logger.getLogger(BundIdSPMetadataResourceProvider.class);

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
        var configProviderRaw = session
                .getProvider(
                        EgovConfigProvider.class,
                        BundIdConfigProviderFactory.PROVIDER_ID
                );

        if (!(configProviderRaw instanceof BundIdConfigProvider)) {
            logger.error("Config provider not found");
            throw new NotFoundException("Config provider not found");
        }

        var configProvider = (BundIdConfigProvider) configProviderRaw;
        if (!configProvider.isEnabled()) {
            logger.error("Config provider not enabled");
            throw new NotFoundException("Config provider not enabled");
        }

        var samlIdentityProvider = getSamlIdentityProvider(idpId);
        var response = samlIdentityProvider
                .export(
                        session.getContext().getUri(),
                        session.getContext().getRealm(),
                        null
                );


        var content = (String) response.getEntity();
        response.close();

        Document doc;
        try {
            doc = DocumentUtil.getDocument(content);
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

        var serviceDescription = utils.addElement(
                null,
                entityDescriptor.getPrefix(),
                "ServiceDescription",
                configProvider.getSpDescription(),
                "xml:lang", "de"
        );
        var serviceName = attributeConsumingService
                .getElementsByTagName("md:ServiceName")
                .item(0);
        if (serviceName == null) {
            serviceName = utils.addElement(
                    null,
                    entityDescriptor.getPrefix(),
                    "ServiceName",
                    configProvider.getSpName(),
                    "xml:lang", "de"
            );
            attributeConsumingService.insertBefore(serviceName, attributeConsumingService.getFirstChild());
        }
        attributeConsumingService.insertBefore(serviceDescription, serviceName.getNextSibling());

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
                configProvider.getOrgName(),
                "xml:lang", "de"
        );
        utils.addElement(
                organization,
                entityDescriptor.getPrefix(),
                "OrganizationDisplayName",
                configProvider.getOrgDescription(),
                "xml:lang", "de"
        );
        utils.addElement(
                organization,
                entityDescriptor.getPrefix(),
                "OrganizationURL",
                configProvider.getOrgUrl(),
                "xml:lang", "de"
        );

        var contactTechnical = utils.addElement(
                entityDescriptor,
                entityDescriptor.getPrefix(),
                "ContactPerson",
                null,
                "contactType", "technical"
        );
        utils.addElement(
                contactTechnical,
                entityDescriptor.getPrefix(),
                "GivenName",
                configProvider.getTechnicalContactName()
        );
        utils.addElement(
                contactTechnical,
                entityDescriptor.getPrefix(),
                "EmailAddress",
                configProvider.getTechnicalContactEmail()
        );

        var contactSupport = utils.addElement(
                entityDescriptor,
                entityDescriptor.getPrefix(),
                "ContactPerson",
                null,
                "contactType", "support"
        );
        utils.addElement(
                contactSupport,
                entityDescriptor.getPrefix(),
                "GivenName",
                configProvider.getSupportContactName()
        );
        utils.addElement(
                contactSupport,
                entityDescriptor.getPrefix(),
                "EmailAddress",
                configProvider.getSupportContactEmail()
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

    private SAMLIdentityProvider getSamlIdentityProvider(String idpId) {
        var identityProviderConfig = session
                .getContext()
                .getRealm()
                .getIdentityProviderByAlias(idpId);

        if (identityProviderConfig == null) {
            logger.error("Identity provider not found");
            throw new NotFoundException("Identity provider not found");
        }

        SAMLIdentityProvider provider;
        try {
            var config = new SAMLIdentityProviderConfig(identityProviderConfig);
            var factory = new SAMLIdentityProviderFactory();
            provider = factory.create(session, config);
        } catch (Exception e) {
            logger.error("Error creating SAML Identity Provider", e);
            throw new BadRequestException("Identity provider is not a SAML identity provider", e);
        }
        return provider;
    }

    @Override
    public void close() {
    }
}
