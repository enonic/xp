package com.enonic.xp.lib.auth;

import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.security.EditableGroup;
import com.enonic.xp.security.Group;
import com.enonic.xp.security.GroupEditor;
import com.enonic.xp.security.Principal;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.UpdateGroupParams;
import com.enonic.xp.testing.script.OldScriptTestSupport;

public class ModifyGroupHandlerTest
    extends OldScriptTestSupport
{

    private SecurityService securityService;

    @Before
    public void setup()
    {
        this.securityService = Mockito.mock( SecurityService.class );
        addService( SecurityService.class, this.securityService );
    }

    @Test
    public void testModifyGroup()
        throws Exception
    {
        Mockito.<Optional<? extends Principal>>when(
            securityService.getGroup( PrincipalKey.from( "group:myGroupStore:groupId" ) ) ).thenReturn(
            Optional.of( TestDataFixtures.getTestUser() ) );

        Mockito.when( this.securityService.updateGroup( Mockito.isA( UpdateGroupParams.class ) ) ).thenAnswer(
            invocationOnMock -> invokeUpdate( (UpdateGroupParams) invocationOnMock.getArguments()[0] ) );

        runTestFunction( "/test/modifyGroup-test.js", "modifyGroup" );
    }

    private Group invokeUpdate( final UpdateGroupParams params )
    {

        final GroupEditor editor = params.getEditor();
        Assert.assertNotNull( editor );

        final Group group = TestDataFixtures.getTestGroup();
        final EditableGroup editable = new EditableGroup( group );

        editor.edit( editable );
        return editable.build();
    }
}
