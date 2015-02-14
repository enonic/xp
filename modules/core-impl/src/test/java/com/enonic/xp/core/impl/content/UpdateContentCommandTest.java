package com.enonic.xp.core.impl.content;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.core.content.Content;
import com.enonic.xp.core.content.ContentId;
import com.enonic.xp.core.content.ContentNotFoundException;
import com.enonic.xp.core.content.ContentPath;
import com.enonic.xp.core.content.UpdateContentParams;
import com.enonic.xp.core.data.PropertyTree;
import com.enonic.xp.core.event.EventPublisher;
import com.enonic.xp.core.media.MediaInfo;
import com.enonic.xp.core.module.ModuleService;
import com.enonic.xp.core.node.Node;
import com.enonic.xp.core.node.NodeId;
import com.enonic.xp.core.node.NodeNotFoundException;
import com.enonic.xp.core.node.NodeService;
import com.enonic.xp.core.node.UpdateNodeParams;
import com.enonic.xp.core.schema.content.ContentTypeService;
import com.enonic.xp.core.schema.mixin.MixinService;
import com.enonic.xp.core.security.PrincipalKey;

import static com.enonic.xp.core.content.Content.newContent;

public class UpdateContentCommandTest
{
    private static final Instant CREATED_TIME = LocalDateTime.of( 2013, 1, 1, 12, 0, 0, 0 ).toInstant( ZoneOffset.UTC );

    private final ContentTypeService contentTypeService = Mockito.mock( ContentTypeService.class );

    private final MixinService mixinService = Mockito.mock( MixinService.class );

    private final ModuleService moduleService = Mockito.mock( ModuleService.class );

    private final NodeService nodeService = Mockito.mock( NodeService.class );

    private final ContentNodeTranslator translator = Mockito.mock( ContentNodeTranslator.class );

    private final EventPublisher eventPublisher = Mockito.mock( EventPublisher.class );

    private final MediaInfo mediaInfo = MediaInfo.create().mediaType( "image/jpg" ).build();

    @Test(expected = ContentNotFoundException.class)
    public void given_content_not_found_when_handle_then_NOT_FOUND_is_returned()
        throws Exception
    {
        // setup
        PropertyTree existingContentData = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        existingContentData.addString( "myData", "aaa" );

        ContentId contentId = ContentId.from( "mycontent" );

        UpdateContentParams params = new UpdateContentParams().
            contentId( contentId ).
            editor( edit -> {
            } );

        UpdateContentCommand command = UpdateContentCommand.create( params ).
            contentTypeService( this.contentTypeService ).
            nodeService( this.nodeService ).
            translator( this.translator ).
            eventPublisher( this.eventPublisher ).
            mediaInfo( this.mediaInfo ).
            mixinService( this.mixinService ).
            moduleService( this.moduleService ).
            build();

        Mockito.when( nodeService.getById( Mockito.isA( NodeId.class ) ) ).thenThrow( new NodeNotFoundException( "Node not found" ) );

        // exercise
        command.execute();
    }


    @Test
    public void contentDao_update_not_invoked_when_nothing_is_changed()
        throws Exception
    {
        // setup
        PropertyTree existingContentData = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        existingContentData.addString( "myData", "aaa" );

        Content existingContent = createContent( existingContentData );

        UpdateContentParams params = new UpdateContentParams().
            contentId( existingContent.getId() ).
            editor( edit -> {
            } );

        UpdateContentCommand command = UpdateContentCommand.create( params ).
            contentTypeService( this.contentTypeService ).
            nodeService( this.nodeService ).
            translator( this.translator ).
            eventPublisher( this.eventPublisher ).
            mediaInfo( this.mediaInfo ).
            mixinService( this.mixinService ).
            moduleService( this.moduleService ).
            build();

        final Node mockNode = Node.newNode().build();
        Mockito.when( nodeService.getById( NodeId.from( existingContent.getId() ) ) ).thenReturn( mockNode );
        Mockito.when( translator.fromNode( mockNode ) ).thenReturn( existingContent );

        // exercise
        command.execute();

        // verify
        Mockito.verify( nodeService, Mockito.never() ).update( Mockito.isA( UpdateNodeParams.class ) );
    }

    private Content createContent( final PropertyTree contentData )
    {
        return newContent().
            id( ContentId.from( "1" ) ).
            parentPath( ContentPath.ROOT ).
            name( "mycontent" ).
            createdTime( CREATED_TIME ).
            displayName( "MyContent" ).
            owner( PrincipalKey.from( "user:system:admin" ) ).
            data( contentData ).
            build();
    }
}
