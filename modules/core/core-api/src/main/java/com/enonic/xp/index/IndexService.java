package com.enonic.xp.index;

import com.google.common.annotations.Beta;

@Beta
public interface IndexService
{
    boolean isMaster();

    ReindexResult reindex( ReindexParams params );

    void purgeSearchIndex( PurgeIndexParams params );
}
