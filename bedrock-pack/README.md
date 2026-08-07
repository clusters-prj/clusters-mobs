# Bedrock(統合版)向けリソースパック

Geyser経由で接続する統合版クライアントに、カスタムモデルを正しい向き・サイズで表示させるためのパック。

## なぜこれが必要か

**Java側のモデル定義やエンティティ姿勢は、一切Bedrockに橋渡しされない。** 実機で検証した結果は次のとおり。

| 手段 | Bedrockでの結果 |
|---|---|
| ItemDisplay | 表示されない |
| ArmorStand 頭スロット | 表示されない(素のバニラブロックでも不可) |
| ArmorStand 腕ポーズ(`setRightArmPose`) | 反映されない |
| Javaモデルの `display` 変換 | 反映されない |
| ArmorStand 手スロット | **表示される。ただし角度はBedrock固定で制御不可** |
| 手スロット + **本パックのアタッチャブル** | **表示され、向き・サイズも完全に制御できる** |

つまり統合版で見た目を制御する唯一の方法が、このパックにアタッチャブルを置くこと。
Geyser自身も旧`render_offsets`について "Please migrate to attachables" と案内している。

## 仕組み

Geyserはカスタムアイテムを `geyser_custom:<mapping-name>` という識別子でBedrockに登録する
(`GEYSER_CUSTOM_NAMESPACE`)。この識別子に一致するアタッチャブルを置くと、クライアントが
それを使って描画する。

```
attachables/custommobs_test_cube.json   identifier: geyser_custom:custommobs_test_cube
models/entity/*.geo.json                ジオメトリ本体
animations/*.animation.json             回転・位置・スケール
textures/entity/*.png                   ジオメトリのテクスチャ
textures/items/*.png + item_texture.json  インベントリ表示用アイコン
```

向きは `animations/*.animation.json` の `rotation` で決まる。Java側は
`assets/minecraft/models/item/*.json` の `display.thirdperson_righthand` で決まるので、
**両方を同じ値に揃えないと見た目がズレる**。

## デプロイ

```bash
python bedrock-pack/build-mcpack.py
scp bedrock-pack/custommobs-test.mcpack root@<velocity>:/root/velocity/plugins/Geyser-Velocity/packs/
ssh root@<velocity> systemctl restart velocity.service
```

注意点:

- **`packs/` の直下に置く。** サブフォルダの中は読まれない
- パックを変更したら **manifest.json のバージョンを上げる。** 上げないとクライアントが
  キャッシュした古いパックを使い続ける
- マッピング定義は `geyser-custom-mappings.json`(リポジトリ直下)を
  Geyserの `custom_mappings/` に置く
- 反映確認は Velocity の起動ログの `Registered N custom items`
