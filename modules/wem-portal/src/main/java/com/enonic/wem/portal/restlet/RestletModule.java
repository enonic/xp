package com.enonic.wem.portal.restlet;

import com.google.inject.AbstractModule;

public final class RestletModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind( FinderFactory.class ).to( FinderFactoryImpl.class );
    }
}
