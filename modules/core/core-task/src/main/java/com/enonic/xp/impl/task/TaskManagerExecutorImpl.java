package com.enonic.xp.impl.task;

import java.time.Duration;
import java.util.concurrent.Executors;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.core.internal.concurrent.SimpleExecutor;

@Component
public class TaskManagerExecutorImpl
    implements TaskManagerExecutor
{
    private static final Logger LOG = LoggerFactory.getLogger( TaskManagerExecutorImpl.class );

    private final SimpleExecutor simpleExecutor;

    public TaskManagerExecutorImpl()
    {
        simpleExecutor =
            new SimpleExecutor( Executors::newCachedThreadPool, "task-manager-thread-%d", e -> LOG.error( "Task execution failed", e ) );
    }

    @Deactivate
    public void deactivate()
    {
        simpleExecutor.shutdownAndAwaitTermination( Duration.ofSeconds( 5 ), neverCommenced -> LOG.warn( "Not all tasks were executed" ) );
    }

    @Override
    public void execute( final Runnable command )
    {
        simpleExecutor.execute( command );
    }
}
