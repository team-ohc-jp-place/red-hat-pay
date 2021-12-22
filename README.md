# OpenShift プロジェクト作成
```shell
$ oc new-project red-hat-pay
``` 

# Operatorのインストール
- Red Hat Integration - AMQ Streams
- Cryostat
- Data Grid
- Grafana Operator (Community)

# アプリケーションをビルド
```shell
$ git clone https://github.com/team-ohc-jp-place/red-hat-pay.git
$ cd red-hat-pay
$ bash environment/build/build.sh
```

# アプリケーションコンテナのビルド・プッシュ
```shell
$ docker login quay.io
$ export quay_user=xxxx
$ cd payment/
$ docker build -f src/main/docker/Dockerfile.jvm -t quarkus/payment-jvm .
$ docker image tag quarkus/payment-jvm:latest quay.io/${quay_user}/payment-jvm:latest
$ docker push quay.io/${quay_user}/payment-jvm:latest
$ cd ../point/
$ docker build -f src/main/docker/Dockerfile.jvm -t quarkus/point-jvm .
$ docker image tag quarkus/point-jvm:latest quay.io/${quay_user}/point-jvm:latest
$ docker push quay.io/${quay_user}/point-jvm:latest
```

# 環境をデプロイ
```shell
$ oc project red-hat-pay
$ cd ../environment/prod
$ bash install.sh
```