package com.enonic.xp.core.impl.app;


import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationListener;

@Component(service = ApplicationListenerHub.class)
public final class ApplicationListenerHub
{
    private final List<ApplicationListener> listeners = new CopyOnWriteArrayList<>();

    private final Map<ApplicationKey, Application> activeApplications = new LinkedHashMap<>();

    private final Object lock = new Object();

    public void activated( final Application app )
    {
        synchronized ( lock )
        {
            activeApplications.put( app.getKey(), app );
            notifyActivated( app );
        }
    }

    public void deactivated( final Application app )
    {
        synchronized ( lock )
        {
            activeApplications.remove( app.getKey() );
            notifyDeactivated( app );
        }
    }

    private void notifyActivated( final Application app )
    {
        for ( final ApplicationListener listener : this.listeners )
        {
            listener.activated( app );
        }
    }

    private void notifyDeactivated( final Application app )
    {
        for ( final ApplicationListener listener : this.listeners )
        {
            listener.deactivated( app );
        }
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addListener( final ApplicationListener listener )
    {
        synchronized ( lock )
        {
            this.listeners.add( listener );

            for ( final Application app : this.activeApplications.values() )
            {
                listener.activated( app );
            }
        }
    }

    public void removeListener( final ApplicationListener listener )
    {
        this.listeners.remove( listener );
    }
}
