package com.enonic.xp.web.jaxrs;

public interface JaxRsResourceFactory<T>
{
    Class<T> getType();

    T newResource();
}
