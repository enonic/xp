package com.enonic.xp.jaxrs;

public interface JaxRsService
    extends Iterable<JaxRsComponent>
{
    void init();

    void add( JaxRsComponent component );

    void remove( JaxRsComponent component );

    void destroy();
}
