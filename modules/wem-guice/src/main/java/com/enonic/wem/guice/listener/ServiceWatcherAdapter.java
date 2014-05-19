package com.enonic.wem.guice.listener;

import java.util.Map;

import org.ops4j.peaberry.Import;
import org.ops4j.peaberry.util.AbstractWatcher;

final class ServiceWatcherAdapter
    extends AbstractWatcher
{
    private final ServiceListener listener;

    public ServiceWatcherAdapter( final ServiceListener listener )
    {
        this.listener = listener;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Object adding( final Import service )
    {
        final Object instance = service.get();
        this.listener.serviceAdded( instance, service.attributes() );
        return instance;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void modified( final Object instance, final Map attributes )
    {
        this.listener.serviceModified( instance, attributes );
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void removed( final Object instance )
    {
        this.listener.serviceRemoved( instance );
    }
}
