package com.enonic.wem.guice.listener;

import org.ops4j.peaberry.AttributeFilter;
import org.ops4j.peaberry.ServiceRegistry;
import org.ops4j.peaberry.util.Filters;

import com.google.inject.Binding;
import com.google.inject.Injector;

import com.enonic.wem.guice.util.InjectorUtils;

public final class ListenerManager
{
    private final Injector injector;

    private final ServiceRegistry serviceRegistry;

    public ListenerManager( final Injector injector )
    {
        this.injector = injector;
        this.serviceRegistry = this.injector.getInstance( ServiceRegistry.class );
    }

    public void bindAll()
    {
        bindServiceListeners();
    }

    private void bindServiceListeners()
    {
        InjectorUtils.findBindings( this.injector, ServiceListener.class ).forEach( this::bindServiceListener );
    }

    private void bindServiceListener( final Binding<?> binding )
    {
        final ServiceListener<?> listener = (ServiceListener<?>) binding.getProvider().get();
        final AttributeFilter filter = listener.getFilter() != null ? Filters.ldap( listener.getFilter() ) : null;
        final Class<?> filterClass = listener.getFilterClass();
        this.serviceRegistry.watch( filterClass, filter, new ServiceWatcherAdapter( listener ) );
    }
}
