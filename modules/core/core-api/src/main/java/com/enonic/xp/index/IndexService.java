package com.enonic.xp.index;

import com.google.common.annotations.Beta;

import com.enonic.xp.repository.RepositoryIds;

@Beta
public interface IndexService
{
    // Check if node is master
    boolean isMaster();

    UpdateIndexSettingsResult updateIndexSettings( final UpdateIndexSettingsParams params );

    void triggerReadOnlyMode( final boolean readOnly, final RepositoryIds repositoryIds );

    ReindexResult reindex( ReindexParams params );

    void purgeSearchIndex( PurgeIndexParams params );
}
