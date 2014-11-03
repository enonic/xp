package com.enonic.wem.portal.internal.base;

public interface ResourceFactory<T>
{
    public Class<T> getType();

    public T newResource();
}
