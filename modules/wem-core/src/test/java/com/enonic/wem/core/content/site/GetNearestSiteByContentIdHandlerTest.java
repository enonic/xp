package com.enonic.wem.core.content.site;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.site.GetNearestSiteByContentId;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.site.Site;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;

import static org.junit.Assert.*;

public class GetNearestSiteByContentIdHandlerTest
    extends AbstractCommandHandlerTest
{
    private GetNearestSiteByContentIdHandler handler;

    private ContentService contentService;

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        contentService = Mockito.mock( ContentService.class );

        handler = new GetNearestSiteByContentIdHandler();
        handler.setContext( this.context );
        handler.setContentService( this.contentService );
    }

    @Test
    public void get_nearest_site_content_is_site()
        throws Exception
    {
        final ContentId contentId = ContentId.from( "aaa" );

        final Content content = Content.newContent().path( "/mycontent" ).id( contentId ).site( createSite() ).build();
        Mockito.when( contentService.getById( contentId ) ).thenReturn( content );

        final GetNearestSiteByContentId command = Commands.site().getNearestSite().content( contentId );
        handler.setCommand( command );
        handler.handle();

        assertEquals( content, command.getResult() );
    }

    @Test
    public void get_nearest_site_parent_is_site()
        throws Exception
    {
        final ContentId contentId = ContentId.from( "aaa" );
        final ContentPath parentPath = ContentPath.from( "/aaa" );

        final Content content = Content.newContent().id( contentId ).name( "name" ).parentPath( parentPath ).build();
        Mockito.when( contentService.getById( contentId ) ).thenReturn( content );

        final Content parent = Content.newContent().path( "/mycontent" ).id( ContentId.from( "bbb" ) ).site( createSite() ).build();
        Mockito.when( contentService.getByPath( parentPath ) ).thenReturn( parent );

        final GetNearestSiteByContentId command = Commands.site().getNearestSite().content( contentId );
        handler.setCommand( command );
        handler.handle();

        assertEquals( parent, command.getResult() );
    }

    @Test
    public void get_nearest_site_parent_of_parent_is_site()
        throws Exception
    {
        final ContentId contentId = ContentId.from( "aaa" );

        final Content content = Content.newContent().id( contentId ).name( "name" ).parentPath( ContentPath.from( "/aaa" ) ).build();
        Mockito.when( contentService.getById( contentId ) ).thenReturn( content );

        final Content parent =
            Content.newContent().id( ContentId.from( "bbb" ) ).name( "renome" ).parentPath( ContentPath.from( "/bbb" ) ).build();
        final Content parentOfParent = Content.newContent().path( "/mycontent" ).id( ContentId.from( "ccc" ) ).site( createSite() ).build();
        Mockito.when( contentService.getByPath( Mockito.isA( ContentPath.class ) ) ).thenReturn( parent ).thenReturn( parentOfParent );

        final GetNearestSiteByContentId command = Commands.site().getNearestSite().content( contentId );
        handler.setCommand( command );
        handler.handle();

        assertEquals( parentOfParent, command.getResult() );
    }

    @Test
    public void get_nearest_site_no_nearest_site()
        throws Exception
    {
        final ContentId contentId = ContentId.from( "aaa" );
        final Content content = Content.newContent().id( contentId ).name( "name" ).parentPath( ContentPath.from( "/aaa" ) ).build();
        Mockito.when( contentService.getById( contentId ) ).thenReturn( content );

        final ContentPath contentPath = ContentPath.from( "/mycontent" );
        final Content parent = Content.newContent().path( contentPath ).id( ContentId.from( "bbb" ) ).site( null ).build();
        Mockito.when( contentService.getByPath( Mockito.isA( ContentPath.class ) ) ).thenReturn( parent );

        final GetNearestSiteByContentId command = Commands.site().getNearestSite().content( contentId );
        handler.setCommand( command );
        handler.handle();

        assertNull( command.getResult() );
    }

    private Site createSite()
    {
        return Site.newSite().template( SiteTemplateKey.from( "mySiteTemplate-1.0.0" ) ).build();
    }
}