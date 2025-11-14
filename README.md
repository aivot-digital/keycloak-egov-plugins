# keycloak-egov-plugins

Extension for Keycloak that integrates support for German identity providers, including BundID, BayernID, and Servicekonto Schleswig-Holstein. It streamlines authentication processes for e-government services, enhancing security and user management.

## Prerequisites

* Java JDK 21
* Maven 3.9.5
* Docker 24.0.7

## Development Setup

1. Clone the repository
   ```sh
   git clone github.com/aivot-digital/keycloak-egov-plugins.git
   ```
2. Build the project
   ```sh
   mvn clean install
   ```
3. Run the keycloak docker container
   ```sh
   docker run \
     --rm \
     --name keycloak \
     -e KEYCLOAK_ADMIN=admin \
     -e KEYCLOAK_ADMIN_PASSWORD=admin \
     -e "DEBUG_PORT=*:8787" \
     -e DEBUG_MODE=true \
     -p 8080:8080 \
     -p 8787:8787 \
     -v $(pwd)/target:/opt/keycloak/providers:ro \
     -v $(pwd)/themes:/opt/keycloak/themes:ro \
     quay.io/keycloak/keycloak:26.4.5 \
     start-dev --debug --spi-theme-welcome-theme=gover
   ```

## Build Image

Build the image with the following command:

```sh
docker build -t keycloak-egov-plugins:26.4.5.0 .
```

## Usage

You can use the following parameters / environment variables to configure the plugin:

### BundID

| Parameter                      | Env                             | Description             | Default                   |
|--------------------------------|---------------------------------|-------------------------|---------------------------|
| --spi-egov-bundid-bmi-id       | KC_SPI_EGOV_BUNDID_BMI_ID       | The ID given by the BMI | BMI-X0000                 |
| --spi-egov-bundid-display-name | KC_SPI_EGOV_BUNDID_DISPLAY_NAME | Name of the form        | Unbenanntes Fachverfahren |

You can also configure these parameters in the keycloak config file located at `keycloak/conf/keycloak.conf`:

```properties
spi-egov-bundid-bmi-id=BMI-X0000
spi-egov-bundid-display-name=Unbenanntes Fachverfahren
```

### BayernID

| Parameter                                   | Env                                          | Description                                                | Default                                     |
|---------------------------------------------|----------------------------------------------|------------------------------------------------------------|---------------------------------------------|
| --spi-egov-bayernid-online-service-id       | KC_SPI_EGOV_BAYERNID_ONLINE_SERVICE_ID       | ID given by the Bavarian State Ministry of Digital Affairs | XXXXXXXXXXX-DE                              |
| --spi-egov-bayernid-sp-name                 | KC_SPI_EGOV_BAYERNID_SP_NAME                 | Name of this service provider                              | Unbenannter Serviceprovider                 |
| --spi-egov-bayernid-sp-description          | KC_SPI_EGOV_BAYERNID_SP_DESCRIPTION          | Description of this service provider                       | Unbenannter Serviceprovider                 |
| --spi-egov-bayernid-org-name                | KC_SPI_EGOV_BAYERNID_ORG_NAME                | Name of the organization                                   | Unbenannte Organisation                     |
| --spi-egov-bayernid-org-description         | KC_SPI_EGOV_BAYERNID_ORG_DESCRIPTION         | Description of the organization                            | Unbenannte Organisation                     |
| --spi-egov-bayernid-org-url                 | KC_SPI_EGOV_BAYERNID_ORG_URL                 | URL of the organization                                    | https://www.example.org                     |
| --spi-egov-bayernid-technical-contact-name  | KC_SPI_EGOV_BAYERNID_TECHNICAL_CONTACT_NAME  | Name of the technical contact                              | Erika Musterfrau                            |
| --spi-egov-bayernid-technical-contact-email | KC_SPI_EGOV_BAYERNID_TECHNICAL_CONTACT_EMAIL | Email of the technical contact                             | tech@example.org                            |
| --spi-egov-bayernid-support-contact-name    | KC_SPI_EGOV_BAYERNID_SUPPORT_CONTACT_NAME    | Name of the support contact                                | Max Mustermann                              |
| --spi-egov-bayernid-support-contact-email   | KC_SPI_EGOV_BAYERNID_SUPPORT_CONTACT_EMAIL   | Email of the support contact                               | support@example.org                         |
| --spi-egov-bayernid-display-name            | KC_SPI_EGOV_BAYERNID_DISPLAY_NAME            | Name of the form                                           | Unbenanntes Fachverfahren                   |
| --spi-egov-bayernid-display-description     | KC_SPI_EGOV_BAYERNID_DISPLAY_DESCRIPTION     | Description of the form                                    | Beschreibung des unbenannten Fachverfahrens |

You can also configure these parameters in the keycloak config file located at `keycloak/conf/keycloak.conf`:

```properties
spi-egov-bayernid-online-service-id=XXXXXXXXXXX-DE
spi-egov-bayernid-sp-name=Name
spi-egov-bayernid-sp-description=Description
spi-egov-bayernid-org-name=Organization
spi-egov-bayernid-org-description=Description
spi-egov-bayernid-org-url=https://www.example.org
spi-egov-bayernid-technical-contact-name=Erika Musterfrau
spi-egov-bayernid-technical-contact-email=tech@example.org
spi-egov-bayernid-support-contact-name=Max Mustermann
spi-egov-bayernid-support-contact-email=support@example.org
spi-egov-bayernid-display-name=Unbenanntes Fachverfahren
spi-egov-bayernid-display-description=Beschreibung des unbenannten Fachverfahrens
```

### Metadata

You can view the metadata for the BayernID plugin by navigating to the following URL:

| Plugin            | URL                                                                                    |
|-------------------|----------------------------------------------------------------------------------------|
| BayernID Metadata | http://localhost:8080/realms/<NAME_OF_YOUR_REALM>/bayernid/<NAME_OF_YOUR_IDP>/metadata | 
