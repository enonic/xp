package com.enonic.xp.lib.content;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.enonic.xp.content.ApplyContentPermissionsParams;
import com.enonic.xp.content.ApplyContentPermissionsResult;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.security.Principal;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.User;
import com.enonic.xp.security.acl.Permission;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ApplyPermissionsHandlerTest
    extends BaseContentHandlerTest
{

    @Test
    public void testExample()
    {
        final Content content = TestDataFixtures.newExampleContent();
        when( this.contentService.getByPath( Mockito.any() ) ).thenReturn( content );

        when( this.contentService.applyPermissions( Mockito.any() ) ).thenReturn( ApplyContentPermissionsResult.create().build() );

        SecurityService securityService = Mockito.mock( SecurityService.class );
        addService( SecurityService.class, securityService );

        final Optional<? extends Principal> value = Optional.of( User.ANONYMOUS );
        Mockito.<Optional<? extends Principal>>when( securityService.getPrincipal( Mockito.any( PrincipalKey.class ) ) )
            .thenReturn( value );

        runScript( "/lib/xp/examples/content/applyPermissions.js" );
    }

    @Test
    public void testMissingPrincipals()
        throws Exception
    {
        final Content content = TestDataFixtures.newExampleContent();
        when( this.contentService.getByPath( Mockito.any() ) ).thenReturn( content );

        when( this.contentService.applyPermissions( Mockito.any() ) ).thenReturn( ApplyContentPermissionsResult.create().build() );

        SecurityService securityService = Mockito.mock( SecurityService.class );
        addService( SecurityService.class, securityService );

        Mockito.<Optional<? extends Principal>>when( securityService.getPrincipal( Mockito.any( PrincipalKey.class ) ) )
            .thenReturn( Optional.empty() );

        runFunction( "/test/ApplyPermissionsHandlerTest.js", "applyPermissionsMissingPrincipals" );
    }

    @Test
    public void testContentNotFoundByPath()
        throws Exception
    {
        when( this.contentService.getByPath( Mockito.any() ) ).thenThrow( ContentNotFoundException.class );

        SecurityService securityService = Mockito.mock( SecurityService.class );
        addService( SecurityService.class, securityService );

        final Optional<? extends Principal> value = Optional.of( User.ANONYMOUS );
        Mockito.<Optional<? extends Principal>>when( securityService.getPrincipal( Mockito.any( PrincipalKey.class ) ) )
            .thenReturn( value );

        runFunction( "/test/ApplyPermissionsHandlerTest.js", "applyPermissionsNotFoundByPath" );
    }

    @Test
    public void testContentNotFoundById()
        throws Exception
    {
        when( this.contentService.getByPath( Mockito.any() ) ).thenThrow( ContentNotFoundException.class );

        SecurityService securityService = Mockito.mock( SecurityService.class );
        addService( SecurityService.class, securityService );

        final Optional<? extends Principal> value = Optional.of( User.ANONYMOUS );
        Mockito.<Optional<? extends Principal>>when( securityService.getPrincipal( Mockito.any( PrincipalKey.class ) ) )
            .thenReturn( value );

        runFunction( "/test/ApplyPermissionsHandlerTest.js", "applyPermissionsNotFoundById" );
    }

    @Test
    public void testPermissionParamsNonCompatible()
        throws Exception
    {
        final Content content = TestDataFixtures.newExampleContent();
        when( this.contentService.getByPath( Mockito.any() ) ).thenReturn( content );

        SecurityService securityService = Mockito.mock( SecurityService.class );
        addService( SecurityService.class, securityService );

        final Optional<? extends Principal> value = Optional.of( User.ANONYMOUS );
        Mockito.<Optional<? extends Principal>>when( securityService.getPrincipal( Mockito.any( PrincipalKey.class ) ) )
            .thenReturn( value );

        runFunction( "/test/ApplyPermissionsHandlerTest.js", "applyPermissionsNonCompatible" );
    }

    @Test
    public void testPermissionsAddAndRemove()
        throws Exception
    {
        final ArgumentCaptor<ApplyContentPermissionsParams> paramsCaptor = ArgumentCaptor.forClass( ApplyContentPermissionsParams.class );

        final Content content = TestDataFixtures.newExampleContent();
        when( this.contentService.getByPath( Mockito.any() ) ).thenReturn( content );

        SecurityService securityService = Mockito.mock( SecurityService.class );
        addService( SecurityService.class, securityService );

        final Optional<? extends Principal> value = Optional.of( User.ANONYMOUS );
        Mockito.<Optional<? extends Principal>>when( securityService.getPrincipal( Mockito.any( PrincipalKey.class ) ) )
            .thenReturn( value );

        when( contentService.applyPermissions( Mockito.any() ) ).thenReturn(
            ApplyContentPermissionsResult.create().addBranchResult( content.getId(), ContentConstants.BRANCH_MASTER, content ).build() );

        runFunction( "/test/ApplyPermissionsHandlerTest.js", "applyPermissionsAddRemove" );

        verify( contentService ).applyPermissions( paramsCaptor.capture() );

        assertEquals( 2, paramsCaptor.getValue().getAddPermissions().getMap().size() );
        assertTrue( paramsCaptor.getValue().getAddPermissions().isAllowedFor( PrincipalKey.ofAnonymous(), Permission.READ ) );
        assertTrue( paramsCaptor.getValue().getAddPermissions().isAllowedFor( RoleKeys.EVERYONE, Permission.READ ) );

        assertEquals( 1, paramsCaptor.getValue().getRemovePermissions().getMap().size() );
        assertTrue( paramsCaptor.getValue()
                        .getRemovePermissions()
                        .isAllowedFor( PrincipalKey.ofAnonymous(), Permission.DELETE, Permission.MODIFY ) );

    }


}
