package com.enonic.wem.servlet.jaxrs;

public interface ResourceProvider<T>
    extends JaxRsComponent
{
    public Class<T> getType();

    public T newResource();
}
