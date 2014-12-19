package com.enonic.xp.web.jaxrs;

public interface ResourceProvider<T>
    extends JaxRsComponent
{
    public Class<T> getType();

    public T newResource();
}
