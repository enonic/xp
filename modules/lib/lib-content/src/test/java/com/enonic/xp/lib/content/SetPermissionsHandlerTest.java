package com.enonic.xp.lib.content;

import java.util.Optional;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ApplyContentPermissionsResult;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.security.Principal;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.User;

public class SetPermissionsHandlerTest
    extends BaseContentHandlerTest
{

    @Test
    public void testExample()
    {
        final Content content = TestDataFixtures.newExampleContent();
        Mockito.when( this.contentService.getByPath( Mockito.any() ) ).thenReturn( content );

        Mockito.when( this.contentService.applyPermissions( Mockito.any() ) ).thenReturn( ApplyContentPermissionsResult.create().build() );

        SecurityService securityService = Mockito.mock( SecurityService.class );
        addService( SecurityService.class, securityService );

        final Optional<? extends Principal> value = Optional.of( User.ANONYMOUS );
        Mockito.<Optional<? extends Principal>>when( securityService.getPrincipal( Mockito.any( PrincipalKey.class ) ) ).thenReturn(
            value );

        runScript( "/lib/xp/examples/content/setPermissions.js" );
    }

    @Test
    public void testMissingPrincipals()
        throws Exception
    {
        final Content content = TestDataFixtures.newExampleContent();
        Mockito.when( this.contentService.getByPath( Mockito.any() ) ).thenReturn( content );

        Mockito.when( this.contentService.applyPermissions( Mockito.any() ) ).thenReturn( ApplyContentPermissionsResult.create().build() );

        SecurityService securityService = Mockito.mock( SecurityService.class );
        addService( SecurityService.class, securityService );

        Mockito.<Optional<? extends Principal>>when( securityService.getPrincipal( Mockito.any( PrincipalKey.class ) ) ).thenReturn(
            Optional.empty() );

        runFunction( "/test/SetPermissionsHandlerTest.js", "setPermissionsMissingPrincipals" );
    }

    @Test
    public void testContentNotFoundByPath()
        throws Exception
    {
        Mockito.when( this.contentService.getByPath( Mockito.any() ) ).thenReturn( null );

        SecurityService securityService = Mockito.mock( SecurityService.class );
        addService( SecurityService.class, securityService );

        final Optional<? extends Principal> value = Optional.of( User.ANONYMOUS );
        Mockito.<Optional<? extends Principal>>when( securityService.getPrincipal( Mockito.any( PrincipalKey.class ) ) ).thenReturn(
            value );

        runFunction( "/test/SetPermissionsHandlerTest.js", "setPermissionsNotFoundByPath" );
    }

    @Test
    public void testContentNotFoundById()
        throws Exception
    {
        Mockito.when( this.contentService.getByPath( Mockito.any() ) ).thenThrow(
            new ContentNotFoundException( ContentId.from( "ee70f5d0-025c-4fbd-a835-5e50ff7b76ba" ), Branch.from( "draft" ) ) );

        SecurityService securityService = Mockito.mock( SecurityService.class );
        addService( SecurityService.class, securityService );

        final Optional<? extends Principal> value = Optional.of( User.ANONYMOUS );
        Mockito.<Optional<? extends Principal>>when( securityService.getPrincipal( Mockito.any( PrincipalKey.class ) ) ).thenReturn(
            value );

        runFunction( "/test/SetPermissionsHandlerTest.js", "setPermissionsNotFoundById" );
    }

}
