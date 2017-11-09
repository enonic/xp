package com.enonic.xp.core.impl.content;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.RenameContentParams;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.core.impl.content.processor.ContentProcessor;
import com.enonic.xp.core.impl.content.processor.ContentProcessors;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.RenameNodeParams;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.PartDescriptorService;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.security.PrincipalKey;

public class RenameContentCommandTest
{
    Node mockNode;

    private ContentTypeService contentTypeService;

    private ContentService contentService;

    private EventPublisher eventPublisher;

    private ContentNodeTranslator translator;

    private NodeService nodeService;

    private PageDescriptorService pageDescriptorService;

    private PartDescriptorService partDescriptorService;

    private LayoutDescriptorService layoutDescriptorService;

    private ContentType contentType;

    private MixinService mixinService;

    private ContentProcessors contentProcessors;

    @Before
    public void setUp()
        throws Exception
    {
        this.contentTypeService = Mockito.mock( ContentTypeService.class );
        this.contentService = Mockito.mock( ContentService.class );
        this.nodeService = Mockito.mock( NodeService.class );
        this.eventPublisher = Mockito.mock( EventPublisher.class );
        this.translator = Mockito.mock( ContentNodeTranslator.class );
        this.mixinService = Mockito.mock( MixinService.class );
        this.pageDescriptorService = Mockito.mock( PageDescriptorService.class );
        this.partDescriptorService = Mockito.mock( PartDescriptorService.class );
        this.layoutDescriptorService = Mockito.mock( LayoutDescriptorService.class );
        this.contentProcessors = Mockito.mock( ContentProcessors.class );

        contentType = ContentType.create().
            superType( ContentTypeName.documentMedia() ).
            name( ContentTypeName.dataMedia() ).
            build();

        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn( contentType );

        mockNode = Node.create().id( NodeId.from( "testId" ) ).build();

        Mockito.when( nodeService.rename( Mockito.isA( RenameNodeParams.class ) ) ).thenReturn( mockNode );
        Mockito.when( nodeService.getById( mockNode.id() ) ).thenReturn( mockNode );

    }

    @Test
    public void test_valid_changed()
        throws Exception
    {

        final Content content = createContent( true );

        Mockito.when( this.nodeService.getById( Mockito.any( NodeId.class ) ) ).thenReturn( mockNode );
        Mockito.when( translator.fromNode( mockNode, false ) ).thenReturn( content );
        Mockito.when( translator.fromNode( mockNode, true ) ).thenReturn( content );
        Iterator<ContentProcessor> contentProcessorIterator = Mockito.mock( Iterator.class );
        Mockito.when( contentProcessors.iterator() ).thenReturn( contentProcessorIterator );

        final RenameContentParams params =
            RenameContentParams.create().contentId( content.getId() ).newName( ContentName.unnamed() ).build();

        createCommand( params ).execute();

        Mockito.verify( nodeService, Mockito.times( 1 ) ).update( Mockito.isA( UpdateNodeParams.class ) );

    }

    @Test
    public void test_valid_not_changed()
        throws Exception
    {

        final Content content = createContent( false );

        Mockito.when( translator.fromNode( mockNode, false ) ).thenReturn( content );
        Mockito.when( translator.fromNode( mockNode, true ) ).thenReturn( content );

        final RenameContentParams params =
            RenameContentParams.create().contentId( content.getId() ).newName( ContentName.unnamed() ).build();

        createCommand( params ).execute();

        Mockito.verify( contentService, Mockito.never() ).update( Mockito.isA( UpdateContentParams.class ) );

    }

    private Content createContent( final Boolean valid )
    {
        return Content.create().
            id( ContentId.from( "testId" ) ).
            path( "/mycontent" ).
            creator( PrincipalKey.from( "user:system:anonymous" ) ).
            type( ContentTypeName.folder() ).
            data( new PropertyTree() ).
            valid( valid ).
            build();
    }

    private RenameContentCommand createCommand( final RenameContentParams params )
    {
        return RenameContentCommand.create( params ).
            contentTypeService( this.contentTypeService ).
            nodeService( this.nodeService ).
            translator( this.translator ).
            eventPublisher( this.eventPublisher ).
            mixinService( this.mixinService ).
            contentProcessors( this.contentProcessors ).
            pageDescriptorService( this.pageDescriptorService ).
            partDescriptorService( this.partDescriptorService ).
            layoutDescriptorService( this.layoutDescriptorService ).
            build();
    }


}
