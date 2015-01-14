package com.enonic.xp.web.jaxrs2;

public interface JaxRsResourceFactory<T>
{
    public Class<T> getType();

    public T newResource();
}
