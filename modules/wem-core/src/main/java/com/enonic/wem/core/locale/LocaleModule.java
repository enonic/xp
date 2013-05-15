package com.enonic.wem.core.locale;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public final class LocaleModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind( LocaleService.class ).to( LocaleServiceImpl.class ).in( Scopes.SINGLETON );
    }
}
