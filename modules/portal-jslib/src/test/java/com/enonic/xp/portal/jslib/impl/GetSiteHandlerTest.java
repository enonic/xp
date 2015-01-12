package com.enonic.xp.portal.jslib.impl;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.site.Site;
import com.enonic.wem.script.command.CommandHandler;
import com.enonic.wem.script.mapper.ContentFixtures;

public class GetSiteHandlerTest
    extends AbstractHandlerTest
{
    private ContentService contentService;

    @Override
    protected CommandHandler createHandler()
        throws Exception
    {
        this.contentService = Mockito.mock( ContentService.class );

        final GetSiteHandler handler = new GetSiteHandler();
        handler.setContentService( this.contentService );

        return handler;
    }

    @Test
    public void getCurrentSite()
        throws Exception
    {
        final Site site = ContentFixtures.newSite();
        context.setSite( site );

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
