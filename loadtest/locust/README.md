# Locust のインストール
* OpenShift クラスタへのログイン
* Locust インストールスクリプトの実行
```
./install-locust.sh
```

* Locust へのアクセス URL の確認
```
echo http://$(oc get route app --template={{.spec.host}})
```

* ブラウザから Locust 画面を開く

# テストスクリプト・ターゲットホストの変更方法
* テストスクリプトの編集 (locustfile.py)
* テストスクリプト・ターゲットホストの変更
```
./seed.sh locustfile.py (ターゲットホスト名)

# コマンド実行例
# ./seed.sh locustfile.py https://echo-api.3scale.net:443
```

* ConfigMap の変更を検知し、自動的に Locust Pod が再デプロイされる

# Locust のスケールアウト

* slave Pod のレプリカ数の変更
```
# コマンド実行例
oc scale dc/locust-slave --replicas=4
```
