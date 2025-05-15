package com.enonic.xp.index;

import java.util.Map;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.repository.RepositoryId;

@PublicApi
public interface IndexService
{
    // Check if node is master
    boolean isMaster();

    UpdateIndexSettingsResult updateIndexSettings( UpdateIndexSettingsParams params );

    Map<String, String> getIndexSettings( RepositoryId repositoryId, IndexType indexType );

    Map<String, Object> getIndexMapping( RepositoryId repositoryId, Branch branch, IndexType indexType );

    ReindexResult reindex( ReindexParams params );

    boolean waitForYellowStatus();
}
