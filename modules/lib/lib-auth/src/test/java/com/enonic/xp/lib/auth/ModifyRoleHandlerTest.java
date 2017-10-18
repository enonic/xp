package com.enonic.xp.lib.auth;

import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.security.EditableRole;
import com.enonic.xp.security.Principal;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.Role;
import com.enonic.xp.security.RoleEditor;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.UpdateRoleParams;
import com.enonic.xp.testing.ScriptTestSupport;

public class ModifyRoleHandlerTest
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
        Mockito.<Optional<? extends Principal>>when( securityService.getRole( Mockito.any() ) ).thenReturn(
            Optional.of( TestDataFixtures.getTestUser() ) );

        Mockito.when( this.securityService.updateRole( Mockito.isA( UpdateRoleParams.class ) ) ).thenAnswer(
            invocationOnMock -> invokeUpdate( (UpdateRoleParams) invocationOnMock.getArguments()[0] ) );

        runScript( "/site/lib/xp/examples/auth/modifyRole.js" );
    }

    @Test
    public void testModifyRole()
    {
        Mockito.<Optional<? extends Principal>>when(
            securityService.getRole( PrincipalKey.from( "role:aRole" ) ) ).thenReturn(
            Optional.of( TestDataFixtures.getTestUser() ) );

        Mockito.when( this.securityService.updateRole( Mockito.isA( UpdateRoleParams.class ) ) ).thenAnswer(
            invocationOnMock -> invokeUpdate( (UpdateRoleParams) invocationOnMock.getArguments()[0] ) );

        runFunction( "/site/test/modifyRole-test.js", "modifyRole" );
    }

    private Role invokeUpdate( final UpdateRoleParams params )
    {
        final RoleEditor editor = params.getEditor();
        Assert.assertNotNull( editor );

        final Role role = TestDataFixtures.getTestRole();
        final EditableRole editable = new EditableRole( role );

        editor.edit( editable );
        return editable.build();
    }
}
