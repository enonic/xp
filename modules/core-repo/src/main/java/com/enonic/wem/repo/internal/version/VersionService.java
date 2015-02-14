package com.enonic.wem.repo.internal.version;

import com.enonic.xp.core.node.NodeVersionDiffQuery;
import com.enonic.xp.core.node.NodeVersionDiffResult;
import com.enonic.xp.core.repository.RepositoryId;
import com.enonic.xp.core.node.FindNodeVersionsResult;
import com.enonic.xp.core.node.NodeVersion;
import com.enonic.xp.core.node.NodeVersionId;

public interface VersionService
{
    public void store( final NodeVersionDocument nodeVersionDocument, final RepositoryId repositoryId );

    public NodeVersion getVersion( final NodeVersionId versionId, final RepositoryId repositoryId );

    public FindNodeVersionsResult findVersions( final GetVersionsQuery query, final RepositoryId repositoryId );

    public NodeVersionDiffResult diff( final NodeVersionDiffQuery query, final RepositoryId repositoryId );

}
