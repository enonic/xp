package com.enonic.xp.index;

import com.google.common.annotations.Beta;

@Beta
public interface IndexService
{
    // Check if node is master
    boolean isMaster();

    ReindexResult reindex( ReindexParams params );

    void purgeSearchIndex( PurgeIndexParams params );
}
