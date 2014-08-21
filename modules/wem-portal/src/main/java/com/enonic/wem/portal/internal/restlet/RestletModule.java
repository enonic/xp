package com.enonic.wem.portal.internal.restlet;

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
