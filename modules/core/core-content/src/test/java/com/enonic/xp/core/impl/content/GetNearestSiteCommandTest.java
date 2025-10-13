package com.enonic.xp.core.impl.content;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
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

    private EventPublisher eventPublisher;

    private ContentTypeService contentTypeService;

    @BeforeEach
    void setUp()
    {
        this.contentTypeService = Mockito.mock( ContentTypeService.class );
        this.nodeService = Mockito.mock( NodeService.class );
        this.eventPublisher = Mockito.mock( EventPublisher.class );
    }

    @Test
    void get_nearest_site_content_is_site()
    {
        final Site site = ContentFixture.mockSite();

        Mockito.when( this.nodeService.getById( Mockito.any( NodeId.class ) ) ).thenReturn( ContentFixture.mockContentNode( site ) );

        assertEquals( site.getPath(), createCommand( site.getId() ).execute().getPath() );
    }

    @Test
    void get_nearest_site_parent_is_site()
    {
        final Site parent = ContentFixture.mockSite();
        final Content content = ContentFixture.mockContent( parent.getPath(), "my-content" );

        final Node node = ContentFixture.mockContentNode( content );

        Mockito.when( this.nodeService.getById( Mockito.any( NodeId.class ) ) ).thenReturn( node );
        Mockito.when( this.nodeService.getByPath( Mockito.isA( NodePath.class ) ) )
            .thenReturn( node, ContentFixture.mockContentNode( parent ) );

        assertEquals( parent.getPath(), createCommand( content.getId() ).execute().getPath() );
    }

    @Test
    void get_nearest_site_parent_of_parent_is_site()
    {
        final Site site = ContentFixture.mockSite();
        final Content contentParent = ContentFixture.mockContent( site.getPath(), "my-content-parent" );
        final Content content = ContentFixture.mockContent( contentParent.getPath(), "my-content" );

        final Node node = ContentFixture.mockContentNode( content );

        Mockito.when( this.nodeService.getById( Mockito.any( NodeId.class ) ) ).thenReturn( node );
        Mockito.when( this.nodeService.getByPath( Mockito.isA( NodePath.class ) ) ).thenReturn( ContentFixture.mockContentNode(content), ContentFixture.mockContentNode(contentParent), ContentFixture.mockContentNode(site) );

        assertEquals( site.getPath(), createCommand( content.getId() ).execute().getPath() );
    }

    @Test
    void get_nearest_site_no_nearest_site()
    {
        final Content parent = ContentFixture.mockContent();

        final Content content = ContentFixture.mockContent( parent.getPath(), "my-content" );

        final Node node = ContentFixture.mockContentNode( content );
        Mockito.when( this.nodeService.getById( Mockito.any( NodeId.class ) ) ).thenReturn( node );
        Mockito.when( this.nodeService.getByPath( Mockito.isA( NodePath.class ) ) )
            .thenReturn( node, ContentFixture.mockContentNode( parent ) );

        assertNull( createCommand( content.getId() ).execute() );
    }

    private GetNearestSiteCommand createCommand( final ContentId contentId )
    {
        return GetNearestSiteCommand.create().
            contentId( contentId ).
            contentTypeService( this.contentTypeService ).
            nodeService( this.nodeService ).
            eventPublisher( this.eventPublisher ).
            build();
    }
}
