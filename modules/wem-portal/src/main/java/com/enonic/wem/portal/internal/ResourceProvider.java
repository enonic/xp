package com.enonic.wem.portal.internal;

public interface ResourceProvider<T>
{
    public Class<T> getType();

    public T newResource();
}
