package com.enonic.wem.core.time;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public final class TimeModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind( TimeZoneService.class ).to( TimeZoneServiceImpl.class ).in( Scopes.SINGLETON );
    }
}
