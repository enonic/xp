package com.enonic.xp.lib.auth;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.security.auth.AccessTokenInfo;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.security.token.Scope;
import com.enonic.xp.security.token.ScopeService;
import com.enonic.xp.security.token.Scopes;
import com.enonic.xp.session.SessionMock;
import com.enonic.xp.testing.ScriptTestSupport;

class HasScopeHandlerTest
    extends ScriptTestSupport
{
    @Override
    public void initialize()
        throws Exception
    {
        super.initialize();
        final Scopes defined = Scopes.from( Scope.create().id( "com.example.orders:write" ).includes( List.of( "com.example.orders:read" ) ).build(),
                                            Scope.create().id( "com.example.orders:read" ).build() );
        addService( ScopeService.class, () -> defined );
        ContextAccessor.current().getLocalScope().setSession( new SessionMock() );
    }

    private void withToken( final String... scope )
    {
        ContextAccessor.current().getLocalScope().getSession().setAttribute( AuthenticationInfo.create()
                                                                                 .user( TestDataFixtures.getTestUser() )
                                                                                 .accessToken( AccessTokenInfo.create()
                                                                                                   .issuer( "xp" )
                                                                                                   .scope( List.of( scope ) )
                                                                                                   .build() )
                                                                                 .build() );
    }

    @Test
    void testExamples()
    {
        withToken( "com.example.orders:write" );
        runScript( "/lib/xp/examples/auth/hasScope.js" );
    }

    @Test
    void a_scope_the_token_carries_or_one_that_includes_it()
    {
        withToken( "com.example.orders:write", "openid" );
        runFunction( "/test/hasScope-test.js", "hasWriteAndRead" );
    }

    @Test
    void a_scope_the_token_lacks()
    {
        withToken( "com.example.orders:read" );
        runFunction( "/test/hasScope-test.js", "hasReadOnly" );
    }

    @Test
    void a_token_without_scopes_has_none()
    {
        withToken();
        runFunction( "/test/hasScope-test.js", "hasNone" );
    }

    @Test
    void a_request_without_a_token_is_not_limited()
    {
        ContextAccessor.current().getLocalScope().getSession().setAttribute(
            AuthenticationInfo.create().user( TestDataFixtures.getTestUser() ).build() );
        runFunction( "/test/hasScope-test.js", "notLimited" );
    }
}
