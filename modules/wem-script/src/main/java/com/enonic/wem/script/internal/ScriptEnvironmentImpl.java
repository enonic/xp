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

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.script.ScriptContributor;

@Singleton
public final class ScriptEnvironmentImpl
    implements ScriptEnvironment, ServiceTrackerCustomizer
{
    private final Map<String, ResourceKey> libraries;

    private final Map<String, Object> variables;

    private BundleContext bundleContext;

    private ServiceTracker serviceTracker;

    private boolean rebuild;

    public ScriptEnvironmentImpl()
    {
        this.libraries = Maps.newHashMap();
        this.variables = Maps.newHashMap();
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
        this.serviceTracker = new ServiceTracker( this.bundleContext, ScriptContributor.class.getName(), this );
        this.serviceTracker.open();
    }

    @PreDestroy
    public void stop()
    {
        this.serviceTracker.close();
    }

    @Override
    public ResourceKey getLibrary( final String name )
    {
        rebuildIfNeeded();
        return this.libraries.get( name );
    }

    @Override
    public Object getVariable( final String name )
    {
        rebuildIfNeeded();
        return this.variables.get( name );
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
        this.variables.clear();

        for ( final ServiceReference reference : this.serviceTracker.getServiceReferences() )
        {
            rebuildStructures( reference );
        }
    }

    private void rebuildStructures( final ServiceReference reference )
    {
        final ModuleKey moduleKey = ModuleKey.from( reference.getBundle() );
        final ScriptContributor contributor = (ScriptContributor) this.bundleContext.getService( reference );

        if ( contributor != null )
        {
            rebuildStructures( moduleKey, contributor );
        }
    }

    private void rebuildStructures( final ModuleKey moduleKey, final ScriptContributor contributor )
    {
        this.variables.putAll( contributor.getVariables() );

        for ( final Map.Entry<String, String> library : contributor.getLibraries().entrySet() )
        {
            this.libraries.put( library.getKey(), ResourceKey.from( moduleKey, library.getValue() ) );
        }
    }
}
