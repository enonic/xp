package com.enonic.wem.repo.internal.version;

import com.enonic.wem.repo.internal.InternalContext;
import com.enonic.xp.node.FindNodeVersionsResult;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionDiffQuery;
import com.enonic.xp.node.NodeVersionDiffResult;
import com.enonic.xp.node.NodeVersionId;

public interface VersionService
{
    void store( final NodeVersionDocument nodeVersionDocument, final InternalContext context );

    NodeVersion getVersion( final NodeVersionId versionId, final InternalContext context );

    FindNodeVersionsResult findVersions( final GetVersionsQuery query, final InternalContext context );

    NodeVersionDiffResult diff( final NodeVersionDiffQuery query, final InternalContext context );
}
