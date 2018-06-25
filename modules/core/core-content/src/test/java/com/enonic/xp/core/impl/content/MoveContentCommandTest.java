package com.enonic.xp.core.impl.content;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentAlreadyMovedException;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.MoveContentParams;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.site.Site;

public class MoveContentCommandTest
{
    private final ContentTypeService contentTypeService = Mockito.mock( ContentTypeService.class );

    private final NodeService nodeService = Mockito.mock( NodeService.class );

    private final ContentService contentService = Mockito.mock( ContentService.class );

    private final ContentNodeTranslator translator = Mockito.mock( ContentNodeTranslator.class );

    private final EventPublisher eventPublisher = Mockito.mock( EventPublisher.class );

    @Test(expected = NodeNotFoundException.class)
    public void move_non_existing_content()
        throws Exception
    {
        PropertyTree existingContentData = new PropertyTree();
        existingContentData.addString( "myData", "aaa" );

        ContentId contentId = ContentId.from( "mycontent" );

        MoveContentParams params = MoveContentParams.create().
            contentId( contentId ).
            parentContentPath( ContentPath.ROOT ).
            build();

        MoveContentCommand command = MoveContentCommand.create( params ).
            contentTypeService( this.contentTypeService ).
            nodeService( this.nodeService ).
            contentService( this.contentService ).
            translator( this.translator ).
            eventPublisher( this.eventPublisher ).
            build();

        Mockito.when( nodeService.getById( Mockito.isA( NodeId.class ) ) ).thenThrow( new NodeNotFoundException( "Node not found" ) );

        // exercise
        command.execute();
    }

    @Test
    public void move_fragment_to_the_same_site()
        throws Exception
    {
        final PropertyTree existingContentData = new PropertyTree();
        existingContentData.addString( "myData", "aaa" );

        final Site parentSite = createSite( existingContentData, ContentPath.ROOT );
        final Content existingContent = createContent( existingContentData, parentSite.getPath(), ContentTypeName.fragment() );
        final Content existingFolder = createContent( existingContentData, parentSite.getPath(), ContentTypeName.folder() );

        MoveContentParams params = MoveContentParams.create().
            contentId( existingContent.getId() ).
            parentContentPath( existingFolder.getPath() ).
            build();

        final MoveContentCommand command = MoveContentCommand.create( params ).
            contentTypeService( this.contentTypeService ).
            nodeService( this.nodeService ).
            contentService( this.contentService ).
            translator( this.translator ).
            eventPublisher( this.eventPublisher ).
            build();

        final Node mockNode = Node.create().parentPath( NodePath.ROOT ).build();

        Mockito.when( nodeService.getById( NodeId.from( existingContent.getId() ) ) ).thenReturn( mockNode );

        Mockito.when( nodeService.move( Mockito.any( NodeId.class ), Mockito.any(), Mockito.any() ) ).thenReturn( mockNode );

        Mockito.when( translator.fromNode( mockNode, true ) ).thenReturn( existingContent );
        Mockito.when( translator.fromNode( mockNode, false ) ).thenReturn( existingContent );

        Mockito.when( contentService.getByPath( existingFolder.getPath() ) ).thenReturn( existingFolder );
        Mockito.when( contentService.getNearestSite( existingContent.getId() ) ).thenReturn( parentSite );

        final ContentType contentType = ContentType.create().
            name( "folder" ).
            displayName( "folder" ).
            setBuiltIn().setFinal( false ).setAbstract( false ).build();

        Mockito.when( contentTypeService.getByName( new GetContentTypeParams().contentTypeName( existingFolder.getType() ) ) ).thenReturn(
            contentType );

        // exercise
        command.execute();
        Mockito.verify( nodeService, Mockito.times( 1 ) ).move( Mockito.any( NodeId.class ), Mockito.any(), Mockito.any() );

    }

    @Test(expected = ContentAlreadyMovedException.class)
    public void move_to_the_same_parent()
        throws Exception
    {
        final PropertyTree existingContentData = new PropertyTree();
        existingContentData.addString( "myData", "aaa" );

        final Content existingContent = createContent( existingContentData, ContentPath.ROOT, ContentTypeName.folder() );

        final MoveContentParams params = MoveContentParams.create().
            contentId( existingContent.getId() ).
            parentContentPath( ContentPath.ROOT ).
            build();

        final MoveContentCommand command = MoveContentCommand.create( params ).
            contentTypeService( this.contentTypeService ).
            nodeService( this.nodeService ).
            contentService( this.contentService ).
            translator( this.translator ).
            eventPublisher( this.eventPublisher ).
            build();

        final Node mockNode = Node.create().name( existingContent.getName().toString() ).parentPath(
            ContentNodeHelper.translateContentParentToNodeParentPath( existingContent.getParentPath() ) ).build();

        Mockito.when( nodeService.getById( NodeId.from( existingContent.getId() ) ) ).thenReturn( mockNode );
        Mockito.when( translator.fromNode( mockNode, true ) ).thenReturn( existingContent );
        Mockito.when( translator.fromNode( mockNode, false ) ).thenReturn( existingContent );

        // exercise
        command.execute();
    }

    private Site createSite( final PropertyTree contentData, final ContentPath parentPath )
    {

        return Site.create().
            id( ContentId.from( "2" ) ).
            parentPath( parentPath ).
            type( ContentTypeName.site() ).
            name( "mycontent" ).
            displayName( "MyContent" ).
            owner( PrincipalKey.from( "user:system:admin" ) ).
            data( contentData ).
            build();
    }

    private Content createContent( final PropertyTree contentData, final ContentPath parentPath, final ContentTypeName type )
    {
        return Content.create().
            id( ContentId.from( "1" ) ).
            parentPath( parentPath ).
            type( type ).
            name( "mycontent" ).
            displayName( "MyContent" ).
            owner( PrincipalKey.from( "user:system:admin" ) ).
            data( contentData ).
            build();
    }
}
