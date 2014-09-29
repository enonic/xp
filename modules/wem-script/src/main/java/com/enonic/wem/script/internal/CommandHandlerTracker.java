package com.enonic.wem.script.internal;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import com.enonic.wem.script.command.CommandHandler;

@Singleton
final class CommandHandlerTracker
    extends ServiceTracker
{
    private final ScriptEnvironment environment;

    @Inject
    public CommandHandlerTracker( final BundleContext context, final ScriptEnvironment environment )
    {
        super( context, CommandHandler.class.getName(), null );
        this.environment = environment;
    }

    @Override
    public Object addingService( final ServiceReference reference )
    {
        final Object service = super.addingService( reference );
        if ( service instanceof CommandHandler )
        {
            this.environment.addHandler( (CommandHandler) service );
        }

        return service;
    }

    @Override
    public void removedService( final ServiceReference reference, final Object service )
    {
        super.removedService( reference, service );

        if ( service instanceof CommandHandler )
        {
            this.environment.removeHandler( (CommandHandler) service );
        }
    }
}
