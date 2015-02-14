package com.enonic.xp.index;

public interface IndexService
{
    public ReindexResult reindex( ReindexParams params );

    public void purgeSearchIndex( PurgeIndexParams params );

}
