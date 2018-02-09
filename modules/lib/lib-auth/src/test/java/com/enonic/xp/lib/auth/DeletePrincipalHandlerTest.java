package com.enonic.xp.lib.auth;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.resource.ResourceProblemException;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalNotFoundException;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.testing.ScriptTestSupport;

public class DeletePrincipalHandlerTest
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
        runScript( "/site/lib/xp/examples/auth/deletePrincipal.js" );
    }

    @Test
    public void testDeleteUser()
    {
        runFunction( "/site/test/deletePrincipal-test.js", "deleteUser" );
    }

    @Test
    public void testDeleteNonExistingUser()
    {
        final PrincipalKey principalKey = PrincipalKey.from( "user:myUserStore:XXX" );
        Mockito.doThrow( new PrincipalNotFoundException( principalKey ) ).when( securityService ).deletePrincipal( principalKey );
        runFunction( "/site/test/deletePrincipal-test.js", "deleteNonExistingUser" );
    }

    @Test(expected = ResourceProblemException.class)
    public void testDeleteSystemUser()
    {
        runFunction( "/site/test/deletePrincipal-test.js", "deleteSystemUser" );
    }
}
