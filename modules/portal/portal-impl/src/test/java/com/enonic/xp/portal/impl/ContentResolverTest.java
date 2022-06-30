package com.enonic.xp.portal.impl;

import java.time.Instant;

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
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfigs;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContentResolverTest
{
    @Mock
    ContentService contentService;

    @Test
    void resolve_in_edit_mode()
    {
        final Content content = newContent();
        final Site site = newSite();

        final PortalRequest request = new PortalRequest();
        request.setMode( RenderMode.EDIT );
        request.setContentPath( ContentPath.from( "/c8da0c10-0002-4b68-b407-87412f3e45c8" ) );

        when( this.contentService.getById( ContentId.from( "c8da0c10-0002-4b68-b407-87412f3e45c8" ) ) ).thenReturn( content );
        when( this.contentService.getNearestSite( ContentId.from( "c8da0c10-0002-4b68-b407-87412f3e45c8" ) ) ).thenReturn( site );

        final ContentResolverResult result = new ContentResolver( contentService ).resolve( request );

        assertSame( content, result.getContent() );
        assertSame( site, result.getNearestSite() );
        assertEquals( "/landing-page", result.getSiteRelativePath() );
    }

    @Test
    void resolve_self_in_edit_mode()
    {
        final Site site = newSite();

        final PortalRequest request = new PortalRequest();
        request.setMode( RenderMode.EDIT );
        request.setContentPath( ContentPath.from( "/c8da0c10-0002-4b68-b407-87412f3e45c9" ) );

        when( this.contentService.getById( ContentId.from( "c8da0c10-0002-4b68-b407-87412f3e45c9" ) ) ).thenReturn( site );
        when( this.contentService.getNearestSite( ContentId.from( "c8da0c10-0002-4b68-b407-87412f3e45c9" ) ) ).thenReturn( site );

        final ContentResolverResult result = new ContentResolver( contentService ).resolve( request );

        assertSame( site, result.getContent() );
        assertSame( site, result.getNearestSite() );
        assertEquals( "/", result.getSiteRelativePath() );
    }

    @Test
    void resolve_not_found_edit_mode()
    {
        final PortalRequest request = new PortalRequest();
        request.setMode( RenderMode.EDIT );
        final ContentPath contentPath = ContentPath.from( "/c8da0c10-0002-4b68-b407-87412f3e45c8" );
        request.setContentPath( contentPath );

        when( this.contentService.getById( ContentId.from( "c8da0c10-0002-4b68-b407-87412f3e45c8" ) ) ).thenThrow(
            new ContentNotFoundException( ContentId.from( "c8da0c10-0002-4b68-b407-87412f3e45c8" ), null ) );

        when( this.contentService.getByPath( contentPath ) ).thenThrow(
            new ContentNotFoundException( ContentId.from( "c8da0c10-0002-4b68-b407-87412f3e45c8" ), null ) );

        final ContentResolverResult result = new ContentResolver( contentService ).resolve( request );

        assertNull( result.getContent() );
        assertNull( result.getNearestSite() );
        assertNull( result.getSiteRelativePath() );
    }

    @Test
    void resolve_found_by_path_edit_mode()
    {
        final Content content = newContent();
        final Site site = newSite();

        final PortalRequest request = new PortalRequest();
        request.setMode( RenderMode.EDIT );
        request.setContentPath( ContentPath.from( "/c8da0c10-0002-4b68-b407-87412f3e45c8" ) );

        when( this.contentService.getById( ContentId.from( "c8da0c10-0002-4b68-b407-87412f3e45c8" ) ) ).thenThrow(
            new ContentNotFoundException( ContentId.from( "c8da0c10-0002-4b68-b407-87412f3e45c8" ), null ) );
        when( this.contentService.getByPath( ContentPath.from( "/c8da0c10-0002-4b68-b407-87412f3e45c8" ) ) ).thenReturn( content );

        when( this.contentService.getNearestSite( ContentId.from( "c8da0c10-0002-4b68-b407-87412f3e45c8" ) ) ).thenReturn( site );

        final ContentResolverResult result = new ContentResolver( contentService ).resolve( request );

        assertSame( content, result.getContent() );
        assertSame( site, result.getNearestSite() );
        assertEquals( "/landing-page", result.getSiteRelativePath() );
    }

    @Test
    void resolve_root_edit_mode()
    {
        final PortalRequest request = new PortalRequest();
        request.setMode( RenderMode.EDIT );
        request.setContentPath( ContentPath.from( "/c8da0c10-0002-4b68-b407-87412f3e45c8" ) );

        final Content rootContent = mock( Content.class );
        when( rootContent.getPath() ).thenReturn( ContentPath.ROOT );

        when( this.contentService.getById( ContentId.from( "c8da0c10-0002-4b68-b407-87412f3e45c8" ) ) ).thenReturn( rootContent );

        final ContentResolverResult result = new ContentResolver( contentService ).resolve( request );

        assertNull( result.getContent() );
        assertNull( result.getNearestSite() );
        assertNull( result.getSiteRelativePath() );
    }

    @Test
    void resolve_no_site_edit_mode()
    {
        final Content content = newContent();

        final PortalRequest request = new PortalRequest();
        request.setMode( RenderMode.EDIT );
        request.setContentPath( ContentPath.from( "/c8da0c10-0002-4b68-b407-87412f3e45c8" ) );

        when( this.contentService.getById( ContentId.from( "c8da0c10-0002-4b68-b407-87412f3e45c8" ) ) ).thenReturn( content );
        when( this.contentService.getNearestSite( ContentId.from( "c8da0c10-0002-4b68-b407-87412f3e45c8" ) ) ).thenReturn( null );

        final ContentResolverResult result = new ContentResolver( contentService ).resolve( request );

        assertSame( content, result.getContent() );
        assertNull( result.getNearestSite() );
        assertNull( result.getSiteRelativePath() );
    }

    @Test
    void resolve_in_live_mode()
    {
        final Content content = newContent();
        final Site site = newSite();

        final PortalRequest request = new PortalRequest();
        request.setContentPath( ContentPath.from( "/mysite/landing-page" ) );

        when( this.contentService.getByPath( ContentPath.from( "/mysite/landing-page" ) ) ).thenReturn( content );
        when( this.contentService.findNearestSiteByPath( ContentPath.from( "/mysite/landing-page" ) ) ).thenReturn( site );

        final ContentResolverResult result = new ContentResolver( contentService ).resolve( request );

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

        final PortalRequest request = new PortalRequest();
        request.setMode( RenderMode.EDIT );

        request.setContentPath( ContentPath.from( "/mysite/landing-page" ) );

        when( this.contentService.getByPath( ContentPath.from( "/mysite/landing-page" ) ) ).thenReturn( content );
        when( this.contentService.getNearestSite( content.getId() ) ).thenReturn( site );

        final ContentResolverResult result = new ContentResolver( contentService ).resolve( request );

        assertSame( content, result.getContent() );
        assertSame( site, result.getNearestSite() );
        assertEquals( "/landing-page", result.getSiteRelativePath() );
    }

    @Test
    void resolve_self_in_live_mode()
    {
        final Site site = newSite();

        final PortalRequest request = new PortalRequest();
        request.setContentPath( ContentPath.from( "/mysite" ) );

        when( this.contentService.getByPath( ContentPath.from( "/mysite" ) ) ).thenReturn( site );
        when( this.contentService.findNearestSiteByPath( ContentPath.from( "/mysite" ) ) ).thenReturn( site );

        final ContentResolverResult result = new ContentResolver( contentService ).resolve( request );

        assertSame( site, result.getContent() );
        assertSame( site, result.getNearestSite() );
        assertEquals( "/", result.getSiteRelativePath() );
    }

    @Test
    void resolve_non_existing_in_live_mode()
    {
        final Site site = newSite();

        final PortalRequest request = new PortalRequest();
        final ContentPath contentPath = ContentPath.from( "/mysite/landing-page/non-existing" );
        request.setContentPath( contentPath );

        when( this.contentService.getByPath( contentPath ) ).thenThrow( new ContentNotFoundException( contentPath, null ) );
        when( this.contentService.contentExists( contentPath ) ).thenReturn( false );
        when( this.contentService.findNearestSiteByPath( contentPath ) ).thenReturn( site );

        final ContentResolverResult result = new ContentResolver( contentService ).resolve( request );

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

        final PortalRequest request = new PortalRequest();
        final ContentPath contentPath = ContentPath.from( "/mysite/landing-page/non-existing" );
        request.setContentPath( contentPath );

        when( this.contentService.getByPath( contentPath ) ).thenThrow( new ContentNotFoundException( contentPath, null ) );
        when( this.contentService.contentExists( contentPath ) ).thenReturn( true );
        when( this.contentService.findNearestSiteByPath( contentPath ) ).thenReturn( site );

        final ContentResolverResult result = new ContentResolver( contentService ).resolve( request );

        assertNull( result.getContent() );
        assertSame( site, result.getNearestSite() );
        assertEquals( "/landing-page/non-existing", result.getSiteRelativePath() );
        final WebException e = assertThrows( WebException.class, result::getContentOrElseThrow );
        assertEquals( HttpStatus.UNAUTHORIZED, e.getStatus() );
    }

    @Test
    void resolve_no_site_in_live_mode()
    {
        final Content content = newContent();

        final PortalRequest request = new PortalRequest();
        request.setContentPath( ContentPath.from( "/mysite/landing-page" ) );

        when( this.contentService.getByPath( ContentPath.from( "/mysite/landing-page" ) ) ).thenReturn( content );
        when( this.contentService.findNearestSiteByPath( ContentPath.from( "/mysite/landing-page" ) ) ).thenReturn( null );

        final ContentResolverResult result = new ContentResolver( contentService ).resolve( request );

        assertSame( content, result.getContent() );
        assertNull( result.getNearestSite() );
        assertNull( result.getSiteRelativePath() );
    }

    private Content newContent()
    {
        final Content.Builder builder = Content.create();
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
        return builder.build();
    }

    private Site newSite()
    {

        final Site.Builder site = Site.create();
        site.id( ContentId.from( "c8da0c10-0002-4b68-b407-87412f3e45c9" ) );
        site.siteConfigs( SiteConfigs.empty() );
        site.name( "mysite" );
        site.parentPath( ContentPath.ROOT );
        return site.build();
    }
}