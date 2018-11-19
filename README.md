# uEngine5-bpm-istio

## Local development enviroment

Edit your hosts file to mimic the Kubernetes DNS service
```
127.0.0.1       kafka
127.0.0.1       uengine-definition
127.0.0.1       uengine-instance

```


Run kafka:
...


Run each service with mvn:
```
cd definition-service
mvn spring-boot:run -Dserver.port=9093
cd ..

cd process-service
mvn spring-boot:run -Dserver.port=9094

```
