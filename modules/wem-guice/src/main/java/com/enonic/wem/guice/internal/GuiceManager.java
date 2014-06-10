package com.enonic.wem.guice.internal;

import org.ops4j.peaberry.Peaberry;
import org.osgi.framework.BundleContext;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import com.enonic.wem.guice.OsgiModule;
import com.enonic.wem.guice.internal.service.ServiceManager;

public final class GuiceManager
    extends AbstractModule
{
    private BundleContext context;

    private Injector injector;

    private ServiceManager serviceManager;

    private OsgiModule module;

    public GuiceManager context( final BundleContext context )
    {
        this.context = context;
        return this;
    }

    public GuiceManager module( final OsgiModule module )
    {
        this.module = module;
        return this;
    }

    @Override
    protected void configure()
    {
        install( Peaberry.osgiModule( this.context ) );
        install( this.module );
    }

    public void activate()
    {
        this.injector = Guice.createInjector( this );
        this.injector.injectMembers( this.module );

        this.serviceManager = new ServiceManager( this.injector );
        this.serviceManager.exportAll();
    }

    public void deactivate()
    {
        try
        {
            this.serviceManager.unexportAll();
        }
        finally
        {
            this.serviceManager = null;
            this.injector = null;
        }
    }
}
