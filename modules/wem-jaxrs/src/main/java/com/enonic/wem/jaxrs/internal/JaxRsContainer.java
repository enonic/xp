package com.enonic.wem.jaxrs.internal;

import com.enonic.wem.jaxrs.JaxRsResourceFactory;

public interface JaxRsContainer
{
    public void restart();

    public void registerFactory( JaxRsResourceFactory factory );

    public void registerProvider( Object instance );

    public void registerResource( Object instance );
}
