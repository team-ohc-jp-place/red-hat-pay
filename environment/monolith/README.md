# OpenShift プロジェクト作成
```shell
$ oc new-project red-hat-pay-monolith
``` 

# アプリケーションをビルド
```shell
$ git clone https://github.com/team-ohc-jp-place/red-hat-pay.git
$ cd red-hat-pay
$ bash environment/build/build.sh
```

# 環境をデプロイ
```shell
$ cd environment/monolith
$ bash install.sh
```

# 環境を削除
```
$ bash cleanup.sh
```
