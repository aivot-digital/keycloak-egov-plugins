# Prerequisites

* Java JDK 17
* Maven 3.9.5
* Docker 24.0.7

# Development Setup

1. Clone the repository
   ```sh
   git clone github.com/aivot-digital/keycloak-de-id-plugins.git
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
     -p 8080:8080 \
     -v $(pwd)/target:/opt/keycloak/providers:ro \
     quay.io/keycloak/keycloak:23.0.6 \
     start-dev
   ```

# Build Image

Build the image with the following command:
```sh
docker build -t keycloak-de-id-plugins:23.0.6 .
```