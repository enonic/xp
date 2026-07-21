package com.enonic.xp.repo.impl.node.dao;

import com.enonic.xp.node.NodeVersionKey;

/**
 * Result of {@link NodeVersionService#serialize}: the content-addressed key (three blob
 * keys, one per segment — unchanged shape, {@link NodeVersionKey} requires all three
 * non-null) together with the raw serialized bytes each hash was computed from. Pure data,
 * no I/O performed to produce it (Phase 3 Gate B, nodb/BUILD-PHASE-3.md — persistence of
 * these bytes moved from here into the storage SPI's {@code NodeStore#storeVersion}/
 * {@code #storeNode}, so both backends can persist them their own way).
 */
public record SerializedNodeVersion(NodeVersionKey key, byte[] nodeDataBytes, byte[] indexConfigBytes, byte[] accessControlBytes)
{
}
