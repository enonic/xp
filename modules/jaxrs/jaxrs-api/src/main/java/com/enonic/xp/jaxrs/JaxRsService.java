package com.enonic.xp.jaxrs;

import com.enonic.xp.web.dispatch.ServletMapping;

public interface JaxRsService
    extends Iterable<JaxRsComponent>
{
    void init();

    ServletMapping init2();

    void add( JaxRsComponent component );

    void remove( JaxRsComponent component );

    void destroy();
}
