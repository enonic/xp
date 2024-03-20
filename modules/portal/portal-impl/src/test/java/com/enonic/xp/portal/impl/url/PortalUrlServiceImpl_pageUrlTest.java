package com.enonic.xp.portal.impl.url;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.portal.impl.ContentFixtures;
import com.enonic.xp.portal.url.PageUrlParams;
import com.enonic.xp.portal.url.UrlTypeConstants;
import com.enonic.xp.repository.RepositoryId;

import static org.assertj.core.api.Assertions.assertThat;
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
        assertEquals( "/site/myproject/draft/context/path?a=3", url );
    }

    @Test
    public void createUrl_withRelativePath()
    {
        final PageUrlParams params = new PageUrlParams().
            portalRequest( this.portalRequest ).
            path( "a/b" ).
            param( "a", 3 );

        final String url = this.service.pageUrl( params );
        assertEquals( "/site/myproject/draft/context/path/a/b?a=3", url );
    }

    @Test
    public void createUrl_withAbsolutePath()
    {
        final PageUrlParams params = new PageUrlParams().
            portalRequest( this.portalRequest ).
            path( "/a/b" );

        final String url = this.service.pageUrl( params );
        assertEquals( "/site/myproject/draft/a/b", url );
    }

    @Test
    public void createUrl_withId()
    {
        final Content content = ContentFixtures.newContent();
        when( this.contentService.getById( content.getId() ) ).thenReturn( content );

        final PageUrlParams params = new PageUrlParams().
            portalRequest( this.portalRequest ).
            id( "123456" );

        final String url = this.service.pageUrl( params );
        assertEquals( "/site/myproject/draft/a/b/mycontent", url );
    }

    @Test
    public void createUrl_withIdAndPath()
    {
        final Content content = ContentFixtures.newContent();
        when( this.contentService.getById( content.getId() ) ).thenReturn( content );

        final PageUrlParams params = new PageUrlParams().
            portalRequest( this.portalRequest ).
            id( "123456" ).
            path( "/a/b" );

        final String url = this.service.pageUrl( params );
        assertEquals( "/site/myproject/draft/a/b/mycontent", url );
    }

    @Test
    public void createUrl_withId_notFound()
    {
        final Content content = ContentFixtures.newContent();
        when( this.contentService.getById( content.getId() ) )
            .thenThrow( ContentNotFoundException.create()
                            .contentId( content.getId() )
                            .repositoryId( RepositoryId.from( "com.enonic.cms.myproject" ) )
                            .branch( ContentConstants.BRANCH_DRAFT )
                            .build() );

        final PageUrlParams params = new PageUrlParams().
            portalRequest( this.portalRequest ).
            id( "123456" );

        final String url = this.service.pageUrl( params );
        assertThat( url ).startsWith( "/site/myproject/draft/context/path/_/error/404?message=Not+Found." );
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
        assertEquals( "http://localhost/site/myproject/draft/context/path?a=3", url );
    }

    @Test
    public void createUrlSlashApi()
    {
        this.portalRequest.setBaseUri( "" );
        this.portalRequest.setRawPath( "/api/com.enonic.app.appname" );

        final PageUrlParams params =
            new PageUrlParams().portalRequest( this.portalRequest ).path( "/a/b" ).param( "a", 3 );

        final String url = this.service.pageUrl( params );
        assertEquals( "/myproject/draft/a/b?a=3", url );
    }
}
