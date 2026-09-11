# open-isco — kotoba-native pilot (Datom + clj→WASM)

ISCO Workforce Coordinator の kotoba-clj source と canonical EDN data を管理する。
コンパイル済み WASM と shell runner は repository に保持しない。

実装は **OpenRouter 経由の `moonshotai/kimi-k2.7-code`** を実装エンジンとして生成し、
Claude (Opus 4.8) がオーケストレーション・検証した (actor ランタイム LLM は不変 →
ADR-2605215000 §1 に抵触しない)。

## 成果物

| ファイル | 役割 |
|---|---|
| `data/isco-occupations.edn` | ISCO-08 の EAVT Datom スキーマと全619ノードの authoritative seed。 |
| `src/isco/coordinator.cljk` | kotoba-clj coordinator source。 |
| `lex/` | actor/open-isco contract の canonical EDN。 |
| `wire/` | JSON/JSON-LD/BPMN の外部表現。 |

## ctx 契約

`run(ctx)` が受け取る CBOR map:

- `code` : text — 対象 ISCO コード (例 `"2512"`)
- `mode` : uint — `0` = 名称ルックアップ / `1` = llm 要約 / `2` = coverage 件数 (CBOR uint) / `3` = 親コード / `4` = 子プロセス数 / `5` = 子コード配列 (CBOR array) / `6` = coverage比 (CBOR map)

## 検証と外部 build

`kbb -cp src:test run_tests.cljk` が seed、contract、actor boundary を検証する。
WASM build が必要な場合は `dependencies.edn` に固定された flat west sibling
`orgs/kotoba-lang/kotoba` の resource-guard 対応 build workflow を使い、生成物は
build output に置く。repository へ commit しない。

mode: 0 lookup / 1 summarize(llm-infer) / 2 coverage / 3 parent / 4 children / 5 materialize / 6 ratio。

## TODO (pilot の次)

- `:summarize` を kqe で引いた名称 (CBOR bytes) からプロンプト生成するよう拡張
  (現状は code 文字列を渡す最小実装)。
- `:summarize` を kqe で引いた名称 (CBOR bytes) からプロンプト生成するよう拡張
  (現状は code 文字列を渡す最小実装)。
- coordinator cell を `kotoba serve` の常駐 WasmExecutor に登録し、619 DID の
  `did:web:isco.etzhayyim.com:occupation:{code}` と接続。

<!-- coverage-worklist:auto -->
## Coverage worklist (`isco.tools.query`)

`gaps: 0` — ISCO-08 標準構造を全件網羅 (`:authoritative`)。深掘りギャップなし。
<!-- /coverage-worklist -->
