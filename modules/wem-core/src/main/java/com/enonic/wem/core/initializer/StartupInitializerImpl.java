package com.enonic.wem.core.initializer;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import com.enonic.wem.core.index.Index;
import com.enonic.wem.core.index.IndexService;

final class StartupInitializerImpl
    implements StartupInitializer
{
    private final List<InitializerTask> tasks;

    private final IndexService indexService;

    @Inject
    public StartupInitializerImpl( final IndexService indexService, final Set<InitializerTask> tasks )
    {
        this.indexService = indexService;

        final List<InitializerTask> sortedTaskList = Lists.newArrayList( tasks );
        Collections.sort( sortedTaskList );
        this.tasks = sortedTaskList;
    }

    public void initialize( final boolean reinit )
        throws Exception
    {
        if ( reinit )
        {
            cleanupOldData();
        }

        for ( final InitializerTask task : this.tasks )
        {
            task.initialize();
        }
    }

    private void cleanupOldData()
    {
        this.indexService.deleteIndex( Index.NODB );
        this.indexService.createIndex( Index.NODB );
        this.indexService.deleteIndex( Index.STORE );
        this.indexService.createIndex( Index.STORE );
        this.indexService.deleteIndex( Index.WORKSPACE );
        this.indexService.createIndex( Index.WORKSPACE );
    }
}
