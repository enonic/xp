package com.enonic.xp.portal.impl.jslib.current;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.portal.impl.jslib.AbstractHandlerTest;
import com.enonic.xp.portal.impl.jslib.ContentFixtures;
import com.enonic.xp.portal.script.command.CommandHandler;
import com.enonic.xp.site.Site;

public class GetCurrentSiteHandlerTest
    extends AbstractHandlerTest
{
    private ContentService contentService;

    @Override
    protected CommandHandler createHandler()
        throws Exception
    {
        this.contentService = Mockito.mock( ContentService.class );

        final GetCurrentSiteHandler handler = new GetCurrentSiteHandler();
        handler.setContentService( this.contentService );

        return handler;
    }

    @Test
    public void getCurrentSite()
        throws Exception
    {
        final Site site = ContentFixtures.newSite();
        portalRequest.setSite( site );

        execute( "getCurrentSite" );
    }

    @Test
    public void getById()
        throws Exception
    {
        final Site site = ContentFixtures.newSite();
        Mockito.when( contentService.getNearestSite( site.getId() ) ).thenReturn( site );

        execute( "getById" );
    }

    @Test
    public void getByPath()
        throws Exception
    {
        final Content content = ContentFixtures.newContent();
        Mockito.when( this.contentService.getByPath( content.getPath() ) ).thenReturn( content );

        final Site site = ContentFixtures.newSite();
        Mockito.when( contentService.getNearestSite( content.getId() ) ).thenReturn( site );

        execute( "getByPath" );
    }

    @Test
    public void getById_notFound()
        throws Exception
    {
        final ContentId id = ContentId.from( "123456" );
        Mockito.when( this.contentService.getById( id ) ).thenThrow( new ContentNotFoundException( id, null ) );

        execute( "getById_notFound" );
    }

    @Test
    public void getByPath_notFound()
        throws Exception
    {
        final ContentPath path = ContentPath.from( "/a/b/mycontent" );
        Mockito.when( this.contentService.getByPath( path ) ).thenThrow( new ContentNotFoundException( path, null ) );

        execute( "getByPath_notFound" );
    }
}
