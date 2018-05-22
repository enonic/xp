package com.enonic.xp.init;

import com.enonic.xp.index.IndexService;

public abstract class ExternalInitializer
    extends Initializer
{
    protected final IndexService indexService;

    protected ExternalInitializer( final IndexService indexService )
    {
        this.indexService = indexService;
    }

    @Override
    protected boolean isMaster()
    {
        return indexService.isMaster();
    }
}
