package com.enonic.xp.lib.auth;

import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.security.EditableUser;
import com.enonic.xp.security.Principal;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.UpdateUserParams;
import com.enonic.xp.security.User;
import com.enonic.xp.security.UserEditor;
import com.enonic.xp.testing.ScriptTestSupport;

public class ModifyUserHandlerTest
    extends ScriptTestSupport
{
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
    public void testExamples()
    {
        Mockito.<Optional<? extends Principal>>when( securityService.getUser( Mockito.any() ) ).thenReturn(
            Optional.of( TestDataFixtures.getTestUser() ) );

        Mockito.when( this.securityService.updateUser( Mockito.isA( UpdateUserParams.class ) ) ).thenAnswer(
            invocationOnMock -> invokeUpdate( (UpdateUserParams) invocationOnMock.getArguments()[0] ) );

        runScript( "/site/lib/xp/examples/auth/modifyUser.js" );
    }

    @Test
    public void testModifyUser()
    {
        Mockito.<Optional<? extends Principal>>when( securityService.getUser( PrincipalKey.from( "user:myUserStore:userId" ) ) ).thenReturn(
            Optional.of( TestDataFixtures.getTestUser() ) );

        Mockito.when( this.securityService.updateUser( Mockito.isA( UpdateUserParams.class ) ) ).thenAnswer(
            invocationOnMock -> invokeUpdate( (UpdateUserParams) invocationOnMock.getArguments()[0] ) );

        runFunction( "/site/test/modifyUser-test.js", "modifyUser" );
    }

    private User invokeUpdate( final UpdateUserParams params )
    {
        final UserEditor editor = params.getEditor();
        Assert.assertNotNull( editor );

        final User user = TestDataFixtures.getTestUser();
        final EditableUser editable = new EditableUser( user );

        editor.edit( editable );
        return editable.build();
    }
}
