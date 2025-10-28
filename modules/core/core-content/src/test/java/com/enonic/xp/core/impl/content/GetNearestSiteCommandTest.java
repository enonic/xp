package com.enonic.xp.core.impl.content;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.site.Site;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class GetNearestSiteCommandTest
{
    private NodeService nodeService;

    private ContentNodeTranslator translator;

    private EventPublisher eventPublisher;

    private ContentTypeService contentTypeService;

    @BeforeEach
    void setUp()
    {
        this.contentTypeService = Mockito.mock( ContentTypeService.class );
        this.nodeService = Mockito.mock( NodeService.class );
        this.translator = Mockito.mock( ContentNodeTranslator.class );
        this.eventPublisher = Mockito.mock( EventPublisher.class );
    }

    @Test
    void get_nearest_site_content_is_site()
    {
        final Node node = Node.create().
            id( NodeId.from( "test" ) ).
            name( "myContent" ).
            parentPath( ContentConstants.CONTENT_ROOT_PATH ).
            build();

        final ContentId contentId = ContentId.from( "aaa" );

        final Site site = Site.create().path( "/mycontent" ).id( contentId ).build();

        Mockito.when( this.nodeService.getById( Mockito.any( NodeId.class ) ) ).thenReturn( node );
        Mockito.when( this.translator.fromNode( node ) ).thenReturn( site );

        assertEquals( site, createCommand( contentId ).execute() );
    }

    @Test
    void get_nearest_site_parent_is_site()
    {
        final ContentId contentId = ContentId.from( "aaa" );

        final Node node = Node.create().
            id( NodeId.from( "test" ) ).
            name( "myContent" ).
            parentPath( ContentConstants.CONTENT_ROOT_PATH ).
            build();

        final Content content = Content.create().
            id( contentId ).
            name( "name" ).
            parentPath( ContentPath.from( "/aaa" ) ).
            build();

        final Site parent = Site.create().
            path( "/mycontent" ).
            id( ContentId.from( "bbb" ) ).
            build();

        Mockito.when( this.nodeService.getById( Mockito.any( NodeId.class ) ) ).thenReturn( node );
        Mockito.when( this.nodeService.getByPath( Mockito.isA( NodePath.class ) ) ).thenReturn( node );
        Mockito.when( this.translator.fromNode( node ) ).thenReturn( content, parent );

        assertEquals( parent, createCommand( contentId ).execute() );
    }

    @Test
    void get_nearest_site_parent_of_parent_is_site()
    {
        final Node node = Node.create().
            id( NodeId.from( "test" ) ).
            name( "myContent" ).
            parentPath( ContentConstants.CONTENT_ROOT_PATH ).
            build();

        final ContentId contentId = ContentId.from( "aaa" );

        final Content content = Content.create().
            id( contentId ).
            name( "name" ).
            parentPath( ContentPath.from( "/aaa" ) ).
            build();

        final Content parent = Content.create().
            id( ContentId.from( "bbb" ) ).
            name( "renome" ).
            parentPath( ContentPath.from( "/bbb" ) ).
            build();

        final Site parentOfParent = Site.create().
            path( "/mycontent" ).
            id( ContentId.from( "ccc" ) ).
            build();

        Mockito.when( this.nodeService.getById( Mockito.any( NodeId.class ) ) ).thenReturn( node );
        Mockito.when( this.nodeService.getByPath( Mockito.isA( NodePath.class ) ) ).thenReturn( node );
        Mockito.when( this.translator.fromNode( node ) ).thenReturn( content, parent, parentOfParent );

        assertEquals( parentOfParent, createCommand( contentId ).execute() );
    }

    @Test
    void get_nearest_site_no_nearest_site()
    {
        final Node node = Node.create().
            id( NodeId.from( "test" ) ).
            name( "myContent" ).
            parentPath( ContentConstants.CONTENT_ROOT_PATH ).
            build();

        final ContentId contentId = ContentId.from( "aaa" );
        final Content content = Content.create().id( contentId ).name( "name" ).parentPath( ContentPath.from( "/aaa" ) ).build();

        final ContentPath contentPath = ContentPath.from( "/mycontent" );
        final Content parent = Content.create().path( contentPath ).id( ContentId.from( "bbb" ) ).build();

        Mockito.when( this.nodeService.getById( Mockito.any( NodeId.class ) ) ).thenReturn( node );
        Mockito.when( this.nodeService.getByPath( Mockito.isA( NodePath.class ) ) ).thenReturn( node );
        Mockito.when( this.translator.fromNode( node ) ).thenReturn( content, parent );

        assertNull( createCommand( contentId ).execute() );
    }

    private GetNearestSiteCommand createCommand( final ContentId contentId )
    {
        return GetNearestSiteCommand.create().
            contentId( contentId ).
            contentTypeService( this.contentTypeService ).
            nodeService( this.nodeService ).
            translator( this.translator ).
            eventPublisher( this.eventPublisher ).
            build();
    }
}
