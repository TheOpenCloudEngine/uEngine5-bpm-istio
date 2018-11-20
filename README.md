# uEngine5-bpm-istio

## Running in Kubernetes / Istio

```
mvn clean package -B -Dmaven.test.skip=true

cd definition-service
mvn clean package -B
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
mvn clean package -B -Dmaven.test.skip=true
docker build -t gcr.io/my-project-1531888882785/uengine-process:v8 .
docker push gcr.io/my-project-1531888882785/uengine-process:v8
kubectl set image deploy/uengine-process uengine-process=gcr.io/my-project-1531888882785/uengine-process:v8
cd ..

cd proxy
mvn clean package -B
docker build -t gcr.io/my-project-1531888882785/uengine-proxy:v1 .
docker push gcr.io/my-project-1531888882785/uengine-proxy:v1

kubectl create -f Deployment.yaml
(Note:  you must change the image name as your one inside the yaml file)
(kubectl run --image="gcr.io/my-project-1531888882785/uengine-definition:v1")

kubectl create -f Service.yaml
(kubectl expose deploy uengine-defintion --port="9093" --type="LoadBalancer")
(kubectl get svc -w)

cd ..


```



Run kafka:
```
$ helm repo add incubator http://storage.googleapis.com/kubernetes-charts-incubator
$ helm install --name kafka incubator/kafka


You can connect to Kafka by running a simple pod in the K8s cluster like this with a configuration like this:

  apiVersion: v1
  kind: Pod
  metadata:
    name: testclient
    namespace: default
  spec:
    containers:
    - name: kafka
      image: confluentinc/cp-kafka:5.0.0-2
      command:
        - sh
        - -c
        - "exec tail -f /dev/null"

Once you have the testclient pod above running, you can list all kafka
topics with:

  kubectl -n default exec testclient -- /usr/bin/kafka-topics --zookeeper my-kafka-zookeeper:2181 --list

To create a new topic:

  kubectl -n default exec testclient -- /usr/bin/kafka-topics --zookeeper my-kafka-zookeeper:2181 --topic test1 --create --partitions 1 --replication-factor 1

To listen for messages on a topic:

  kubectl -n default exec -ti testclient -- /usr/bin/kafka-console-consumer --bootstrap-server my-kafka:9092 --topic test1 --from-beginning

To stop the listener session above press: Ctrl+C

To start an interactive message producer session:
  kubectl -n default exec -ti testclient -- /usr/bin/kafka-console-producer --broker-list my-kafka-headless:9092 --topic test1

To create a message in the above session, simply type the message and press "enter"

```



## Local development enviroment

Edit your hosts file to mimic the Kubernetes DNS service
```
127.0.0.1       kafka
127.0.0.1       uengine-definition
127.0.0.1       uengine-process

```



Run each service with mvn:
```
cd definition-service
mvn spring-boot:run -Dserver.port=9093
cd ..

cd process-service
mvn spring-boot:run -Dserver.port=9094

```
