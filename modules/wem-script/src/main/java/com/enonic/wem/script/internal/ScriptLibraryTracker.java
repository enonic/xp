package com.enonic.wem.script.internal;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import com.enonic.wem.script.ScriptLibrary;

@Singleton
final class ScriptLibraryTracker
    extends ServiceTracker
{
    private final ScriptEnvironment environment;

    @Inject
    public ScriptLibraryTracker( final BundleContext context, final ScriptEnvironment environment )
    {
        super( context, ScriptLibrary.class.getName(), null );
        this.environment = environment;
    }

    @Override
    public Object addingService( final ServiceReference reference )
    {
        final Object service = super.addingService( reference );
        if ( service instanceof ScriptLibrary )
        {
            this.environment.addLibrary( (ScriptLibrary) service );
        }

        return service;
    }

    @Override
    public void removedService( final ServiceReference reference, final Object service )
    {
        super.removedService( reference, service );

        if ( service instanceof ScriptLibrary )
        {
            this.environment.removeLibrary( (ScriptLibrary) service );
        }
    }
}
