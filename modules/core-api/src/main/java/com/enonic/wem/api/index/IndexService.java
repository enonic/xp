package com.enonic.wem.api.index;

public interface IndexService
{
    public ReindexResult reindex( ReindexParams params );

    public void purgeSearchIndex( PurgeIndexParams params );

}
