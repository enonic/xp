package com.enonic.xp.server.impl.status;

import org.mockito.Mockito;
import org.osgi.framework.Bundle;

import com.google.common.net.MediaType;

import com.enonic.xp.status.BaseReporterTest;
import com.enonic.xp.status.StatusReporter;

public abstract class BaseOsgiReporterTest<T extends StatusReporter>
    extends BaseReporterTest<T>
{
    public BaseOsgiReporterTest( final String name )
    {
        super( name, MediaType.JSON_UTF_8 );
    }

    final Bundle newBundle( final long id, final String name )
    {
        final Bundle bundle = Mockito.mock( Bundle.class );
        Mockito.when( bundle.getBundleId() ).thenReturn( id );
        Mockito.when( bundle.getSymbolicName() ).thenReturn( name );
        Mockito.when( bundle.getState() ).thenReturn( Bundle.ACTIVE );
        return bundle;
    }
}
