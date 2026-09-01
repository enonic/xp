package com.enonic.xp.impl.server.rest.api;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostIdProvider;
import com.enonic.xp.web.vhost.VirtualHostService;

import static com.enonic.xp.impl.server.rest.api.ManagementApiTestSupport.request;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class VirtualHostApiHandlerTest
{
    private VirtualHostService virtualHostService;

    private VirtualHostApiHandler handler;

    @BeforeEach
    void setUp()
    {
        virtualHostService = mock( VirtualHostService.class );
        handler = new VirtualHostApiHandler( virtualHostService );
    }

    @Test
    void disabled()
    {
        when( virtualHostService.isEnabled() ).thenReturn( false );

        final WebResponse response = handler.handle( request( HttpMethod.GET, "/server:vhost" ) );

        assertEquals( HttpStatus.OK, response.getStatus() );
        assertEquals( "{\"enabled\":false,\"vhosts\":[]}", response.getBody() );
    }

    @Test
    void listHidesContextAndAllow()
    {
        final IdProviderKey idProviderKey = IdProviderKey.from( "myidprovider" );
        final VirtualHost vhost = mock( VirtualHost.class );
        when( vhost.getName() ).thenReturn( "stats" );
        when( vhost.getHost() ).thenReturn( "stats.enonic.com" );
        when( vhost.getSource() ).thenReturn( "/" );
        when( vhost.getTarget() ).thenReturn( "/" );
        when( vhost.getConnector() ).thenReturn( "status" );
        when( vhost.getOrder() ).thenReturn( 10 );
        when( vhost.getDefaultIdProviderKey() ).thenReturn( idProviderKey );
        when( vhost.getIdProviders() ).thenReturn(
            Map.of( idProviderKey, VirtualHostIdProvider.create().flows( Set.of( "autologin", "login" ) ).build() ) );
        when( vhost.getContext() ).thenReturn( Map.of( "api.server:snapshot.verbs", "list", "secret", "hunter2" ) );
        when( vhost.getAllowedPrincipals() ).thenReturn( PrincipalKeys.from( RoleKeys.ADMIN ) );

        when( virtualHostService.isEnabled() ).thenReturn( true );
        when( virtualHostService.getVirtualHosts() ).thenReturn( List.of( vhost ) );

        final WebResponse response = handler.handle( request( HttpMethod.GET, "/server:vhost" ) );

        assertEquals( HttpStatus.OK, response.getStatus() );
        final String body = String.valueOf( response.getBody() );
        assertEquals( "{\"enabled\":true,\"vhosts\":[{\"name\":\"stats\",\"host\":\"stats.enonic.com\",\"source\":\"/\",\"target\":\"/\"," +
                          "\"endpoint\":\"statistics\",\"order\":10,\"defaultIdProviderKey\":\"myidprovider\"," +
                          "\"idProviders\":[{\"key\":\"myidprovider\",\"flows\":[\"autologin\",\"login\"]}]}]}", body );
        assertFalse( body.contains( "hunter2" ) );
        assertFalse( body.contains( "verbs" ) );
        assertFalse( body.contains( "system.admin" ) );
    }
}
