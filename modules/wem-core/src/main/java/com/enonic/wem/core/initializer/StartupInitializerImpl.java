package com.enonic.wem.core.initializer;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import com.enonic.wem.core.index.Index;
import com.enonic.wem.core.index.IndexService;
import com.enonic.wem.core.lifecycle.LifecycleBean;
import com.enonic.wem.core.lifecycle.LifecycleStage;

final class StartupInitializerImpl
    extends LifecycleBean
    implements StartupInitializer
{
    private final static Logger LOG = LoggerFactory.getLogger( StartupInitializerImpl.class );

    @Inject
    protected Set<InitializerTask> tasks;

    @Inject
    protected IndexService indexService;

    public StartupInitializerImpl()
    {
        super( LifecycleStage.L5 );
    }

    @Override
    protected void doStart()
        throws Exception
    {
        initialize( false );
    }

    @Override
    protected void doStop()
        throws Exception
    {
        // Do nothing
    }

    public void initialize( final boolean reinit )
        throws Exception
    {
        if ( reinit )
        {
            cleanupOldData();
            doInitialize();
        }
        else if ( !isInitialized() )
        {
            doInitialize();
        }
    }

    protected void doInitialize()
        throws Exception
    {
        LOG.info( "Running all initializers..." );

        final List<InitializerTask> sortedTaskList = Lists.newArrayList( this.tasks );
        Collections.sort( sortedTaskList );

        for ( final InitializerTask task : sortedTaskList )
        {
            doInitialize( task );
        }
    }

    private void cleanupOldData()
    {
        LOG.info( "Recreating indexes..." );

        final Set<String> indicesNames = indexService.getAllIndicesNames();

        for ( final String indexName : indicesNames )
        {
            LOG.info( "Deleting index: " + indexName );
            this.indexService.deleteIndex( indexName );
        }

        this.indexService.createIndex( Index.WORKSPACE );
        this.indexService.createIndex( Index.VERSION );
    }

    private void doInitialize( final InitializerTask task )
        throws Exception
    {
        long tm = System.currentTimeMillis();

        LOG.info( "Running " + task.getClass().getSimpleName() + " initializer..." );
        task.initialize();
        LOG.info( "Executed " + task.getClass().getSimpleName() + " initializer in " + ( System.currentTimeMillis() - tm ) + " ms" );
    }

    private boolean isInitialized()
    {
        return this.indexService.countDocuments( Index.WORKSPACE ) > 0;
    }
}
