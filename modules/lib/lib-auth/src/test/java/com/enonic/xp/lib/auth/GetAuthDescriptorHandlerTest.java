package com.enonic.xp.lib.auth;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.auth.AuthDescriptor;
import com.enonic.xp.auth.AuthDescriptorMode;
import com.enonic.xp.auth.AuthDescriptorService;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.testing.ScriptTestSupport;

public class GetAuthDescriptorHandlerTest
    extends ScriptTestSupport
{
    private SecurityService securityService;

    private AuthDescriptorService authDescriptorService;

    private ApplicationKey applicationKey;

    @Override
    public void initialize()
        throws Exception
    {
        super.initialize();
        this.securityService = Mockito.mock( SecurityService.class );
        addService( SecurityService.class, this.securityService );
        this.authDescriptorService = Mockito.mock( AuthDescriptorService.class );
        addService( AuthDescriptorService.class, this.authDescriptorService );
        this.applicationKey = ApplicationKey.from( "com.enonic.app.test" );
    }

    @Test
    public void testExamples()
    {
        final AuthDescriptor authDescriptor = AuthDescriptor.create().key( applicationKey ).mode( AuthDescriptorMode.LOCAL ).build();
        Mockito.when( securityService.getUserStore( UserStoreKey.from( "myUserStore" ) ) ).thenReturn(
            TestDataFixtures.getTestUserStore() );
        Mockito.when( authDescriptorService.getDescriptor( applicationKey ) ).thenReturn( authDescriptor );
        runScript( "/site/lib/xp/examples/auth/getAuthDescriptor.js" );
    }

    @Test
    public void testGetAuthDescriptorMode()
    {
        final AuthDescriptor authDescriptor = AuthDescriptor.create().key( applicationKey ).mode( AuthDescriptorMode.LOCAL ).build();
        Mockito.when( securityService.getUserStore( UserStoreKey.from( "myUserStore" ) ) ).thenReturn(
            TestDataFixtures.getTestUserStore() );
        Mockito.when( authDescriptorService.getDescriptor( applicationKey ) ).thenReturn( authDescriptor );

        runFunction( "/site/test/getAuthDescriptor-test.js", "getAuthDescriptor" );
    }

    @Test
    public void testNonExistingAuthDescriptorMode()
    {
        Mockito.when( securityService.getUserStore( UserStoreKey.from( "myUserStore" ) ) ).thenReturn(
            TestDataFixtures.getTestUserStore() );
        Mockito.when( authDescriptorService.getDescriptor( applicationKey ) ).thenReturn( null );

        runFunction( "/site/test/getAuthDescriptor-test.js", "getNonExistingAuthDescriptor" );
    }
}
