package com.enonic.wem.jaxrs;

public abstract class JaxRsResourceFactory<T>
{
    private final Class<T> type;

    public JaxRsResourceFactory( final Class<T> type )
    {
        this.type = type;
    }

    public final Class<T> getType()
    {
        return this.type;
    }

    public abstract T newInstance();
}
