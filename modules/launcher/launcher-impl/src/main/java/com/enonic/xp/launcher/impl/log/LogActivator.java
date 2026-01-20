package com.enonic.xp.launcher.impl.log;

import java.util.AbstractMap.SimpleEntry;
import java.util.function.Supplier;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogReaderService;
import org.osgi.service.log.admin.LoggerAdmin;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.bridge.SLF4JBridgeHandler;

public class LogActivator
    implements BundleActivator
{
    static
    {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }

    private volatile ServiceTracker<LoggerAdmin, LogReaderServiceTracker> loggerAdminServiceTracker;

    @Override
    public void start( final BundleContext bundleContext )
    {
        this.loggerAdminServiceTracker = new LoggerAdminServiceTracker( bundleContext );
        this.loggerAdminServiceTracker.open();
    }

    @Override
    public void stop( BundleContext bundleContext )
    {
        this.loggerAdminServiceTracker.close();
    }

    private static class LoggerAdminServiceTracker
        extends ServiceTracker<LoggerAdmin, LogReaderServiceTracker>
    {
        final BundleContext bundleContext;

        LoggerAdminServiceTracker( final BundleContext bundleContext )
        {
            super( bundleContext, LoggerAdmin.class, null );
            this.bundleContext = bundleContext;
        }

        @Override
        public LogReaderServiceTracker addingService( final ServiceReference<LoggerAdmin> reference )
        {
            return withLocalClassloader( () -> {
                final LoggerAdmin loggerAdmin = bundleContext.getService( reference );
                final LogReaderServiceTracker logReaderServiceTracker = new LogReaderServiceTracker( bundleContext, loggerAdmin );
                logReaderServiceTracker.open();
                return logReaderServiceTracker;
            } );
        }

        @Override
        public void removedService( final ServiceReference<LoggerAdmin> reference, final LogReaderServiceTracker logReaderServiceTracker )
        {
            withLocalClassloader( () -> {
                logReaderServiceTracker.close();
                return null;
            } );
        }
    }

    private static class LogReaderServiceTracker
        extends ServiceTracker<LogReaderService, SimpleEntry<LogReaderService, LogbackLogListener>>
    {
        final LoggerAdmin loggerAdmin;

        LogReaderServiceTracker( final BundleContext context, final LoggerAdmin loggerAdmin )
        {
            super( context, LogReaderService.class, null );
            this.loggerAdmin = loggerAdmin;
        }

        @Override
        public SimpleEntry<LogReaderService, LogbackLogListener> addingService( final ServiceReference<LogReaderService> reference )
        {
            return withLocalClassloader( () -> {
                final LogReaderService logReaderService = context.getService( reference );
                final LogbackLogListener logbackLogListener = new LogbackLogListener( loggerAdmin );
                logReaderService.addLogListener( logbackLogListener );
                return new SimpleEntry<>( logReaderService, logbackLogListener );
            } );
        }

        @Override
        public void removedService( final ServiceReference<LogReaderService> reference,
                                    final SimpleEntry<LogReaderService, LogbackLogListener> entry )
        {
            withLocalClassloader( () -> {
                entry.getKey().removeLogListener( entry.getValue() );
                return null;
            } );
        }
    }

    private static <R> R withLocalClassloader( final Supplier<R> action )
    {
        final Thread currentThread = Thread.currentThread();
        final ClassLoader originalClassloader = currentThread.getContextClassLoader();
        try
        {
            currentThread.setContextClassLoader( LogActivator.class.getClassLoader() );
            return action.get();
        }
        finally
        {
            currentThread.setContextClassLoader( originalClassloader );
        }
    }
}
