# NoDB Build — Phase 0 Work Order (Storage SPI inside XP)

**Read first:** `nodb/DESIGN.md` §3 (the SPI), §9 (phases), §10 (risk register);
`nodb/spi/*.java` (shape reference — drafts, superseded by what this phase creates);
`nodb/BUILD-SLICE-1.md` (style/gate conventions). This work order is self-contained —
do not require prior conversation context.

## Goal

Extract a **storage SPI** inside the XP codebase and refactor the existing embedded-ES
code to implement it, with **zero behavior change**: the full XP test suite runs green
with no test modifications. This is pure refactoring, shippable in an 8.x. It is the
prerequisite for wiring `nodb-client` into XP (Phase 1 completion).

**This is surgery on live code, not greenfield.** The prime directive: XP's test suite
green after EVERY gate; small, single-purpose commits; no drive-by cleanups, renames,
or "while I'm here" improvements. If a step balloons far past its estimate, STOP and
record the blocker here rather than grinding.

## Non-goals (do not build)

No NoDB/gRPC/Postgres anything. No behavior change of any kind. No removal or
deprecation of embedded ES. No public API (`core-api`) changes except where explicitly
listed. No JS-API or app-facing changes. No test modifications (adding NEW tests is
fine). No dependency version bumps. No module split of the ES code (stretch gate E only).

## Ground truth (from architecture investigation — verify in Gate 0, don't re-derive)

- The seam: `StorageDao` (`modules/core/core-repo/.../repo/impl/storage/StorageDao.java`),
  `SearchDao` (`.../repo/impl/search/SearchDao.java`), `IndexServiceInternal`
  (`.../repo/impl/index/IndexServiceInternal.java`), `SnapshotService` (interface already
  in core-api `com.enonic.xp.snapshot`). Sole implementations live under
  `.../repo/impl/elasticsearch/`.
- ~101 files in core-repo import `org.elasticsearch.*`; all but THREE are inside the
  `elasticsearch/` package. The three leaks (exception types only):
  `NodeServiceImpl` + `RefreshCommand` (`IndexNotFoundException`),
  `RepositoryCreator` (`IndexAlreadyExistsException`).
- Package-position leak: `IndexDataService.store(IndexDocument, ...)` — `IndexDocument`
  is a plain DTO but lives under `repo.impl.elasticsearch.document` while being consumed
  by ES-free code.
- The service layer above the seam (`NodeStorageService`, `BranchService`,
  `VersionService`, `CommitService`, `IndexDataService`, `NodeSearchService`) is already
  ES-free. Storage doc shapes: BRANCH/VERSION/COMMIT documents built by
  `BranchStorageRequestFactory` / `VersionStorageDocFactory` /
  `CommitStorageRequestFactory` (field lists = DESIGN.md §4 records).
- Blob payloads (`NodeVersionService`/`BinaryService` → `BlobStore`) are ALREADY behind
  a clean SPI and are NOT part of this phase — with the ES backend, payloads stay in the
  blobstore exactly as today. (NoDB's payload table enters in Phase 1+, behind the same
  service interfaces.)

## Branch & baseline

Work on a NEW branch `storage-spi-phase0` off `master` (NOT off nodb-design — the nodb/
directory is unrelated to the XP build; cherry-pick nothing from it).

## Gates

### Gate 0 — Baseline & inventory (small)
- Discover exact Gradle test tasks: core-repo unit tests, and which itest suites cover
  node/repo storage (look at settings.gradle + modules/itest). Record commands + green
  baseline timings in this file (append a "Baseline" section).
- Verify the ground-truth inventory above with grep; list any drift.
- Add the architectural test now (it will fail — that's the point; keep it disabled or
  scoped to the known-leaks allowlist until Gate A): a JUnit test (ArchUnit if already
  on the classpath somewhere; otherwise a simple classpath/import scanner) asserting no
  `org.elasticsearch` imports in core-repo outside `com.enonic.xp.repo.impl.elasticsearch`
  and an explicit allowlist. **Gate: baseline suites green, inventory recorded.**

### Gate A — SPI module + leak fixes (est. ~300k tokens)
- New XP module `modules/core/core-storage-spi` (copy build conventions from a small
  existing module, e.g. core-blobstore; wire into settings.gradle; OSGi bundle exporting
  `com.enonic.xp.storage.spi` — internal/provisional, not an app-facing API).
- SPI contents (adapt from nodb/spi drafts, but Phase-0-realistic): typed records
  (BranchEntryRecord, VersionRecord, CommitRecord — field sets per DESIGN §4), the
  interfaces `NodeStore` (records only, NO payload methods this phase),
  `NodeSearchIndex` (search + per-repo index lifecycle + refresh), `RepositoryStorageAdmin`,
  and SPI exception types (`StorageIndexNotFoundException`, `StorageIndexExistsException`).
  SPI may depend on core-api (query AST types) — never on core-repo.
- Fix the three exception leaks: ES impls translate ES exceptions to SPI exceptions at
  the boundary; `NodeServiceImpl`/`RefreshCommand`/`RepositoryCreator` catch SPI types.
- Move `IndexDocument` (and its ES-free document family) out of the `elasticsearch`
  package to `com.enonic.xp.repo.impl.index.document` (pure move, fix imports).
- Enable the arch test with an empty allowlist for the moved/fixed items.
- **Gate: core-repo tests green + arch test green (elasticsearch package confined).**
- Update DESIGN.md §10 loose-end #10 (IndexDocument) when done.

### Gate B — Storage side behind typed SPI (est. ~800k tokens; the big one)
- Implement `NodeStore` in the ES backend (`.../elasticsearch/`): a class adapting the
  typed records onto the EXISTING `*StorageRequestFactory` + `StorageDaoImpl` machinery
  (records → StoreRequests → ES). No serialization changes — byte-identical documents.
- Retarget the ES-free service layer: `BranchServiceImpl`, `VersionServiceImpl`,
  `CommitServiceImpl` (store/get/delete paths incl. multi-get, branches-of-node,
  forceRefresh flags) consume SPI `NodeStore` instead of `StorageDao` + factories.
  `StorageDao` and the request factories become internal to the ES backend package.
- Preserve semantics exactly: routing/parent behavior, forceRefresh, SearchPreference
  — these live INSIDE the ES NodeStore impl now.
- **Gate: core-repo tests + storage/node itest suites green, no test changes.**

### Gate C — Search, index admin, snapshots behind SPI (est. ~600k tokens)
- `SearchDao` → SPI: move the already-ES-free `SearchRequest`/`SearchResult` DTO family
  into the SPI module (mechanical import churn); `NodeSearchServiceImpl` consumes SPI
  `NodeSearchIndex`; `SearchDaoImpl` implements it.
- `IndexServiceInternal` consumers: route index create/delete/exists/settings/mapping/
  refresh through `RepositoryStorageAdmin` + `NodeSearchIndex.refresh`. Investigate the
  consumers of `waitForYellowStatus`/`isMaster`/`closeIndices`/`openIndices` (boot &
  repo-init paths) and keep those on a NARROW ES-internal interface — they must not
  cross the SPI (DESIGN §3.2). Document who the consumers are.
- `SnapshotServiceImpl` stays the sole binding of the core-api `SnapshotService`; just
  confirm no new leaks. No SPI snapshot interface needed while ES is the only backend —
  note this as a Phase-1 decision instead of inventing one now.
- **Gate: full core-repo + query/search itest suites green.**

### Gate D — Wiring & enforcement (est. ~300k tokens)
- OSGi wiring: SPI interfaces are registered as OSGi services BY the ES backend's
  components (SCR); consumers `@Reference` the SPI types. No provider-selection
  machinery yet (only one backend exists) — but the registration property
  `storage.backend=elasticsearch` is set, so Phase 1 selection is a filter, not a rewrite.
- Arch test final: `org.elasticsearch` imports allowed ONLY under
  `com.enonic.xp.repo.impl.elasticsearch` (and core-elasticsearch/repack modules).
- **Gate: FULL XP build incl. all itest suites green; arch enforcement on.**
- Update DESIGN.md §9 (Phase 0 status) and §10; append actuals (tokens, duration,
  file counts) to this file.

### Gate E — STRETCH, only if D lands cleanly with budget left
- Extract `.../repo/impl/elasticsearch/**` into its own module
  `modules/core/core-storage-elasticsearch` (bundle split, SCR wiring, repack deps).
  Purely mechanical after D. If any friction: skip, it's not required for Phase 1.

## Execution guidance

- Model mix: mechanical moves/import-churn/codegen → cheap-model subagents; the Gate B
  retargeting, OSGi wiring, and every gate review → strongest model. Review each gate's
  diff against DESIGN.md §3 before declaring it done.
- Budget: ~2M output tokens total for A–D. One gate per session is a fine cadence;
  gates are individually committable (suite green) so sessions can end safely anywhere.
- Fast loop: module-scoped test tasks; full itest suites only at gate boundaries.
- Commit per gate on `storage-spi-phase0`; commit messages state "no behavior change"
  and name the gate. Never commit with a red suite.

## Definition of done

Full XP test suite green with zero test modifications; arch test enforcing ES
confinement; SPI module documented (package-info) as internal/provisional; DESIGN.md
phase table + risk register updated; everything committed on `storage-spi-phase0`.
