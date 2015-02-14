package com.enonic.xp.portal.impl.url;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.branch.Branch;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.xp.portal.impl.ContentFixtures;
import com.enonic.xp.portal.url.PageUrlParams;

import static org.junit.Assert.*;

public class PortalUrlServiceImpl_pageUrlTest
    extends AbstractPortalUrlServiceImplTest
{
    @Test
    public void createUrl_toMe()
    {
        final PageUrlParams params = new PageUrlParams().
            context( this.context ).
            param( "a", 3 );

        final String url = this.service.pageUrl( params );
        assertEquals( "/portal/draft/context/path?a=3", url );
    }

    @Test
    public void createUrl_withRelativePath()
    {
        final PageUrlParams params = new PageUrlParams().
            context( this.context ).
            path( "a/b" ).
            param( "a", 3 );

        final String url = this.service.pageUrl( params );
        assertEquals( "/portal/draft/context/path/a/b?a=3", url );
    }

    @Test
    public void createUrl_withAbsolutePath()
    {
        final PageUrlParams params = new PageUrlParams().
            context( this.context ).
            path( "/a/b" );

        final String url = this.service.pageUrl( params );
        assertEquals( "/portal/draft/a/b", url );
    }

    @Test
    public void createUrl_withId()
    {
        final Content content = ContentFixtures.newContent();
        Mockito.when( this.contentService.getById( content.getId() ) ).thenReturn( content );

        final PageUrlParams params = new PageUrlParams().
            context( this.context ).
            id( "123456" );

        final String url = this.service.pageUrl( params );
        assertEquals( "/portal/draft/a/b/mycontent", url );
    }

    @Test
    public void createUrl_withIdAndPath()
    {
        final Content content = ContentFixtures.newContent();
        Mockito.when( this.contentService.getById( content.getId() ) ).thenReturn( content );

        final PageUrlParams params = new PageUrlParams().
            context( this.context ).
            id( "123456" ).
            path( "/a/b" );

        final String url = this.service.pageUrl( params );
        assertEquals( "/portal/draft/a/b/mycontent", url );
    }

    @Test
    public void createUrl_withId_notFound()
    {
        final Content content = ContentFixtures.newContent();
        Mockito.when( this.contentService.getById( content.getId() ) ).thenThrow(
            new ContentNotFoundException( content.getId(), Branch.from( "draft" ) ) );

        final PageUrlParams params = new PageUrlParams().
            context( this.context ).
            id( "123456" );

        final String url = this.service.pageUrl( params );
        assertEquals( "/portal/draft/context/path/_/error/404?message=Content+with+id+%5B123456%5D+was+not+found+in+branch+%5Bdraft%5D",
                      url );
    }
}
