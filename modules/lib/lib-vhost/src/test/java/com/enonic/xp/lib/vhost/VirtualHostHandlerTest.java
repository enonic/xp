package com.enonic.xp.lib.vhost;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.IdProviderKeys;
import com.enonic.xp.testing.ScriptTestSupport;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostService;

public class VirtualHostHandlerTest
    extends ScriptTestSupport
{

    private VirtualHostService virtualHostService;

    private VirtualHost virtualHost;

    @Override
    protected void initialize()
        throws Exception
    {
        super.initialize();
        virtualHostService = Mockito.mock( VirtualHostService.class );
        addService( VirtualHostService.class, virtualHostService );
    }

    @BeforeEach
    public void setUp()
    {
        Mockito.when( virtualHostService.isEnabled() ).thenReturn( true );

        virtualHost = Mockito.mock( VirtualHost.class );

        Mockito.when( virtualHost.getName() ).thenReturn( "a" );
        Mockito.when( virtualHost.getSource() ).thenReturn( "/a" );
        Mockito.when( virtualHost.getTarget() ).thenReturn( "/other/a" );
        Mockito.when( virtualHost.getHost() ).thenReturn( "localhost" );

        final IdProviderKey defaultIdProviderKey = IdProviderKey.from( "default" );
        Mockito.when( virtualHost.getDefaultIdProviderKey() ).thenReturn( defaultIdProviderKey );
        Mockito.when( virtualHost.getIdProviderKeys() ).thenReturn( IdProviderKeys.from( defaultIdProviderKey ) );
    }

    @Test
    public void testEnabled()
    {
        runFunction( "/com/enonic/xp/lib/vhost/vhost-test.js", "testEnabled" );
    }

    @Test
    public void testGetVirtualHosts()
    {
        Mockito.when( virtualHostService.getVirtualHosts() ).thenReturn( List.of( virtualHost ) );

        runFunction( "/com/enonic/xp/lib/vhost/vhost-test.js", "testGetVirtualHosts" );
    }

}
