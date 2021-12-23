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

# アプリのURL取得方法
```shell
echo "http://`oc -n rhp-istio-system get route istio-ingressgateway -o jsonpath='{.spec.host}'`/index"
```

# 環境を削除
```shell
$ bash environment/cleanup.sh 
```