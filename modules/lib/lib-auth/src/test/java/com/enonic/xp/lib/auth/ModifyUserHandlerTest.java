package com.enonic.xp.lib.auth;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.security.EditableUser;
import com.enonic.xp.security.Principal;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.UpdateUserParams;
import com.enonic.xp.security.User;
import com.enonic.xp.security.UserEditor;
import com.enonic.xp.testing.ScriptTestSupport;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ModifyUserHandlerTest
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
    void testExamples()
    {
        Mockito.<Optional<? extends Principal>>when( securityService.getUser( Mockito.any() ) ).thenReturn(
            Optional.of( TestDataFixtures.getTestUser() ) );

        Mockito.when( this.securityService.updateUser( Mockito.isA( UpdateUserParams.class ) ) ).thenAnswer(
            invocationOnMock -> invokeUpdate( (UpdateUserParams) invocationOnMock.getArguments()[0] ) );

        runScript( "/lib/xp/examples/auth/modifyUser.js" );
    }

    @Test
    void testModifyUser()
    {
        Mockito.<Optional<? extends Principal>>when(
            securityService.getUser( PrincipalKey.from( "user:myIdProvider:userId" ) ) ).thenReturn(
            Optional.of( TestDataFixtures.getTestUser() ) );

        Mockito.when( this.securityService.updateUser( Mockito.isA( UpdateUserParams.class ) ) ).thenAnswer(
            invocationOnMock -> invokeUpdate( (UpdateUserParams) invocationOnMock.getArguments()[0] ) );

        runFunction( "/test/modifyUser-test.js", "modifyUser" );
    }

    private User invokeUpdate( final UpdateUserParams params )
    {
        final UserEditor editor = params.getEditor();
        assertNotNull( editor );

        final User user = TestDataFixtures.getTestUser();
        final EditableUser editable = new EditableUser( user );

        editor.edit( editable );
        return editable.build();
    }
}
