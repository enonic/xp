package com.enonic.xp.lib.auth;

import org.mockito.Mockito;
import org.junit.jupiter.api.Test;

import com.enonic.xp.resource.ResourceProblemException;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalNotFoundException;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.testing.ScriptTestSupport;

import static org.junit.jupiter.api.Assertions.*;

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
        runScript( "/lib/xp/examples/auth/deletePrincipal.js" );
    }

    @Test
    public void testDeleteUser()
    {
        runFunction( "/test/deletePrincipal-test.js", "deleteUser" );
    }

    @Test
    public void testDeleteNonExistingUser()
    {
        final PrincipalKey principalKey = PrincipalKey.from( "user:myIdProvider:XXX" );
        Mockito.doThrow( new PrincipalNotFoundException( principalKey ) ).when( securityService ).deletePrincipal( principalKey );
        runFunction( "/test/deletePrincipal-test.js", "deleteNonExistingUser" );
    }

    @Test
    public void testDeleteSystemUser()
    {
        assertThrows(ResourceProblemException.class, () -> runFunction( "/test/deletePrincipal-test.js", "deleteSystemUser" ));
    }
}
