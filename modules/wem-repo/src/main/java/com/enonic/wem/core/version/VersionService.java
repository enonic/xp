package com.enonic.wem.core.version;

import com.enonic.wem.api.repository.RepositoryId;
import com.enonic.wem.api.node.FindNodeVersionsResult;
import com.enonic.wem.api.node.NodeVersion;
import com.enonic.wem.api.node.NodeVersionId;

public interface VersionService
{
    public void store( final NodeVersionDocument nodeVersionDocument, final RepositoryId repositoryId );

    public NodeVersion getVersion( final NodeVersionId versionId, final RepositoryId repositoryId );

    public FindNodeVersionsResult findVersions( final GetVersionsQuery query, final RepositoryId repositoryId );

}
