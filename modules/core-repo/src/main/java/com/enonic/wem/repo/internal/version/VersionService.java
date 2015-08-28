package com.enonic.wem.repo.internal.version;

import com.enonic.xp.node.FindNodeVersionsResult;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionDiffQuery;
import com.enonic.xp.node.NodeVersionDiffResult;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.repository.RepositoryId;

public interface VersionService
{
   void store( final NodeVersionDocument nodeVersionDocument, final RepositoryId repositoryId );

   NodeVersion getVersion( final NodeVersionId versionId, final RepositoryId repositoryId );

   FindNodeVersionsResult findVersions( final GetVersionsQuery query, final RepositoryId repositoryId );

   NodeVersionDiffResult diff( final NodeVersionDiffQuery query, final RepositoryId repositoryId );

}
