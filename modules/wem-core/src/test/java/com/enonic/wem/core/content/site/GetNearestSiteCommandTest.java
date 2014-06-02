package com.enonic.wem.core.content.site;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.site.Site;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.context.Context;

import static org.junit.Assert.*;

public class GetNearestSiteCommandTest
{
    private GetNearestSiteCommand command;

    private ContentService contentService;

    @Before
    public void setUp()
        throws Exception
    {
        this.contentService = Mockito.mock( ContentService.class );

        this.command = new GetNearestSiteCommand();
        this.command.contentService( this.contentService );
    }

    @Test
    public void get_nearest_site_content_is_site()
        throws Exception
    {
        final ContentId contentId = ContentId.from( "aaa" );

        final Content content = Content.newContent().path( "/mycontent" ).id( contentId ).site( createSite() ).build();
        Mockito.when( this.contentService.getById( contentId, ContentConstants.DEFAULT_CONTEXT ) ).thenReturn( content );

        this.command.contentId( contentId );

        assertEquals( content, this.command.execute() );
    }

    @Test
    public void get_nearest_site_parent_is_site()
        throws Exception
    {
        final ContentId contentId = ContentId.from( "aaa" );
        final ContentPath parentPath = ContentPath.from( "/aaa" );

        final Content content = Content.newContent().id( contentId ).name( "name" ).parentPath( parentPath ).build();
        Mockito.when( this.contentService.getById( contentId, ContentConstants.DEFAULT_CONTEXT ) ).thenReturn( content );

        final Content parent = Content.newContent().path( "/mycontent" ).id( ContentId.from( "bbb" ) ).site( createSite() ).build();
        Mockito.when( this.contentService.getByPath( parentPath, ContentConstants.DEFAULT_CONTEXT ) ).thenReturn( parent );

        this.command.contentId( contentId );

        assertEquals( parent, this.command.execute() );
    }

    @Test
    public void get_nearest_site_parent_of_parent_is_site()
        throws Exception
    {
        final ContentId contentId = ContentId.from( "aaa" );

        final Content content = Content.newContent().
            id( contentId ).
            name( "name" ).
            parentPath( ContentPath.from( "/aaa" ) ).
            build();

        Mockito.when( this.contentService.getById( contentId, ContentConstants.DEFAULT_CONTEXT ) ).
            thenReturn( content );

        final Content parent = Content.newContent().
            id( ContentId.from( "bbb" ) ).
            name( "renome" ).
            parentPath( ContentPath.from( "/bbb" ) ).
            build();

        final Content parentOfParent = Content.newContent().
            path( "/mycontent" ).
            id( ContentId.from( "ccc" ) ).
            site( createSite() ).
            build();

        Mockito.when( this.contentService.getByPath( Mockito.isA( ContentPath.class ), Mockito.isA( Context.class ) ) ).
            thenReturn( parent ).
            thenReturn( parentOfParent );

        this.command.contentId( contentId );

        assertEquals( parentOfParent, this.command.execute() );
    }

    @Test
    public void get_nearest_site_no_nearest_site()
        throws Exception
    {
        final ContentId contentId = ContentId.from( "aaa" );
        final Content content = Content.newContent().id( contentId ).name( "name" ).parentPath( ContentPath.from( "/aaa" ) ).build();
        Mockito.when( this.contentService.getById( contentId, ContentConstants.DEFAULT_CONTEXT ) ).thenReturn( content );

        final ContentPath contentPath = ContentPath.from( "/mycontent" );
        final Content parent = Content.newContent().path( contentPath ).id( ContentId.from( "bbb" ) ).site( null ).build();
        Mockito.when( this.contentService.getByPath( Mockito.isA( ContentPath.class ), Mockito.isA( Context.class ) ) ).thenReturn(
            parent );

        this.command.contentId( contentId );

        assertNull( this.command.execute() );
    }

    private Site createSite()
    {
        return Site.newSite().template( SiteTemplateKey.from( "mySiteTemplate-1.0.0" ) ).build();
    }
}