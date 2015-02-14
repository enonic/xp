package com.enonic.xp.web.jaxrs;

public interface JaxRsResourceFactory<T>
{
    public Class<T> getType();

    public T newResource();
}
