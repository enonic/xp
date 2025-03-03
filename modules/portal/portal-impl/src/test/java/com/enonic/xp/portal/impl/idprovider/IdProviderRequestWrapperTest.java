package com.enonic.xp.portal.impl.idprovider;

import java.security.Principal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.servlet.http.HttpServletRequest;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.session.Session;
import com.enonic.xp.session.SessionMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class IdProviderRequestWrapperTest
{
    @Mock
    HttpServletRequest request;

    @Test
    void getUserPrincipal()
    {
        final User user = User.create().key( PrincipalKey.ofUser( IdProviderKey.createDefault(), "userId" ) ).login( "usr" ).build();
        final AuthenticationInfo authenticationInfo = AuthenticationInfo.create().user( user ).build();
        final Context context = ContextBuilder.create().build();
        final Session session = new SessionMock();
        context.getLocalScope().setSession( session );
        session.setAttribute( authenticationInfo );

        final Principal principal = context.callWith( () -> new IdProviderRequestWrapper( request ).getUserPrincipal() );
        assertEquals( principal, user );
        verifyNoInteractions( request );
    }

    @Test
    void isUserInRole()
    {
        final User user = User.create().key( PrincipalKey.ofUser( IdProviderKey.createDefault(), "userId" ) ).login( "usr" ).build();
        final AuthenticationInfo authenticationInfo = AuthenticationInfo.create().user( user ).principals( RoleKeys.ADMIN ).build();

        final Context context = ContextBuilder.create().build();
        final Session session = new SessionMock();
        context.getLocalScope().setSession( session );
        session.setAttribute( authenticationInfo );
        final Boolean isAdmin = context.callWith( () -> new IdProviderRequestWrapper( request ).isUserInRole( RoleKeys.ADMIN.getId() ) );
        assertTrue( isAdmin );
        verifyNoInteractions( request );
    }
}
