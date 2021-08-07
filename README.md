# Kryptonite ID

simple oauth2 identity provider

## build & run with docker

Requirements:

- a configuration file (example: config/application-config.yaml)

```
# build project
docker run -it --rm -u 1000 -v "$(pwd)":/usr/src/kryptonite -w /usr/src/kryptonite maven:3.8-openjdk-11 mvn clean package

# build image
docker build -t kryptonite .

# run
docker run --rm -ti -p8080:8080 -v $(pwd)/config/:/etc/kryptonite kryptonite
```

Sample Authorization call:
http://localhost:8080/authorize?response_type=code&state=some-data%C3%9F&client_id=demo&scope=profile%20email&redirect_uri=http%3A%2F%2Flocalhost%2Fcallback


## build & run natively

Requirements:

- java 11
- maven 3.6+

```
# build
mvn clean package

# run
java -jar target/kryptonite-0.0.1-SNAPSHOT.jar --spring.config.location=file:.config/application-config.yaml
```

