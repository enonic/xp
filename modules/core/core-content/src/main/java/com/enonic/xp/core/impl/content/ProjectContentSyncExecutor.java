package com.enonic.xp.core.impl.content;

import java.time.Duration;
import java.util.concurrent.Executor;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.core.internal.concurrent.SimpleExecutor;

@Component(service = ProjectContentSyncExecutor.class)
public class ProjectContentSyncExecutor
    implements Executor
{
    private static final Logger LOG = LoggerFactory.getLogger( ProjectContentSyncExecutor.class );

    private final SimpleExecutor simpleExecutor;

    public ProjectContentSyncExecutor()
    {
        this.simpleExecutor = SimpleExecutor.ofSingle( "project-node-sync-thread",
                                                  e -> LOG.error( "Project node sync failed", e ) );
    }

    @Override
    public void execute( final Runnable command )
    {
        simpleExecutor.execute( command );
    }

    @Deactivate
    public void deactivate()
    {
        this.simpleExecutor.shutdownAndAwaitTermination( Duration.ofSeconds( 5 ), neverCommenced -> {
            LOG.warn( "Not all project layers were synchronized" );
        } );
    }
}
