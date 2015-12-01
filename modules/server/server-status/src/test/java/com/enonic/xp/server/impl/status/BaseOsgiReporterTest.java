package com.enonic.xp.server.impl.status;

import org.mockito.Mockito;
import org.osgi.framework.Bundle;

public abstract class BaseOsgiReporterTest
    extends BaseReporterTest
{
    protected final Bundle newBundle( final long id, final String name )
    {
        final Bundle bundle = Mockito.mock( Bundle.class );
        Mockito.when( bundle.getBundleId() ).thenReturn( id );
        Mockito.when( bundle.getSymbolicName() ).thenReturn( name );
        Mockito.when( bundle.getState() ).thenReturn( Bundle.ACTIVE );
        return bundle;
    }
}
