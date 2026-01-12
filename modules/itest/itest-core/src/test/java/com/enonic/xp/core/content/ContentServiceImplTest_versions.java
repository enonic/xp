package com.enonic.xp.core.content;

import org.junit.jupiter.api.Test;

import com.enonic.xp.archive.ArchiveContentParams;
import com.enonic.xp.archive.RestoreContentParams;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentAccessException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentVersion;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.FindContentVersionsParams;
import com.enonic.xp.content.FindContentVersionsResult;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.security.auth.AuthenticationInfo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ContentServiceImplTest_versions
    extends AbstractContentServiceTest
{

    @Test
    void get_versions()
    {
        final Content content = this.contentService.create( CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "content" ).
            parent( ContentPath.ROOT ).
            name( "myContent" ).
            type( ContentTypeName.folder() ).
            build() );

        final UpdateContentParams updateContentParams = new UpdateContentParams();
        updateContentParams.contentId( content.getId() ).
            editor( edit -> {
                edit.displayName = "new display name";
            } );

        this.contentService.update( updateContentParams );

        final FindContentVersionsResult result = this.contentService.getVersions( FindContentVersionsParams.create().
            contentId( content.getId() ).
            build() );

        assertEquals( 2, result.getContentVersions().getSize() );
        assertEquals( 2, result.getTotalHits() );
    }

    @Test
    void get_archived_versions()
    {
        final Content content = this.contentService.create( CreateContentParams.create()
                                                                .contentData( new PropertyTree() )
                                                                .displayName( "content" )
                                                                .parent( ContentPath.ROOT )
                                                                .name( "myContent" )
                                                                .type( ContentTypeName.folder() )
                                                                .build() );

        this.contentService.archive( ArchiveContentParams.create().contentId( content.getId() ).build() );

        this.contentService.restore( RestoreContentParams.create().contentId( content.getId() ).build() );

        final FindContentVersionsResult result =
            this.contentService.getVersions( FindContentVersionsParams.create().contentId( content.getId() ).build() );

        assertEquals( 3, result.getContentVersions().getSize() );
        assertEquals( 3, result.getTotalHits() );

        assertThat( result.getContentVersions() ).elements( 0, 1 )
            .extracting( ContentVersion::getActions )
            .map( cs -> cs.stream().map( ContentVersion.Action::operation ).findFirst().orElseThrow() )
            .containsExactly( "content.restore", "content.archive" );
    }

    @Test
    void get_versions_requires_modify_permission()
    {
        final Content content = this.contentService.create( CreateContentParams.create()
                                                                .contentData( new PropertyTree() )
                                                                .displayName( "content" )
                                                                .parent( ContentPath.ROOT )
                                                                .name( "myContent" )
                                                                .type( ContentTypeName.folder() )
                                                                .permissions( AccessControlList.create()
                                                                                  .add( AccessControlEntry.create()
                                                                                            .principal( RoleKeys.ADMIN )
                                                                                            .allowAll()
                                                                                            .build() )
                                                                                  .add( AccessControlEntry.create()
                                                                                            .principal( PrincipalKey.ofAnonymous() )
                                                                                            .allow( Permission.READ )
                                                                                            .build() )
                                                                                  .build() )
                                                                .build() );

        // Create a user without MODIFY permission
        final User limitedUser = User.create()
            .key( PrincipalKey.ofUser( IdProviderKey.system(), "limitedUser" ) )
            .displayName( "Limited User" )
            .login( "limitedUser" )
            .build();

        final AuthenticationInfo limitedAuthInfo = AuthenticationInfo.create()
            .user( limitedUser )
            .principals( limitedUser.getKey(), PrincipalKey.ofAnonymous() )
            .build();

        // Verify that getting versions with limited user throws ContentAccessException
        assertThrows( ContentAccessException.class, () -> ContextBuilder.from( ContextAccessor.current() )
            .authInfo( limitedAuthInfo )
            .build()
            .callWith( () -> this.contentService.getVersions( FindContentVersionsParams.create().contentId( content.getId() ).build() ) ) );
    }

    @Test
    void get_versions_with_modify_permission()
    {
        // Create a user with MODIFY permission
        final User modifyUser = User.create()
            .key( PrincipalKey.ofUser( IdProviderKey.system(), "modifyUser" ) )
            .displayName( "Modify User" )
            .login( "modifyUser" )
            .build();

        final Content content = this.contentService.create( CreateContentParams.create()
                                                                .contentData( new PropertyTree() )
                                                                .displayName( "content" )
                                                                .parent( ContentPath.ROOT )
                                                                .name( "myContent2" )
                                                                .type( ContentTypeName.folder() )
                                                                .permissions( AccessControlList.create()
                                                                                  .add( AccessControlEntry.create()
                                                                                            .principal( RoleKeys.ADMIN )
                                                                                            .allowAll()
                                                                                            .build() )
                                                                                  .add( AccessControlEntry.create()
                                                                                            .principal( modifyUser.getKey() )
                                                                                            .allow( Permission.READ, Permission.MODIFY )
                                                                                            .build() )
                                                                                  .build() )
                                                                .build() );

        final UpdateContentParams updateContentParams = new UpdateContentParams();
        updateContentParams.contentId( content.getId() ).editor( edit -> {
            edit.displayName = "new display name";
        } );

        this.contentService.update( updateContentParams );

        final AuthenticationInfo modifyAuthInfo = AuthenticationInfo.create()
            .user( modifyUser )
            .principals( modifyUser.getKey() )
            .build();

        // Verify that getting versions with MODIFY permission succeeds
        final FindContentVersionsResult result = ContextBuilder.from( ContextAccessor.current() )
            .authInfo( modifyAuthInfo )
            .build()
            .callWith( () -> this.contentService.getVersions( FindContentVersionsParams.create().contentId( content.getId() ).build() ) );

        assertEquals( 2, result.getContentVersions().getSize() );
        assertEquals( 2, result.getTotalHits() );
    }
}

