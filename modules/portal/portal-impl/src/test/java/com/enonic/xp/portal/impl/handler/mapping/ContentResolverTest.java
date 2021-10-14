package com.enonic.xp.portal.impl.handler.mapping;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.branch.Branch;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
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

        assertSame( content, result.content );
        assertSame( site, result.nearestSite );
        assertEquals( "/landing-page", result.siteRelativePath );
    }

    @Test
    void resolve_self_in_edit_mode()
    {
        final Site site = newSite();

        final PortalRequest request = new PortalRequest();
        request.setMode( RenderMode.EDIT );
        request.setContentPath( ContentPath.from( "/c8da0c10-0002-4b68-b407-87412f3e45c8" ) );

        when( this.contentService.getById( ContentId.from( "c8da0c10-0002-4b68-b407-87412f3e45c8" ) ) ).thenReturn( site );
        when( this.contentService.getNearestSite( ContentId.from( "c8da0c10-0002-4b68-b407-87412f3e45c8" ) ) ).thenReturn( site );

        final ContentResolverResult result = new ContentResolver( contentService ).resolve( request );

        assertSame( site, result.content );
        assertSame( site, result.nearestSite );
        assertEquals( "/", result.siteRelativePath );
    }

    @Test
    void resolve_no_site_no_result_in_edit_mode()
    {
        final Content content = newContent();

        final PortalRequest request = new PortalRequest();
        request.setMode( RenderMode.EDIT );
        request.setContentPath( ContentPath.from( "/c8da0c10-0002-4b68-b407-87412f3e45c8" ) );

        when( this.contentService.getById( ContentId.from( "c8da0c10-0002-4b68-b407-87412f3e45c8" ) ) ).thenReturn( content );
        when( this.contentService.getNearestSite( ContentId.from( "c8da0c10-0002-4b68-b407-87412f3e45c8" ) ) ).thenReturn( null );

        final ContentResolverResult result = new ContentResolver( contentService ).resolve( request );
        assertNull( result );
    }

    @Test
    void resolve_in_live_mode()
    {
        final Content content = newContent();
        final Site site = newSite();

        final PortalRequest request = new PortalRequest();
        request.setContentPath( ContentPath.from( "/mysite/landing-page" ) );

        when( this.contentService.getByPath( ContentPath.from( "/mysite/landing-page" ) ) ).thenReturn( content );
        when( this.contentService.findNearestSiteByPath( ContentPath.from( "/mysite/landing-page" ) )).thenReturn( site );

        final ContentResolverResult result = new ContentResolver( contentService ).resolve( request );

        assertSame( content, result.content );
        assertSame( site, result.nearestSite );
        assertEquals( "/landing-page", result.siteRelativePath );
    }

    @Test
    void resolve_self_in_live_mode()
    {
        final Site site = newSite();

        final PortalRequest request = new PortalRequest();
        request.setContentPath( ContentPath.from( "/mysite" ) );

        when( this.contentService.getByPath( ContentPath.from( "/mysite" ) ) ).thenReturn( site );
        when( this.contentService.findNearestSiteByPath( ContentPath.from( "/mysite" ) )).thenReturn( site );

        final ContentResolverResult result = new ContentResolver( contentService ).resolve( request );

        assertSame( site, result.content );
        assertSame( site, result.nearestSite );
        assertEquals( "/", result.siteRelativePath );
    }

    @Test
    void resolve_non_existing_in_live_mode()
    {
        final Site site = newSite();

        final PortalRequest request = new PortalRequest();
        final ContentPath contentPath = ContentPath.from( "/mysite/landing-page/non-existing" );
        request.setContentPath( contentPath );

        when( this.contentService.getByPath( contentPath ) ).thenThrow( new ContentNotFoundException( contentPath, null ) );
        when( this.contentService.findNearestSiteByPath( contentPath )).thenReturn( site );

        final ContentResolverResult result = new ContentResolver( contentService ).resolve( request );

        assertNull( result.content );
        assertSame( site, result.nearestSite );
        assertEquals( "/landing-page/non-existing", result.siteRelativePath );
    }

    @Test
    void resolve_no_site_no_result_in_live_mode()
    {
        final PortalRequest request = new PortalRequest();
        request.setContentPath( ContentPath.from( "/mysite/landing-page" ) );

        when( this.contentService.findNearestSiteByPath( ContentPath.from( "/mysite/landing-page" ) )).thenReturn( null );

        final ContentResolverResult result = new ContentResolver( contentService ).resolve( request );

        assertNull( result );
    }

    private Content newContent()
    {
        final Content.Builder builder = Content.create();
        builder.id( ContentId.from( "123456" ) );
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
        site.id( ContentId.from( "100123" ) );
        site.siteConfigs( SiteConfigs.empty() );
        site.name( "mysite" );
        site.parentPath( ContentPath.ROOT );
        return site.build();
    }
}