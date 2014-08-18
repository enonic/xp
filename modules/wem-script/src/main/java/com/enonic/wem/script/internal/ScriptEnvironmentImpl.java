package com.enonic.wem.script.internal;

import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import com.google.common.collect.Maps;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.script.ScriptContributor;
import com.enonic.wem.script.ScriptEnvironment;

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

    public void setBundleContext( final BundleContext bundleContext )
    {
        this.bundleContext = bundleContext;
    }

    public void start()
    {
        this.serviceTracker = new ServiceTracker( this.bundleContext, ScriptContributor.class.getName(), this );
        this.serviceTracker.open();
    }

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
    public Map<String, Object> getGlobalVariables()
    {
        rebuildIfNeeded();
        return this.variables;
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
        this.variables.putAll( contributor.getGlobalVariables() );

        for ( final Map.Entry<String, String> library : contributor.getLibraries().entrySet() )
        {
            this.libraries.put( library.getKey(), ResourceKey.from( moduleKey, library.getValue() ) );
        }
    }
}
