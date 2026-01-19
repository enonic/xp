package com.enonic.xp.web;

import org.junit.jupiter.api.Test;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WebExceptionTest
{
    @Test
    void forbidden_403_for_authenticated()
    {
        // for already authenticated users forbidden must not allow ID Provider to re-authenticate
        final AuthenticationInfo authenticationInfo = AuthenticationInfo.create().user( User.anonymous() ).build();
        final Context authenticatedContext = ContextBuilder.from( ContextAccessor.current() ).authInfo( authenticationInfo ).build();

        final WebException webException = authenticatedContext.callWith( () -> WebException.forbidden( "some message" ) );
        assertEquals( HttpStatus.FORBIDDEN, webException.getStatus() );
    }

    @Test
    void forbidden_401_for_not_authenticated()
    {
        // forbidden becomes 401 unauthorized, so ID Provider can authenticate.
        // It should happen only if user is not authenticated.
        final WebException webException = WebException.forbidden( "some message" );
        assertEquals( HttpStatus.UNAUTHORIZED, webException.getStatus() );
    }
}