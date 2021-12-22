# OpenShift プロジェクト作成
```shell
$ oc new-project red-hat-pay
``` 

# Operatorのインストール
- Red Hat Integration - AMQ Streams
- Cryostat
- Data Grid
- Grafana Operator (Community)

# 初期処理
```shell
$ git clone https://github.com/team-ohc-jp-place/red-hat-pay.git
$ cd red-hat-pay
$ bash environment/init.sh 
```

# アプリケーションをビルド
```shell
$ bash environment/build/build.sh
```

# アプリケーションコンテナのビルド・プッシュ
```shell
$ bash environment/build/image-build.sh
```

# 環境をデプロイ
```shell
$ cd environment/prod
$ bash install.sh
```