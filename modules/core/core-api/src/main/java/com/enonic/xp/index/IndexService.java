package com.enonic.xp.index;

import java.util.Map;

import com.enonic.xp.repository.RepositoryId;


public interface IndexService
{
    // Check if node is master
    boolean isMaster();

    UpdateIndexSettingsResult updateIndexSettings( UpdateIndexSettingsParams params );

    Map<String, String> getIndexSettings( RepositoryId repositoryId, IndexType indexType );

    ReindexResult reindex( ReindexParams params );

    boolean waitForYellowStatus();
}
