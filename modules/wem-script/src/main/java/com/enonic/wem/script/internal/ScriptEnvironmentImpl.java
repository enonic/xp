package com.enonic.wem.script.internal;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import com.google.common.collect.Maps;

import com.enonic.wem.script.ScriptLibrary;

@Singleton
public final class ScriptEnvironmentImpl
    implements ScriptEnvironment, ServiceTrackerCustomizer
{
    private final Map<String, ScriptLibrary> libraries;

    private BundleContext bundleContext;

    private ServiceTracker serviceTracker;

    private boolean rebuild;

    public ScriptEnvironmentImpl()
    {
        this.libraries = Maps.newHashMap();
        this.rebuild = false;
    }

    @Inject
    public void setBundleContext( final BundleContext bundleContext )
    {
        this.bundleContext = bundleContext;
    }

    @PostConstruct
    public void start()
    {
        this.serviceTracker = new ServiceTracker( this.bundleContext, ScriptLibrary.class.getName(), this );
        this.serviceTracker.open();
    }

    @PreDestroy
    public void stop()
    {
        this.serviceTracker.close();
    }

    @Override
    public ScriptLibrary getLibrary( final String name )
    {
        rebuildIfNeeded();
        return this.libraries.get( name );
    }

    @Override
    public Object addingService( final ServiceReference reference )
    {
        this.rebuild = true;
        return this.bundleContext.getService( reference );
    }

    @Override
    public void modifiedService( final ServiceReference reference, final Object service )
    {
        this.rebuild = true;
    }

    @Override
    public void removedService( final ServiceReference reference, final Object service )
    {
        this.rebuild = true;
    }

    private void rebuildIfNeeded()
    {
        if ( !this.rebuild )
        {
            return;
        }

        rebuildStructures();
        this.rebuild = false;
    }

    private synchronized void rebuildStructures()
    {
        this.libraries.clear();
        for ( final ServiceReference reference : this.serviceTracker.getServiceReferences() )
        {
            rebuildStructures( reference );
        }
    }

    private void rebuildStructures( final ServiceReference reference )
    {
        final ScriptLibrary library = (ScriptLibrary) this.bundleContext.getService( reference );
        if ( library != null )
        {
            this.libraries.put( library.getName(), library );
        }
    }
}
