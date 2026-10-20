package com.enonic.xp.web.vhost;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VirtualHostTest
{
    private static VirtualHost serving( final String... audience )
    {
        final VirtualHost virtualHost = Mockito.mock( VirtualHost.class );
        Mockito.when( virtualHost.getAudience() ).thenReturn( Set.of( audience ) );
        Mockito.when( virtualHost.acceptsAudience( Mockito.any(), Mockito.any() ) ).thenCallRealMethod();
        return virtualHost;
    }

    @Test
    void a_host_declaring_no_audience_stands_for_xp_itself()
    {
        assertTrue( serving().acceptsAudience( Set.of( "xp" ), "xp" ) );
        assertTrue( serving().acceptsAudience( Set.of( "xp", "api" ), "xp" ) );
        assertFalse( serving().acceptsAudience( Set.of( "api" ), "xp" ) );
        assertFalse( serving().acceptsAudience( Set.of( "xp" ), "prod-eu" ) );
        assertFalse( serving().acceptsAudience( Set.of(), "xp" ) );
    }

    @Test
    void a_host_declaring_audiences_matches_its_list_alone()
    {
        assertTrue( serving( "api", "mgmt" ).acceptsAudience( Set.of( "mgmt" ), "xp" ) );
        assertTrue( serving( "api", "mgmt" ).acceptsAudience( Set.of( "other", "api" ), "xp" ) );
        assertFalse( serving( "api", "mgmt" ).acceptsAudience( Set.of( "other" ), "xp" ) );
        assertFalse( serving( "api", "mgmt" ).acceptsAudience( Set.of( "xp" ), "xp" ) );
        assertFalse( serving( "api", "mgmt" ).acceptsAudience( Set.of(), "xp" ) );
    }
}
