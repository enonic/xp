package com.enonic.xp.core.impl.security.token;

import java.time.Clock;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.enonic.xp.node.NodeService;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * Configuration handling; the behaviour against the node store is covered by the integration
 * test of the same name.
 */
class GrantServiceImplTest
{
    private final NodeService nodeService = mock( NodeService.class );

    private final GrantServiceImpl service = new GrantServiceImpl( nodeService, new InMemorySharedMapService(), Clock.systemUTC() );

    @Test
    void activate_with_osgi_service_properties()
    {
        service.activate( Map.of( "service.pid", "com.enonic.xp.security", "service.bundleid", 42L, "token.grant.maxPerUser", " 1 " ) );
        verifyNoInteractions( nodeService );
    }

    @Test
    void invalid_config_is_rejected()
    {
        assertThatThrownBy( () -> service.activate( Map.of( "token.grant.ttl", "PT0S" ) ) ).isInstanceOf( IllegalArgumentException.class );
        assertThatThrownBy( () -> service.activate( Map.of( "token.grant.maxPerUser", "0" ) ) ).isInstanceOf( IllegalArgumentException.class );
        assertThatThrownBy( () -> service.activate( Map.of( "token.grant.rotateAfter", "-PT1S" ) ) ).isInstanceOf( IllegalArgumentException.class );
        assertThatThrownBy( () -> service.activate( Map.of( "token.grant.reuseGrace", "soon" ) ) ).isInstanceOf( java.time.format.DateTimeParseException.class );
        assertThatThrownBy( () -> service.activate( Map.of( "token.grant.maxPerClient", "x" ) ) ).isInstanceOf( NumberFormatException.class );
    }
}
