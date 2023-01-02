package com.enonic.xp.portal.impl.url;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.portal.impl.ContentFixtures;
import com.enonic.xp.portal.url.PageUrlParams;
import com.enonic.xp.portal.url.UrlTypeConstants;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class PortalUrlServiceImpl_pageUrlTest
    extends AbstractPortalUrlServiceImplTest
{
    @Test
    public void createUrl_toMe()
    {
        final PageUrlParams params = new PageUrlParams().
            portalRequest( this.portalRequest ).
            param( "a", 3 );

        final String url = this.service.pageUrl( params );
        assertEquals( "/site/default/draft/context/path?a=3", url );
    }

    @Test
    public void createUrl_withRelativePath()
    {
        final PageUrlParams params = new PageUrlParams().
            portalRequest( this.portalRequest ).
            path( "a/b" ).
            param( "a", 3 );

        final String url = this.service.pageUrl( params );
        assertEquals( "/site/default/draft/context/path/a/b?a=3", url );
    }

    @Test
    public void createUrl_withAbsolutePath()
    {
        final PageUrlParams params = new PageUrlParams().
            portalRequest( this.portalRequest ).
            path( "/a/b" );

        final String url = this.service.pageUrl( params );
        assertEquals( "/site/default/draft/a/b", url );
    }

    @Test
    public void createUrl_withId()
    {
        final Content content = ContentFixtures.newContent();
        Mockito.when( this.contentService.getById( content.getId() ) ).thenReturn( content );

        final PageUrlParams params = new PageUrlParams().
            portalRequest( this.portalRequest ).
            id( "123456" );

        final String url = this.service.pageUrl( params );
        assertEquals( "/site/default/draft/a/b/mycontent", url );
    }

    @Test
    public void createUrl_withIdAndPath()
    {
        final Content content = ContentFixtures.newContent();
        Mockito.when( this.contentService.getById( content.getId() ) ).thenReturn( content );

        final PageUrlParams params = new PageUrlParams().
            portalRequest( this.portalRequest ).
            id( "123456" ).
            path( "/a/b" );

        final String url = this.service.pageUrl( params );
        assertEquals( "/site/default/draft/a/b/mycontent", url );
    }

    @Test
    public void createUrl_withId_notFound()
    {
        final Content content = ContentFixtures.newContent();
        Mockito.when( this.contentService.getById( content.getId() ) ).thenThrow(
            new ContentNotFoundException( content.getId(), Branch.from( "draft" ) ) );

        final PageUrlParams params = new PageUrlParams().
            portalRequest( this.portalRequest ).
            id( "123456" );

        final String url = this.service.pageUrl( params );
        assertEquals(
            "/site/default/draft/context/path/_/error/404?message=Content+with+id+%5B123456%5D+was+not+found+in+branch+%5Bdraft%5D",
            url );
    }

    @Test
    public void createUrl_absolute()
    {
        final PageUrlParams params = new PageUrlParams().
            type( UrlTypeConstants.ABSOLUTE ).
            portalRequest( this.portalRequest ).
            param( "a", 3 );

        when( req.getServerName() ).thenReturn( "localhost" );
        when( req.getScheme() ).thenReturn( "http" );
        when( req.getServerPort() ).thenReturn( 80 );

        final String url = this.service.pageUrl( params );
        assertEquals( "http://localhost/site/default/draft/context/path?a=3", url );
    }

    @Test
    public void createUrl_absoluteWithCustomBaseUrl()
    {
        PageUrlParams params = new PageUrlParams().
            type( UrlTypeConstants.ABSOLUTE ).
            portalRequest( this.portalRequest ).
            param( "a", 3 );

        String url = ContextBuilder.create()
            .attribute( "_customServerUrl", "http://www.company.com" )
            .authInfo( AuthenticationInfo.create().user( User.ANONYMOUS ).build() )
            .build()
            .callWith( () -> this.service.pageUrl( params ) );

        assertEquals( "http://www.company.com/site/default/draft/context/path?a=3", url );
    }
}
