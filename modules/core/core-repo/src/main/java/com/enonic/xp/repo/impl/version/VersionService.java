package com.enonic.xp.repo.impl.version;

import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.storage.spi.NodeSegments;

public interface VersionService
{
    /**
     * Convenience overload for a version write that reuses an EXISTING
     * {@code NodeVersionKey} verbatim (commit finalization, attribute changes): the payload
     * segments are hash-only, built from {@code nodeVersion.getNodeVersionKey()} — no new
     * bytes to persist. Equivalent to calling {@link #store(NodeVersion, NodeSegments, InternalContext)}
     * with those hash-only segments.
     */
    void store( NodeVersion nodeVersion, InternalContext context );

    /** Explicit-segments overload, used when new payload bytes exist (first write of this version's content). */
    void store( NodeVersion nodeVersion, NodeSegments segments, InternalContext context );

    void delete( NodeVersionId nodeVersionId, InternalContext context );

    NodeVersion getVersion( NodeVersionId nodeVersionId, InternalContext context );
}
