package com.enonic.xp.lib.auth;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.token.CreateScopeParams;
import com.enonic.xp.security.token.Scope;
import com.enonic.xp.security.token.Scopes;
import com.enonic.xp.security.token.UpdateScopeParams;
import com.enonic.xp.testing.ScriptTestSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ScopeHandlersTest
    extends ScriptTestSupport
{
    private static final Scope WRITE = Scope.create()
        .idProvider( IdProviderKey.system() )
        .id( "content:write" )
        .title( "Edit content" )
        .description( "Create, change and publish content in all projects" )
        .roles( PrincipalKeys.from( "role:cms.cm.app", "role:cms.expert" ) )
        .build();

    private SecurityService securityService;

    @Override
    public void initialize()
        throws Exception
    {
        super.initialize();
        this.securityService = Mockito.mock( SecurityService.class );
        addService( SecurityService.class, this.securityService );
    }

    @Test
    void testExamples()
    {
        when( securityService.createScope( Mockito.any() ) ).thenReturn( WRITE );
        runScript( "/lib/xp/examples/auth/createScope.js" );

        when( securityService.updateScope( Mockito.any() ) ).thenAnswer( i -> Optional.of( i.<UpdateScopeParams>getArgument( 0 ).applyTo( WRITE ) ) );
        runScript( "/lib/xp/examples/auth/modifyScope.js" );

        when( securityService.getScopes( IdProviderKey.system() ) ).thenReturn( Scopes.from( WRITE ) );
        runScript( "/lib/xp/examples/auth/getScopes.js" );

        when( securityService.deleteScope( IdProviderKey.system(), "content:write" ) ).thenReturn( true );
        runScript( "/lib/xp/examples/auth/deleteScope.js" );
    }

    @Test
    void createScope_passes_every_value()
    {
        when( securityService.createScope( Mockito.any() ) ).thenAnswer( i -> i.<CreateScopeParams>getArgument( 0 ).getScope() );

        runFunction( "/test/scopes-test.js", "createScope" );

        final ArgumentCaptor<CreateScopeParams> params = ArgumentCaptor.forClass( CreateScopeParams.class );
        verify( securityService ).createScope( params.capture() );
        final Scope created = params.getValue().getScope();
        assertEquals( IdProviderKey.from( "enonic" ), created.getIdProvider() );
        assertEquals( "profile", created.getId() );
        assertEquals( "Profile", created.getTitle() );
        assertEquals( PrincipalKeys.empty(), created.getRoles() );
        assertTrue( created.isRequired() );
    }

    @Test
    void createScope_defaults()
    {
        when( securityService.createScope( Mockito.any() ) ).thenAnswer( i -> i.<CreateScopeParams>getArgument( 0 ).getScope() );

        runFunction( "/test/scopes-test.js", "createScopeDefaults" );
    }

    @Test
    void modifyScope_sends_only_what_was_given_and_clears_on_null()
    {
        when( securityService.updateScope( Mockito.any() ) ).thenAnswer( i -> Optional.of( i.<UpdateScopeParams>getArgument( 0 ).applyTo( WRITE ) ) );

        runFunction( "/test/scopes-test.js", "modifyScope" );
    }

    @Test
    void modifyScope_unknown_is_null()
    {
        when( securityService.updateScope( Mockito.any() ) ).thenReturn( Optional.empty() );

        runFunction( "/test/scopes-test.js", "modifyScopeUnknown" );
    }

    @Test
    void getScopes_and_deleteScope()
    {
        when( securityService.getScopes( IdProviderKey.from( "enonic" ) ) ).thenReturn( Scopes.empty() );
        when( securityService.deleteScope( IdProviderKey.from( "enonic" ), "profile" ) ).thenReturn( false );

        runFunction( "/test/scopes-test.js", "getScopesEmpty" );
        runFunction( "/test/scopes-test.js", "deleteScopeUnknown" );
        assertFalse( securityService.deleteScope( IdProviderKey.from( "enonic" ), "profile" ) );
    }

    @Test
    void required_parameters()
    {
        runFunction( "/test/scopes-test.js", "requiredParameters" );
    }
}
