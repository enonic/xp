package com.enonic.wem.core.country;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public final class CountryModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind( CountryService.class ).to( CountryServiceImpl.class ).in( Scopes.SINGLETON );
    }
}
