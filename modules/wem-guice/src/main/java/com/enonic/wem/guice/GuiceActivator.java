package com.enonic.wem.guice;

import java.util.ArrayList;

import org.ops4j.peaberry.Peaberry;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import com.enonic.wem.guice.binder.OsgiModule;
import com.enonic.wem.guice.binder.ServiceManager;
import com.enonic.wem.guice.listener.ListenerManager;

public abstract class GuiceActivator
    extends OsgiModule
    implements BundleActivator
{
    private BundleContext context;

    private Injector injector;

    private ServiceManager serviceManager;

    protected final BundleContext getContext()
    {
        return this.context;
    }

    @Override
    public final void start( final BundleContext context )
        throws Exception
    {
        this.context = context;
        startModule();
        doStart();
    }

    @Override
    public final void stop( final BundleContext context )
        throws Exception
    {
        try
        {
            stopModule();
            doStop();
        }
        finally
        {
            this.injector = null;
        }
    }

    private void startModule()
    {
        this.injector = Guice.createInjector( createModules( this.context ) );
        this.injector.injectMembers( this );

        this.serviceManager = new ServiceManager( this.injector );
        this.serviceManager.exportAll();

        new ListenerManager( this.injector ).bindAll();
    }

    private Iterable<Module> createModules( final BundleContext context )
    {
        final ArrayList<Module> modules = new ArrayList<>();
        modules.add( Peaberry.osgiModule( context ) );
        modules.add( this );
        return modules;
    }

    private void stopModule()
    {
        this.serviceManager.unexportAll();
    }

    protected void doStart()
        throws Exception
    {
        // Do nothing. Override to implement custom logic.
    }

    protected void doStop()
        throws Exception
    {
        // Do nothing. Override to implement custom logic.
    }
}
