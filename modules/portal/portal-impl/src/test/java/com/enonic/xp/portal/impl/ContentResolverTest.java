package com.enonic.xp.portal.impl;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfigs;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContentResolverTest
{
    @Mock
    ContentService contentService;

    PortalRequest request;

    @BeforeEach
    void setUp()
    {
        this.request = new PortalRequest();
        this.request.setRepositoryId( RepositoryId.from( "com.enonic.cms.myproject" ) );
    }

    @Test
    void resolve_in_edit_mode()
    {
        final Content content = newContent();
        final Site site = newSite();

        request.setBaseUri( "/site" );
        request.setMode( RenderMode.EDIT );
        request.setContentPath( ContentPath.from( "/c8da0c10-0002-4b68-b407-87412f3e45c8" ) );

        when( this.contentService.getById( ContentId.from( "c8da0c10-0002-4b68-b407-87412f3e45c8" ) ) ).thenReturn( content );
        when( this.contentService.getNearestSite( ContentId.from( "c8da0c10-0002-4b68-b407-87412f3e45c8" ) ) ).thenReturn( site );

        final ContentResolverResult result = new ContentResolver( contentService, mock( ProjectService.class ) ).resolve( request );

        assertSame( content, result.getContent() );
        assertSame( site, result.getNearestSite() );
        assertEquals( "/landing-page", result.getSiteRelativePath() );
    }

    @Test
    void resolve_self_in_edit_mode()
    {
        final Site site = newSite();

        request.setBaseUri( "/site" );
        request.setMode( RenderMode.EDIT );
        request.setContentPath( ContentPath.from( "/site0c10-0002-4b68-b407-87412f3e45c9" ) );

        when( this.contentService.getById( site.getId() ) ).thenReturn( site );

        final ContentResolverResult result = new ContentResolver( contentService, mock( ProjectService.class ) ).resolve( request );

        assertSame( site, result.getContent() );
        assertSame( site, result.getNearestSite() );
        assertEquals( "/", result.getSiteRelativePath() );
    }

    @Test
    void resolve_not_found_edit_mode()
    {

        request.setBaseUri( "/admin/site/edit" );
        request.setMode( RenderMode.EDIT );
        final ContentPath contentPath = ContentPath.from( "/c8da0c10-0002-4b68-b407-87412f3e45c8" );
        request.setContentPath( contentPath );

        when( this.contentService.getById( ContentId.from( "c8da0c10-0002-4b68-b407-87412f3e45c8" ) ) ).thenThrow(
            ContentNotFoundException.class );

        when( this.contentService.getByPath( contentPath ) ).thenThrow( ContentNotFoundException.class );

        final ContentResolverResult result = new ContentResolver( contentService, mock( ProjectService.class ) ).resolve( request );

        assertNull( result.getContent() );
        assertNull( result.getNearestSite() );
        assertEquals( "/c8da0c10-0002-4b68-b407-87412f3e45c8", result.getSiteRelativePath() );
    }

    @Test
    void resolve_found_by_path_edit_mode()
    {
        final Content content = newContent();
        final Site site = newSite();

        request.setBaseUri( "/admin/site/edit" );
        request.setMode( RenderMode.EDIT );
        request.setContentPath( ContentPath.from( "/some-page" ) );

        when( this.contentService.getById( ContentId.from( "some-page" ) ) ).thenThrow( ContentNotFoundException.class );
        when( this.contentService.getByPath( ContentPath.from( "/some-page" ) ) ).thenReturn( content );

        when( this.contentService.getNearestSite( ContentId.from( "c8da0c10-0002-4b68-b407-87412f3e45c8" ) ) ).thenReturn( site );

        final ContentResolverResult result = new ContentResolver( contentService, mock( ProjectService.class ) ).resolve( request );

        assertSame( content, result.getContent() );
        assertSame( site, result.getNearestSite() );
        assertEquals( "/landing-page", result.getSiteRelativePath() );
    }

    @Test
    void resolve_root_edit_mode()
    {
        request.setBaseUri( "/admin/site/edit" );
        request.setMode( RenderMode.EDIT );
        request.setContentPath( ContentPath.from( "/root0c10-0002-4b68-b407-87412f3e45c8" ) );

        final Content rootContent = newRootContent();

        when( this.contentService.getById( ContentId.from( "root0c10-0002-4b68-b407-87412f3e45c8" ) ) ).thenReturn( rootContent );

        final ContentResolverResult result = new ContentResolver( contentService, mock( ProjectService.class ) ).resolve( request );

        assertNull( result.getContent() );
        assertNull( result.getNearestSite() );
        assertEquals( "/", result.getSiteRelativePath() );
        final WebException exception = assertThrows( WebException.class, result::getContentOrElseThrow );
        assertEquals( HttpStatus.NOT_FOUND, exception.getStatus() );
    }

    @Test
    void resolve_root_path_in_edit_mode()
    {

        request.setBaseUri( "/admin/site/edit" );
        request.setMode( RenderMode.EDIT );
        request.setContentPath( ContentPath.ROOT );

        final ContentResolverResult result = new ContentResolver( contentService, mock( ProjectService.class ) ).resolve( request );

        assertNull( result.getContent() );
        assertNull( result.getNearestSite() );
        assertEquals( "/", result.getSiteRelativePath() );
        final WebException exception = assertThrows( WebException.class, result::getContentOrElseThrow );
        assertEquals( HttpStatus.NOT_FOUND, exception.getStatus() );
    }

    @Test
    void resolve_no_site_edit_mode()
    {
        final Content content = newContent();

        request.setBaseUri( "/admin/site/edit" );
        request.setMode( RenderMode.EDIT );
        request.setContentPath( ContentPath.from( "/site0c10-0002-4b68-b407-87412f3e45c9" ) );

        when( this.contentService.getById( ContentId.from( "site0c10-0002-4b68-b407-87412f3e45c9" ) ) ).thenReturn( content );
        when( this.contentService.getNearestSite( any() ) ).thenReturn( null );

        final ContentResolverResult result = new ContentResolver( contentService, mock( ProjectService.class ) ).resolve( request );

        assertSame( content, result.getContent() );
        assertNull( result.getNearestSite() );
        assertEquals( "/mysite/landing-page", result.getSiteRelativePath() );
    }

    @Test
    void resolve_in_live_mode()
    {
        final Content content = newContent();
        final Site site = newSite();

        request.setBaseUri( "/site" );
        request.setContentPath( ContentPath.from( "/mysite/landing-page" ) );

        when( this.contentService.getByPath( ContentPath.from( "/mysite/landing-page" ) ) ).thenReturn( content );
        when( this.contentService.findNearestSiteByPath( ContentPath.from( "/mysite/landing-page" ) ) ).thenReturn( site );

        final ContentResolverResult result = new ContentResolver( contentService, mock( ProjectService.class ) ).resolve( request );

        assertSame( content, result.getContent() );
        assertSame( site, result.getNearestSite() );
        assertEquals( "/landing-page", result.getSiteRelativePath() );
    }

    @Test
    void resolve_path_in_edit_mode()
    {
        // CS should stop sending paths in EDIT mode.
        // But for now we support it.
        final Content content = newContent();
        final Site site = newSite();

        request.setBaseUri( "/admin/site/edit" );
        request.setMode( RenderMode.EDIT );

        request.setContentPath( ContentPath.from( "/mysite/landing-page" ) );

        when( this.contentService.getByPath( ContentPath.from( "/mysite/landing-page" ) ) ).thenReturn( content );
        when( this.contentService.getNearestSite( content.getId() ) ).thenReturn( site );

        final ContentResolverResult result = new ContentResolver( contentService, mock( ProjectService.class ) ).resolve( request );

        assertSame( content, result.getContent() );
        assertSame( site, result.getNearestSite() );
        assertEquals( "/landing-page", result.getSiteRelativePath() );
    }

    @Test
    void resolve_self_in_live_mode()
    {
        final Site site = newSite();

        request.setBaseUri( "/site" );
        request.setContentPath( ContentPath.from( "/mysite" ) );

        when( this.contentService.getByPath( ContentPath.from( "/mysite" ) ) ).thenReturn( site );

        final ContentResolverResult result = new ContentResolver( contentService, mock( ProjectService.class ) ).resolve( request );

        assertSame( site, result.getContent() );
        assertSame( site, result.getNearestSite() );
        assertEquals( "/", result.getSiteRelativePath() );
    }

    @Test
    void resolve_non_existing_in_live_mode()
    {
        final Site site = newSite();

        request.setBaseUri( "/site" );
        final ContentPath contentPath = ContentPath.from( "/mysite/landing-page/non-existing" );
        request.setContentPath( contentPath );

        when( this.contentService.getByPath( contentPath ) ).thenThrow( ContentNotFoundException.class );
        when( this.contentService.findNearestSiteByPath( contentPath ) ).thenReturn( site );

        final ContentResolverResult result = new ContentResolver( contentService, mock( ProjectService.class ) ).resolve( request );

        assertNull( result.getContent() );
        assertSame( site, result.getNearestSite() );
        assertEquals( "/landing-page/non-existing", result.getSiteRelativePath() );
        final WebException e = assertThrows( WebException.class, result::getContentOrElseThrow );
        assertEquals( HttpStatus.NOT_FOUND, e.getStatus() );
    }

    @Test
    void resolve_existing_but_needs_authentication_in_live_mode()
    {
        final Site site = newSite();
        final Content content = newPrivilegedContent();

        request.setBaseUri( "/site" );
        final ContentPath contentPath = ContentPath.from( "/mysite/privileged-page" );
        request.setContentPath( contentPath );

        when( this.contentService.getByPath( contentPath ) ).thenReturn( content );
        when( this.contentService.findNearestSiteByPath( contentPath ) ).thenReturn( site );

        final ContentResolverResult result = new ContentResolver( contentService, mock( ProjectService.class ) ).resolve( request );

        assertNull( result.getContent() );
        assertSame( site, result.getNearestSite() );
        assertEquals( "/privileged-page", result.getSiteRelativePath() );
        final WebException e = assertThrows( WebException.class, result::getContentOrElseThrow );
        assertEquals( HttpStatus.UNAUTHORIZED, e.getStatus() );
    }

    @Test
    void resolve_no_site_in_live_mode()
    {
        final Content content = newContent();

        request.setBaseUri( "/site" );
        request.setContentPath( ContentPath.from( "/mysite/landing-page" ) );

        when( this.contentService.getByPath( ContentPath.from( "/mysite/landing-page" ) ) ).thenReturn( content );
        when( this.contentService.findNearestSiteByPath( ContentPath.from( "/mysite/landing-page" ) ) ).thenReturn( null );

        final ContentResolverResult result = new ContentResolver( contentService, mock( ProjectService.class ) ).resolve( request );

        assertSame( content, result.getContent() );
        assertNull( result.getNearestSite() );
        assertNotNull( result.getSiteRelativePath() );
    }

    @Test
    void resolve_root_in_live_mode()
    {

        request.setBaseUri( "/site" );
        request.setContentPath( ContentPath.ROOT );

        final ContentResolverResult result = new ContentResolver( contentService, mock( ProjectService.class ) ).resolve( request );

        assertNull( result.getContent() );
        assertNull( result.getNearestSite() );
        assertEquals( "/", result.getSiteRelativePath() );
        final WebException exception = assertThrows( WebException.class, result::getContentOrElseThrow );
        assertEquals( HttpStatus.NOT_FOUND, exception.getStatus() );
    }

    private Content newContent()
    {
        final Content.Builder<?> builder = Content.create();
        builder.id( ContentId.from( "c8da0c10-0002-4b68-b407-87412f3e45c8" ) );
        builder.name( "landing-page" );
        builder.displayName( "My Landing Page" );
        builder.parentPath( ContentPath.from( "/mysite" ) );
        builder.type( ContentTypeName.from( ApplicationKey.from( "com.enonic.test.app" ), "landing-page" ) );
        builder.modifier( PrincipalKey.from( "user:system:admin" ) );
        builder.modifiedTime( Instant.ofEpochSecond( 0 ) );
        builder.creator( PrincipalKey.from( "user:system:admin" ) );
        builder.createdTime( Instant.ofEpochSecond( 0 ) );
        builder.data( new PropertyTree() );
        builder.permissions( AccessControlList.create()
                                 .add( AccessControlEntry.create().allow( Permission.READ ).principal( RoleKeys.EVERYONE ).build() )
                                 .build() );
        return builder.build();
    }

    private Content newPrivilegedContent()
    {
        final Content.Builder<?> builder = Content.create();
        builder.id( ContentId.from( "c8da0c10-0002-4b68-b407-87412f3e45c9" ) );
        builder.name( "privileged-page" );
        builder.displayName( "My Privileged Page" );
        builder.parentPath( ContentPath.from( "/mysite" ) );
        builder.type( ContentTypeName.from( ApplicationKey.from( "com.enonic.test.app" ), "landing-page" ) );
        builder.modifier( PrincipalKey.from( "user:system:admin" ) );
        builder.modifiedTime( Instant.ofEpochSecond( 0 ) );
        builder.creator( PrincipalKey.from( "user:system:admin" ) );
        builder.createdTime( Instant.ofEpochSecond( 0 ) );
        builder.data( new PropertyTree() );
        builder.permissions( AccessControlList.create()
                                 .add( AccessControlEntry.create().allow( Permission.READ ).principal( RoleKeys.ADMIN ).build() )
                                 .build() );
        return builder.build();
    }


    private Content newRootContent()
    {
        final Content.Builder<?> builder = Content.create();
        builder.root();
        builder.id( ContentId.from( "root0c10-0002-4b68-b407-87412f3e45c8" ) );
        builder.displayName( "" );
        builder.type( ContentTypeName.folder() );
        builder.modifier( PrincipalKey.from( "user:system:admin" ) );
        builder.modifiedTime( Instant.ofEpochSecond( 0 ) );
        builder.creator( PrincipalKey.from( "user:system:admin" ) );
        builder.createdTime( Instant.ofEpochSecond( 0 ) );
        builder.data( new PropertyTree() );
        builder.permissions( AccessControlList.create()
                                 .add( AccessControlEntry.create().allow( Permission.READ ).principal( RoleKeys.EVERYONE ).build() )
                                 .build() );
        return builder.build();
    }

    private Site newSite()
    {

        final Site.Builder site = Site.create();
        site.id( ContentId.from( "site0c10-0002-4b68-b407-87412f3e45c9" ) );
        site.siteConfigs( SiteConfigs.empty() );
        site.name( "mysite" );
        site.parentPath( ContentPath.ROOT );
        site.permissions( AccessControlList.create()
                              .add( AccessControlEntry.create().allow( Permission.READ ).principal( RoleKeys.EVERYONE ).build() )
                              .build() );
        return site.build();
    }
}
