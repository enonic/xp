package com.enonic.xp.lib.auth;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.security.EditableGroup;
import com.enonic.xp.security.Group;
import com.enonic.xp.security.GroupEditor;
import com.enonic.xp.security.Principal;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.UpdateGroupParams;
import com.enonic.xp.testing.ScriptTestSupport;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ModifyGroupHandlerTest
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
        Mockito.<Optional<? extends Principal>>when( securityService.getGroup( Mockito.any() ) ).thenReturn(
            Optional.of( TestDataFixtures.getTestUser() ) );

        Mockito.when( this.securityService.updateGroup( Mockito.isA( UpdateGroupParams.class ) ) ).thenAnswer(
            invocationOnMock -> invokeUpdate( (UpdateGroupParams) invocationOnMock.getArguments()[0] ) );

        runScript( "/lib/xp/examples/auth/modifyGroup.js" );
    }

    @Test
    void testModifyGroup()
    {
        Mockito.<Optional<? extends Principal>>when(
            securityService.getGroup( PrincipalKey.from( "group:myGroupStore:groupId" ) ) ).thenReturn(
            Optional.of( TestDataFixtures.getTestUser() ) );

        Mockito.when( this.securityService.updateGroup( Mockito.isA( UpdateGroupParams.class ) ) ).thenAnswer(
            invocationOnMock -> invokeUpdate( (UpdateGroupParams) invocationOnMock.getArguments()[0] ) );

        runFunction( "/test/modifyGroup-test.js", "modifyGroup" );
    }

    private Group invokeUpdate( final UpdateGroupParams params )
    {
        final GroupEditor editor = params.getEditor();
        assertNotNull( editor );

        final Group group = TestDataFixtures.getTestGroup();
        final EditableGroup editable = new EditableGroup( group );

        editor.edit( editable );
        return editable.build();
    }
}
