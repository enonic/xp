package com.enonic.xp.testing.script;

import java.util.Map;
import java.util.function.Supplier;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.google.common.collect.Maps;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.bean.BeanContext;

public final class MockBeanContext
    implements BeanContext
{
    private final ResourceKey resourceKey;

    private final Map<Class, Supplier> bindings;

    private final BundleContext bundleContext;

    public MockBeanContext( final ResourceKey resourceKey, final BundleContext bundleContext )
    {
        this.resourceKey = resourceKey;
        this.bindings = Maps.newHashMap();
        this.bundleContext = bundleContext;
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
    @SuppressWarnings("unchecked")
    public <T> Supplier<T> getService( final Class<T> type )
    {
        return () -> (T) lookupService( type );
    }

    private Object lookupService( final Class type )
    {
        final ServiceReference ref = this.bundleContext.getServiceReference( type );
        if ( ref == null )
        {
            return null;
        }

        return this.bundleContext.getService( ref );
    }

    public <T> void addBinding( final Class<T> type, final T instance )
    {
        this.bindings.put( type, () -> instance );
    }
}
