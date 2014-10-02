package com.enonic.wem.servlet.jaxrs;

public interface ResourceProvider<T>
{
    public Class<T> getType();

    public T newResource();
}
