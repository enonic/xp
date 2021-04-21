package com.enonic.xp.impl.scheduler;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.core.internal.concurrent.SimpleExecutor;

@Component
public class ScheduleAuditLogExecutorImpl
    implements ScheduleAuditLogExecutor
{
    private static final Logger LOG = LoggerFactory.getLogger( ScheduleAuditLogExecutorImpl.class );

    private final SimpleExecutor simpleExecutor;

    @Activate
    public ScheduleAuditLogExecutorImpl()
    {
        final Function<ThreadFactory, ExecutorService> executorServiceSupplier =
            ( threadFactory ) -> new ThreadPoolExecutor( 0, Runtime.getRuntime().availableProcessors(), 60L, TimeUnit.SECONDS,
                                                         new LinkedBlockingQueue<>( 100 ), threadFactory,
                                                         new ThreadPoolExecutor.CallerRunsPolicy() );

        this.simpleExecutor =
            new SimpleExecutor( executorServiceSupplier, "job-audit-log-thread-%d", e -> LOG.error( "Audit log failed", e ) );
    }

    @Deactivate
    public void deactivate()
    {
        simpleExecutor.shutdownAndAwaitTermination( Duration.ofSeconds( 5 ), neverCommenced -> LOG.warn( "Not all messages were logged" ) );
    }

    @Override
    public void execute( final Runnable command )
    {
        simpleExecutor.execute( command );
    }
}
