package com.enonic.xp.testing.mock;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.bean.BeanContext;

public final class MockBeanContext
    implements BeanContext
{
    private final ResourceKey resourceKey;

    private final Map<Class, Supplier> bindings;

    private final MockServiceRegistry serviceRegistry;

    public MockBeanContext( final ResourceKey resourceKey )
    {
        this( resourceKey, new MockServiceRegistry() );
    }

    public MockBeanContext( final ResourceKey resourceKey, final MockServiceRegistry serviceRegistry )
    {
        this.resourceKey = resourceKey;
        this.bindings = new HashMap<>();
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public ApplicationKey getApplicationKey()
    {
        return this.resourceKey.getApplicationKey();
    }

    @Override
    public ResourceKey getResourceKey()
    {
        return this.resourceKey;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Supplier<T> getBinding( final Class<T> type )
    {
        return this.bindings.get( type );
    }

    @Override
    public <T> Supplier<T> getService( final Class<T> type )
    {
        return this.serviceRegistry.getService( type );
    }

    public <T> void addBinding( final Class<T> type, final T instance )
    {
        this.bindings.put( type, () -> instance );
    }

    public <T> void addService( final Class<T> type, final T instance )
    {
        this.serviceRegistry.register( type, instance );
    }
}
