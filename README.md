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
$ bash environment/setting.sh
```

# アプリケーションのビルドとアプリケーションコンテナのビルド・プッシュ
```shell
$ bash environment/build/build.sh
```

# 環境をデプロイ
```shell
$ cd environment/prod
$ bash install.sh
```

# 環境を削除
```shell
$ bash environment/cleanup.sh 
```