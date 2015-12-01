package com.enonic.xp.core.impl.content;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.site.Site;

import static org.junit.Assert.*;

public class GetNearestSiteCommandTest
{
    private ContentService contentService;

    @Before
    public void setUp()
        throws Exception
    {
        this.contentService = Mockito.mock( ContentService.class );
    }

    @Test
    public void get_nearest_site_content_is_site()
        throws Exception
    {
        final ContentId contentId = ContentId.from( "aaa" );

        final Site site = Site.create().path( "/mycontent" ).id( contentId ).build();
        Mockito.when( this.contentService.getById( contentId ) ).thenReturn( site );

        final GetNearestSiteCommand command = GetNearestSiteCommand.create().
            contentId( contentId ).
            contentService( this.contentService ).
            build();

        assertEquals( site, command.execute() );
    }

    @Test
    public void get_nearest_site_parent_is_site()
        throws Exception
    {
        final ContentId contentId = ContentId.from( "aaa" );
        final ContentPath parentPath = ContentPath.from( "/aaa" );

        final Content content = Content.create().
            id( contentId ).
            name( "name" ).
            parentPath( parentPath ).
            build();

        Mockito.when( this.contentService.getById( contentId ) ).
            thenReturn( content );

        final Site parent = Site.create().
            path( "/mycontent" ).
            id( ContentId.from( "bbb" ) ).
            build();

        Mockito.when( this.contentService.getByPath( parentPath ) ).
            thenReturn( parent );

        final GetNearestSiteCommand command = GetNearestSiteCommand.create().
            contentId( contentId ).
            contentService( this.contentService ).
            build();

        assertEquals( parent, command.execute() );
    }

    @Test
    public void get_nearest_site_parent_of_parent_is_site()
        throws Exception
    {
        final ContentId contentId = ContentId.from( "aaa" );

        final Content content = Content.create().
            id( contentId ).
            name( "name" ).
            parentPath( ContentPath.from( "/aaa" ) ).
            build();

        Mockito.when( this.contentService.getById( contentId ) ).
            thenReturn( content );

        final Content parent = Content.create().
            id( ContentId.from( "bbb" ) ).
            name( "renome" ).
            parentPath( ContentPath.from( "/bbb" ) ).
            build();

        final Site parentOfParent = Site.create().
            path( "/mycontent" ).
            id( ContentId.from( "ccc" ) ).
            build();

        Mockito.when( this.contentService.getByPath( Mockito.isA( ContentPath.class ) ) ).
            thenReturn( parent ).
            thenReturn( parentOfParent );

        final GetNearestSiteCommand command = GetNearestSiteCommand.create().
            contentId( contentId ).
            contentService( this.contentService ).
            build();

        assertEquals( parentOfParent, command.execute() );
    }

    @Test
    public void get_nearest_site_no_nearest_site()
        throws Exception
    {
        final ContentId contentId = ContentId.from( "aaa" );
        final Content content = Content.create().id( contentId ).name( "name" ).parentPath( ContentPath.from( "/aaa" ) ).build();
        Mockito.when( this.contentService.getById( contentId ) ).thenReturn( content );

        final ContentPath contentPath = ContentPath.from( "/mycontent" );
        final Content parent = Content.create().path( contentPath ).id( ContentId.from( "bbb" ) ).build();
        Mockito.when( this.contentService.getByPath( Mockito.isA( ContentPath.class ) ) ).thenReturn( parent );

        final GetNearestSiteCommand command = GetNearestSiteCommand.create().
            contentId( contentId ).
            contentService( this.contentService ).
            build();

        assertNull( command.execute() );
    }
}