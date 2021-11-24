# Operatorのインストール
- Cryostat
- Data Grid
- Grafana 

# アプリケーションをビルド
```shell
$ git clone https://github.com/team-ohc-jp-place/red-hat-pay.git
$ cd red-hat-pay
$ bash environment/build/build.sh
```

# 環境をデプロイ
```shell
$ oc project XXX
$ cd environment/prod
$ bash install.sh
```