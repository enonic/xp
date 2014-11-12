package com.enonic.wem.core.version;

import com.enonic.wem.api.repository.RepositoryId;
import com.enonic.wem.repo.FindNodeVersionsResult;
import com.enonic.wem.repo.NodeVersion;
import com.enonic.wem.repo.NodeVersionId;

public interface VersionService
{
    public void store( final NodeVersionDocument nodeVersionDocument, final RepositoryId repositoryId );

    public NodeVersion getVersion( final NodeVersionId versionId, final RepositoryId repositoryId );

    public FindNodeVersionsResult findVersions( final GetVersionsQuery query, final RepositoryId repositoryId );

}
