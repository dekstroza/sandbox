# Thorntail 4 Example

Basic Thorntail 4 example with JAX-RS and WebSockets.

## Requirements

1. OpenJDK 11
2. Maven 3.5.3 or greater
3. Docker (if building container)

## Build instructions

To skip docker container build:
```
./mvnw clean install
```

or to include docker container as well
```
./mvnw clean install -dockerfile.skip=false
```

