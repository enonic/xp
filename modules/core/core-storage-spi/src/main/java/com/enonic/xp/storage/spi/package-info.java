/**
 * Internal, provisional storage SPI for the XP node/repo layer.
 * <p>
 * <b>Not an app-facing API.</b> This package is internal to XP core and subject to
 * change without notice or migration path — no compatibility guarantees across XP
 * versions while the extraction is in progress. It is not intended to be used by
 * applications, and its shape may still change during Phase 0/1.
 * <p>
 * This is Phase 0 of the storage-SPI extraction (see {@code nodb/BUILD-PHASE-0.md} and
 * {@code nodb/DESIGN.md} §3): the existing embedded-Elasticsearch code in {@code core-repo}
 * is being refactored to implement this SPI with zero behavior change. Two backends are
 * planned: the existing embedded Elasticsearch backend (the only one that exists today,
 * implemented in {@code com.enonic.xp.repo.impl.elasticsearch}) and, in Phase 1, a NoDB
 * backend. Backend implementations register their SPI services (e.g. {@link
 * com.enonic.xp.storage.spi.NodeStore}, {@link com.enonic.xp.storage.spi.NodeSearchIndex},
 * {@link com.enonic.xp.storage.spi.RepositoryStorageAdmin}) as OSGi (SCR) components with the
 * service property {@code storage.backend=<name>} (e.g. {@code elasticsearch}). In Phase 0,
 * with only one backend, consumers still plain-{@code @Reference} the SPI types; the property
 * exists so that Phase 1 backend selection is a {@code @Reference} target filter on
 * {@code storage.backend}, not a rewrite of the wiring.
 */
package com.enonic.xp.storage.spi;
