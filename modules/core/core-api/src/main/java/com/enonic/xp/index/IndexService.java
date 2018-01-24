package com.enonic.xp.index;

import com.google.common.annotations.Beta;

import com.enonic.xp.repository.IndexSettings;
import com.enonic.xp.repository.RepositoryId;

@Beta
public interface IndexService
{
    // Check if node is master
    boolean isMaster();

    UpdateIndexSettingsResult updateIndexSettings( final UpdateIndexSettingsParams params );

    IndexSettings getIndexSettings( final RepositoryId repositoryId, final IndexType indexType );

    ReindexResult reindex( ReindexParams params );

    void purgeSearchIndex( PurgeIndexParams params );
}
