package com.enonic.xp.lib.auth;

import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.security.EditableUser;
import com.enonic.xp.security.Principal;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.UpdateUserParams;
import com.enonic.xp.security.User;
import com.enonic.xp.security.UserEditor;
import com.enonic.xp.session.SessionKey;
import com.enonic.xp.session.SimpleSession;
import com.enonic.xp.testing.script.ScriptTestSupport;

public class ModifyUserHandlerTest
    extends ScriptTestSupport
{

    private SimpleSession session;

    private SecurityService securityService;

    @Before
    public void setup()
    {
        this.securityService = Mockito.mock( SecurityService.class );
        addService( SecurityService.class, this.securityService );

        this.session = new SimpleSession( SessionKey.generate() );
        ContextAccessor.current().getLocalScope().setSession( session );

        this.session.setAttribute( HandlerTestHelper.createAuthenticationInfo() );
    }

    @Test
    public void testModifyUser()
        throws Exception
    {
        Mockito.<Optional<? extends Principal>>when( securityService.getUser( PrincipalKey.from( "user:myUserStore:userId" ) ) ).thenReturn(
            Optional.of( HandlerTestHelper.getTestUser() ) );

        Mockito.when( this.securityService.updateUser( Mockito.isA( UpdateUserParams.class ) ) ).thenAnswer(
            invocationOnMock -> invokeUpdate( (UpdateUserParams) invocationOnMock.getArguments()[0] ) );

        runTestFunction( "/test/modifyUser-test.js", "modifyUser" );
    }

    private User invokeUpdate( final UpdateUserParams params )
    {

        final UserEditor editor = params.getEditor();
        Assert.assertNotNull( editor );

        final User user = HandlerTestHelper.getTestUser();
        final EditableUser editable = new EditableUser( user );

        editor.edit( editable );
        return editable.build();
    }
}
