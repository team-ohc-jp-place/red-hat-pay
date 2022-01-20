# クラスタへ各Operatorをインストール
- Red Hat Integration - AMQ Streams
- Data Grid
- OpenShift Elasticsearch Operator (stable-5.2)
- Red Hat OpenShift distributed tracing platform
- Kiali Operator
- Red Hat OpenShift Service Mesh
- Red Hat OpenShift Logging (stable-5.2)

# 初期処理
```shell
$ git clone https://github.com/team-ohc-jp-place/red-hat-pay.git
$ cd red-hat-pay/environment
$ bash init.sh 
$ bash setting.sh
```

# Red Hat Pay名前空間へ各Operatorをインストール
- Cryostat
- Grafana Operator (Community)

# アプリケーションのビルドとアプリケーションコンテナのビルド・プッシュ
```shell
$ bash build.sh
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

# データの初期化
```shell
echo "http://`oc -n rhp-istio-system get route istio-ingressgateway -o jsonpath='{.spec.host}'`/init/3000/100000000/100000000"
```

# 環境を削除
```shell
$ bash cleanup.sh 
```