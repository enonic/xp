package com.enonic.xp.lib.auth;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.token.Client;
import com.enonic.xp.security.token.Clients;
import com.enonic.xp.security.token.CreateClientParams;
import com.enonic.xp.security.token.GrantTypes;
import com.enonic.xp.security.token.UpdateClientParams;
import com.enonic.xp.testing.ScriptTestSupport;

import static org.mockito.Mockito.when;

class ClientHandlersTest
    extends ScriptTestSupport
{
    private static final Client CLAUDE = Client.create()
        .idProvider( IdProviderKey.system() )
        .id( "claude" )
        .displayName( "Claude" )
        .uri( "https://claude.ai" )
        .grantTypes( List.of( GrantTypes.AUTHORIZATION_CODE, GrantTypes.REFRESH_TOKEN ) )
        .redirectUris( List.of( "https://claude.ai/api/mcp/auth_callback" ) )
        .scope( List.of( "content:read" ) )
        .resource( List.of( "https://example.com/api/mcp" ) )
        .grantTtl( Duration.ofDays( 30 ) )
        .build();

    private SecurityService securityService;

    @Override
    public void initialize()
        throws Exception
    {
        super.initialize();
        this.securityService = Mockito.mock( SecurityService.class );
        addService( SecurityService.class, this.securityService );
        when( securityService.createClient( Mockito.any() ) ).thenAnswer( i -> i.<CreateClientParams>getArgument( 0 ).getClient() );
        when( securityService.updateClient( Mockito.any() ) ).thenAnswer( i -> {
            final UpdateClientParams params = i.getArgument( 0 );
            return "claude".equals( params.getId() ) ? Optional.of( params.applyTo( CLAUDE ) ) : Optional.empty();
        } );
    }

    @Test
    void testExamples()
    {
        runScript( "/lib/xp/examples/auth/createClient.js" );
        runScript( "/lib/xp/examples/auth/modifyClient.js" );
        when( securityService.getClients( IdProviderKey.system() ) ).thenReturn( Clients.from( CLAUDE ) );
        runScript( "/lib/xp/examples/auth/getClients.js" );
        when( securityService.deleteClient( IdProviderKey.system(), "claude" ) ).thenReturn( true );
        runScript( "/lib/xp/examples/auth/deleteClient.js" );
    }

    @Test
    void create()
    {
        runFunction( "/test/clients-test.js", "createClientDefaults" );
        runFunction( "/test/clients-test.js", "createClientDisabled" );
        runFunction( "/test/clients-test.js", "requiredParameters" );
    }

    @Test
    void modify()
    {
        runFunction( "/test/clients-test.js", "modifyClient" );
        runFunction( "/test/clients-test.js", "modifyClientUnknown" );
    }

    @Test
    void list_and_delete()
    {
        when( securityService.getClients( IdProviderKey.from( "enonic" ) ) ).thenReturn( Clients.empty() );
        runFunction( "/test/clients-test.js", "getClientsEmpty" );
        runFunction( "/test/clients-test.js", "deleteClientUnknown" );
    }
}
