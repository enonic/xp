# NoDB — Findings for later research

**Status:** living document · **Started:** 2026-08-06 (during Phase 4)

Cross-cutting findings surfaced while building NoDB that outlive the gate that found them.
Each is either a **fixed defect whose class deserves an audit**, or an **open question that
needs a decision or measurement** rather than a code change today. Gate-local details stay in
the `BUILD-PHASE-*.md` records; this file is the list to come back to.

---

## 1. Two-transaction reads can silently bury work (FIXED — audit the class)

**Observed:** the outbox indexer read the queue in one transaction and the queue's max
sequence in another, then advanced its checkpoint to that max. A write committing between the
two was invisible to the first read and visible to the second, so the checkpoint moved past a
row that was never applied — and since the reader only looks *above* the checkpoint, that row
became unreachable forever. A document permanently missing from the search index, with no
error, no retry and no lag signal.

**Evidence:** Phase 4 Gate C; reproduced from the golden corpus (one arbitrary node missing,
a different one each run); regression test provokes the window with a proxied `DataSource`.

**Research:** `Tx.inTenantTx` opens, commits and closes its own connection, so *every* pair of
consecutive `inTenantTx` calls is two snapshots. Audit the codebase for other places where a
decision is made from read A and acted on with read B — particularly vacuum/GC (Phase 5),
which computes "unreferenced" sets, and anything that will later compute a high-water mark.
Consider making the hazard structural: a checked way to run multiple reads in one snapshot,
and/or a rule that a checkpoint may only ever advance to a sequence a pass actually returned.

## 2. ES 2.4's "DUCET" collation was the JVM default locale (FIXED in nodb — decide for ES)

**Observed:** `icu_collation_ducet` in `search-settings.json` is literally
`{"type": "icu_collation"}` with no `language`, so the ES ICU plugin used
`Collator.getInstance()` — i.e. **`Locale.getDefault()`**, a deployment property. Measured on
an `nb_NO` machine it produces byte-for-byte the Norwegian ordering; `ULocale.ROOT` (real
DUCET) produces a different one. So XP's DUCET fallback, and therefore **every `COLLATE` sort
naming a locale XP has no collator for**, has been environment-dependent: `nb_NO` on a
Norwegian developer's machine, typically `en_US` in a Linux container, `C`/`POSIX` when `LANG`
is unset.

**Evidence:** Phase 4 Gate D; corpus rows `ICU-05-collate-de-ducet`,
`ICU-06-collate-unknown-locale`. NoDB pins `ULocale.ROOT`.

**Measured 2026-08-06 (icu4j 78.3), and it lowers the risk sharply.** The official XP Docker
image sets `LANG=en_US.UTF-8` / `LC_ALL=en_US.UTF-8` (with `locale-gen`), so
`Locale.getDefault()` in a container is **en_US** — and `en_US` collation is **byte-identical
to ROOT/DUCET**, because ICU's `en` carries no collation tailoring:
```
ROOT     aal,æble,ähnlich,åtte,øl,opa,über,zug
en_US    aal,æble,ähnlich,åtte,øl,opa,über,zug     <- identical
nb_NO    opa,über,zug,æble,ähnlich,øl,aal,åtte     <- æøå after z, and aa treated as å
sv       aal,opa,über,zug,åtte,æble,ähnlich,øl
```
So in the containerized product the DUCET fallback has been **accidentally correct**, and
pinning `ULocale.ROOT` is a **no-op there**. The divergence is confined to (a) developer
machines, where the OS locale leaks in, and (b) self-hosted installs run outside the image
with a non-`en` `LANG`.

**Corollary — two of Gate D's "documented deltas" are a recording artifact, not behaviour.**
The ES baseline was recorded on an `nb_NO` machine, which is precisely why `ICU-05` and
`ICU-06` show order deltas. Re-recorded in the Docker image or CI (`en_US`) they would match
nodb's ROOT ordering exactly. **Action: re-record the baseline in a controlled locale**, or
pin `-Duser.language`/`-Duser.country` for corpus runs, so the corpus stops carrying the
recording host's locale. (Note this is *separate* from the four German `icuSort` failures:
those name `de` explicitly and used `icu_collation_de`, so they are an ES-2.4-CLDR-vintage
issue, not a default-locale one.)

**Research remaining:** (a) a self-hosted install on a non-`en` host will see unmapped-locale
sorts change at cutover — worth a release note and a migration check. (b) Should the ES path
be fixed too? It would change indexed collation keys and force a reindex, which argues for
leaving it and documenting the change as part of migration. (c) Consider setting
`-Duser.language`/`-Duser.country` explicitly in `server.sh` so XP never inherits a host
locale for anything, belt-and-braces even after nodb owns collation.

## 3. `nodeService.getByIds` is order-preserving by accident (FIXED — document the contract)

**Observed:** `BranchStore.getByNodeIds` documented itself as returning rows in no particular
order, but `BranchServiceImpl.get(Iterable<NodeId>)` feeds the result straight into `Nodes`,
and Elasticsearch's multi-get has always answered in the **requested** order. So callers rely
on an ordering no layer promised. In nodb mode a correct sort came back scrambled, differently
each run (generated ids → heap order), with no error.

**Evidence:** Phase 4 Gate D; fixed with `ORDER BY array_position(...)`, and
`FindNodesByQueryCommandTest_order` went 7/8 → 8/8.

**Research:** the guarantee is now load-bearing in two implementations but stated in neither
public API. Decide whether `NodeService.getByIds` *promises* request order (then document it,
and the SPI must too) or does not (then find and fix the callers that assume it). Audit other
"returns a collection" SPI methods for the same unstated dependency.

## 4. BM25 vs TF-IDF: boosts no longer mean what they meant

**Observed:** `fulltext('title, description^5', …)` returns a different order on OpenSearch
than ES 2.4 — a long boosted `description` match outscores an unboosted `title` match under
BM25's length normalization but not under TF-IDF. ES 2.4's `classic` similarity no longer
exists, so it cannot be pinned back.

**Evidence:** Phase 4 Gate D; `FindNodesByQueryCommandTest_func_fulltext#boost_field`
(13/14 nodb vs 14/14 ES); corpus rows `TEXT-03`, `TEXT-04`, `TEXT-14` pass as SET matches with
documented order deltas.

**Research:** this is tolerated by the phase's acceptance rule but it is a *product* question,
not a test question. Are XP's documented boost semantics still accurate? Do the default
weights in Content Studio search and in `lib-content` need re-tuning for BM25? Is BM25
actually better for typical XP content (long article bodies vs short titles)? Measure on real
content before deciding.

## 5. Mixed mappings across repos: sort behaviour differs from ES

**Observed:** a multi-repo sort on a field present in only one repo **errors** on ES 2.4
(`IndexException`), while nodb returns hits. The control (a field present in *no* repo)
succeeds on both, isolating the mixed mapping as the cause.

**Evidence:** Phase 4 Gate C; corpus rows `SOURCE-03`, `SOURCE-04`; required the `FIXED`
acceptance tag.

**Research:** is "return hits" the behaviour we want, or should a mixed-mapping sort fail
loudly? Returning hits is friendlier and matches `unmapped_type` semantics; erroring matches
today. Cross-project search over repos with divergent dynamic mappings is normal in XP, so
this will be hit in practice — worth deciding deliberately rather than inheriting.

## 6. XP's index layout leaned on ES-2.4-isms that silently no-op (three found — assume more)

**Observed:** three blockers of one family, each found only by indexing a real document and
querying it, each failing *silently* rather than erroring:
1. the text value type had an **empty postfix**, so `data.x` and `data.x._analyzed` collided
   under dot-expansion (fails on the first document of every repo);
2. all 91 dynamic templates used `match` where OpenSearch needs `path_match` (index create and
   indexing succeed; **queries return zero hits, no error**);
3. `path_match: "*"` also matches **object** paths, so the catch-all mapped `data` itself as a
   keyword (again fails on the first document).
Plus two query-time equivalents: `pathMatch` silently matched only exact paths because the
index-level `default_search` analyzer wins over a field's `analyzer`; and OpenSearch flipping
`require_field_match` to `true` would have silently dismantled highlight's three-field
expansion.

**Evidence:** Phase 4 Gates A and D.

**Research:** the pattern is "ES 2.4 tolerated a shape whose modern equivalent is a no-op, not
an error". The golden corpus is the net that caught all five, which is an argument for
*extending* it rather than trusting review. Worth an explicit sweep for remaining 2.4-isms
before Gate F, and a standing rule: any mapping or analyzer change is proven by indexing a
document and querying it, never by inspection.

## 7. Deliberately reproduced quirks (decide whether to keep at GA)

- **`maxScore` is `NaN`** whenever results are sorted by field, and on every `GET_ALL` query
  (ES reads it from the final, empty scroll page). Reproduced in nodb for parity.
- **`explain` is silently dropped on `GET_ALL`** — the scroll path never set it. Reproduced.
- **An empty `fulltext()` query string returns `match_all`**, not zero hits. Reproduced.
  A real footgun for `fulltext(field, userInput)` with empty input.
- **`fulltext()` is accent-insensitive while a raw term match is not** (asciifolding is in the
  fulltext analyzer chain only). Correct, but surprising; documentation-worthy.

**Research:** each is bug-for-bug parity today. Decide per item whether GA keeps it, fixes it
with a release note, or exposes it as an option.

## 8. Smaller items worth a look

- **`returnFields` is never exposed to applications** (zero occurrences in `modules/lib/` or
  core-api's node package), which is why `_allText`'s computed value "cannot be read back".
  The value is in `_source` and the SPI already reads it — closing the gap is an API addition,
  independent of storage. See DESIGN §5 "Virtual / derived fields".
- **`_allText` has no base (`_text`) variant** — only analyzer-shaped copies, so it has no
  canonical retrievable form; and `_source` carries the concatenated text twice. Multi-fields
  fit here (pure analyzer derivatives) though not `_orderby` (independently computed).
- **`document_index_default` and `fulltext_search_default` are byte-identical** analyzers —
  two names, one definition. Kept as two for Gate D's references; collapse later.
- **Highlighting forces `type: plain`**, which re-analyzes `_source` per field per document —
  three times per highlighted property under the three-field expansion. OpenSearch's default
  `unified` may be materially faster; measure before Gate G's baseline.
- **`ReindexResult` accumulates every node id**, so reindex is O(N) in memory regardless of
  how the query is fed. Pre-existing on ES; means "reindex is memory-safe at scale" is not yet
  a claim we can make.
- **Fixture granularity:** `FindNodesByMultiRepoQueryCommandTest` and
  `VersionTableVacuumTaskTest` cannot pass as *classes* in nodb mode because they rely on a
  per-method ES index wipe while the nodb tenant is class-scoped. Gate F needs per-method
  nodb tenants.
- **DESIGN risk #8 is confirmed reachable:** an intra-push rename swap violates
  `unique(repo_key, branch, node_path)` (`PushNodesCommandTest` 15/16). Needs `DEFERRABLE`
  unique or equivalent, scheduled with the subtree-move work.
