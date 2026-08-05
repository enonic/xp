# ┌──────────────────────────────────────────────────────────────────────────────────────┐
# │ SUPERSEDED — NOT USED, NOT BUILT. Kept as the record of why it is unnecessary.        │
# │                                                                                      │
# │ Phase 4 Gate A / decision D8 (nodb/BUILD-PHASE-4.md): NoDB computes the ICU          │
# │ collation keys for `*._orderby_<loc>` ITSELF (engine/.../search/CollationKeyResolver, │
# │ icu4j pinned in nodb/gradle/libs.versions.toml) and stores them as a plain `keyword`. │
# │ The 44 `icu_collation` filters were analysis-icu's ONLY consumer, so with them gone   │
# │ the plugin is needed for NOTHING and the stack runs the STOCK                         │
# │ opensearchproject/opensearch:3.7.0 image — see dev-stack.sh and docker-compose.yml.   │
# │                                                                                      │
# │ What that buys, beyond deleting a build step: no managed-service plugin-parity risk   │
# │ (AWS preinstalls ICU today, but the dependency is now simply absent), and the sort    │
# │ contract stops depending on the ENGINE's icu4j — the 3.7.0 image bundles 77.1, older  │
# │ than the 78.3 both XP and NoDB pin. The version-pinning and plugin-inventory notes    │
# │ below remain accurate and are the reasoning that led here; delete this file only when │
# │ the reasoning is no longer worth having.                                              │
# └──────────────────────────────────────────────────────────────────────────────────────┘
#
# enonic/nodb-opensearch — OpenSearch for NoDB, with the analysis plugins XP's
# index settings require.
#
# WHY THIS IMAGE EXISTED
#   Amazon OpenSearch Service (the managed target) ships the ICU Analysis plugin
#   preinstalled on every domain. The vanilla opensearchproject/opensearch image does
#   NOT. XP's search-settings.json declares 44 `icu_collation` token filters feeding 44
#   `icu_sort_<lang>` analyzers, so a plain image cannot even create the index. This
#   thin derived image closes exactly that gap and nothing else — dev/self-hosted gets
#   the same analysis surface as the managed service.
#
# VERSION PINNING (deliberate, do not float)
#   Pinned to the newest release Amazon OpenSearch Service actually offers (3.7,
#   GA in all regions 2026-07-30). OpenSearch OSS 3.8.0 shipped 2026-08-04 but the managed
#   service does not run it — floating tags (`:3`, `:latest`) already point at 3.8.0, i.e.
#   past what the managed target supports. That is why the previous
#   `opensearchproject/opensearch:3` in docker-compose.yml was wrong even before ICU.
#   Amazon OpenSearch Service adopts only every OTHER 3.x minor — 3.1, 3.3, 3.5, 3.7 —
#   so the next legitimate bump is 3.9, not 3.8. Verify before bumping:
#     https://docs.aws.amazon.com/opensearch-service/latest/developerguide/what-is.html#choosing-version
#   ICU collation keys are CLDR-version-dependent: two hosts on different ICU versions
#   sort "æ ø å" differently. Sort order must be a property of the pinned version, not
#   of the environment, so dev, CI and cloud pin the same patch release.
#
# PLUGIN INVENTORY — why only ICU
#   XP today depends on exactly ONE Elasticsearch plugin: analysis-icu (see
#   gradle/libs.versions.toml: org.elasticsearch.plugin:analysis-icu:2.4.6). Every other
#   analyzer named in search-mapping.json (arabic ... turkish, cjk, thai, sorani,
#   norwegian, plus the light_nynorsk stemmer) is core Lucene and needs no plugin. XP
#   maps ja/ko/zh stemming onto the core `cjk` analyzer, so kuromoji / nori / smartcn are
#   NOT required. No phonetic, stempel or ukrainian analyzer is referenced anywhere.
#   => analysis-icu is necessary and sufficient.
FROM opensearchproject/opensearch:3.7.0

RUN /usr/share/opensearch/bin/opensearch-plugin install --batch analysis-icu
