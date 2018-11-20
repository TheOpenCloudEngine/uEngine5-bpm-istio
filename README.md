# uEngine5-bpm-istio

## Running in Kubernetes / Istio

```
mvn clean package -B -Dmaven.test.skip=true

cd definition-service
docker build -t gcr.io/my-project-1531888882785/uengine-definition:v1 .
docker push gcr.io/my-project-1531888882785/uengine-definition:v1

kubectl create -f Deployment.yaml
(Note:  you must change the image name as your one inside the yaml file)
(kubectl run --image="gcr.io/my-project-1531888882785/uengine-definition:v1")

kubectl create -f Service.yaml
(kubectl expose deploy uengine-defintion --port="9093" --type="LoadBalancer")
(kubectl get svc -w)

cd ..

cd process-service
docker build -t gcr.io/my-project-1531888882785/uengine-process:v1 .
docker push gcr.io/my-project-1531888882785/uengine-process:v1
cd ..

cd 


```

## Local development enviroment

Edit your hosts file to mimic the Kubernetes DNS service
```
127.0.0.1       kafka
127.0.0.1       uengine-definition
127.0.0.1       uengine-process

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
