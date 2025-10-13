package com.enonic.xp.core.impl.content;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.MoveContentParams;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.node.MoveNodeParams;
import com.enonic.xp.node.MoveNodeResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.schema.xdata.XDataService;
import com.enonic.xp.site.Site;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MoveContentCommandTest
{
    ContentTypeService contentTypeService = mock( ContentTypeService.class );

    NodeService nodeService = mock( NodeService.class );

    EventPublisher eventPublisher = mock( EventPublisher.class );

    XDataService xDataService = mock( XDataService.class );

    @Test
    void move_non_existing_content()
    {
        PropertyTree existingContentData = new PropertyTree();
        existingContentData.addString( "myData", "aaa" );

        ContentId contentId = ContentId.from( "mycontent" );

        MoveContentParams params = MoveContentParams.create().contentId( contentId ).parentContentPath( ContentPath.ROOT ).build();

        MoveContentCommand command = MoveContentCommand.create( params )
            .contentTypeService( this.contentTypeService )
            .nodeService( this.nodeService )
            .xDataService( this.xDataService )
            .eventPublisher( this.eventPublisher )
            .build();

        when( nodeService.getById( Mockito.isA( NodeId.class ) ) ).thenThrow( new NodeNotFoundException( "Node not found" ) );

        // exercise
        assertThrows( ContentNotFoundException.class, () -> command.execute() );
    }

    @Test
    void move_fragment_to_the_same_site()
    {
        final Site parentSite = ContentFixture.mockSite();
        final Content existingContent = ContentFixture.mockContent(parentSite.getPath(), "my-content" );
        final Content existingFolder = ContentFixture.mockContent(parentSite.getPath(), "my-folder");

        MoveContentParams params =
            MoveContentParams.create().contentId( existingContent.getId() ).parentContentPath( existingFolder.getPath() ).build();

        final MoveContentCommand command = MoveContentCommand.create( params )
            .contentTypeService( this.contentTypeService )
            .nodeService( this.nodeService )
            .xDataService( this.xDataService )
            .eventPublisher( this.eventPublisher )
            .build();

        final Node mockNode = ContentFixture.mockContentNode( existingContent );

        when( nodeService.getById( NodeId.from( existingContent.getId() ) ) ).thenReturn( mockNode );

        when( nodeService.move( Mockito.any( MoveNodeParams.class ) ) ).thenReturn( MoveNodeResult.create()
                                                                                        .addMovedNode( MoveNodeResult.MovedNode.create()
                                                                                                           .previousPath( mockNode.path() )
                                                                                                           .node( mockNode )
                                                                                                           .build() )
                                                                                        .build() );

        final Node mockFolderNode = ContentFixture.mockContentNode( existingFolder );

        when( nodeService.getByPath( ContentNodeHelper.translateContentPathToNodePath( existingFolder.getPath() ) ) ).thenReturn(
            mockFolderNode );

        final ContentType contentType = ContentType.create()
            .name( ContentTypeName.folder() )
            .displayName( "folder" )
            .setBuiltIn()
            .setFinal( false )
            .setAbstract( false )
            .build();

        when( contentTypeService.getByName( new GetContentTypeParams().contentTypeName( existingFolder.getType() ) ) ).thenReturn(
            contentType );

        // exercise
        command.execute();
        Mockito.verify( nodeService, Mockito.times( 1 ) ).move( Mockito.any( MoveNodeParams.class ) );
    }
}
