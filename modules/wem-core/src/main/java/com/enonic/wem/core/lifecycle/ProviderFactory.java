package com.enonic.wem.core.lifecycle;

import javax.inject.Provider;

import org.springframework.beans.factory.FactoryBean;

/**
 * Temporary class to break the barrier between a provider and a spring factory bean.
 */
public abstract class ProviderFactory<T>
    implements FactoryBean<T>, Provider<T>
{
    private final Class<? extends T> type;

    public ProviderFactory( final Class<? extends T> type )
    {
        this.type = type;
    }

    @Override
    public final T getObject()
    {
        return get();
    }

    @Override
    public final Class<?> getObjectType()
    {
        return this.type;
    }

    @Override
    public final boolean isSingleton()
    {
        return true;
    }
}
