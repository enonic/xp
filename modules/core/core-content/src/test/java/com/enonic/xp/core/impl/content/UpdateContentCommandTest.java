package com.enonic.xp.core.impl.content;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.media.MediaInfo;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.xdata.XDataService;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.site.SiteService;

public class UpdateContentCommandTest
{
    private static final Instant CREATED_TIME = LocalDateTime.of( 2013, 1, 1, 12, 0, 0, 0 ).toInstant( ZoneOffset.UTC );

    private final ContentTypeService contentTypeService = Mockito.mock( ContentTypeService.class );

    private final XDataService xDataService = Mockito.mock( XDataService.class );

    private final SiteService siteService = Mockito.mock( SiteService.class );

    private final NodeService nodeService = Mockito.mock( NodeService.class );

    private final ContentNodeTranslator translator = Mockito.mock( ContentNodeTranslator.class );

    private final EventPublisher eventPublisher = Mockito.mock( EventPublisher.class );

    private final MediaInfo mediaInfo = MediaInfo.create().mediaType( "image/jpg" ).build();

    @Test(expected = ContentNotFoundException.class)
    public void given_content_not_found_when_handle_then_NOT_FOUND_is_returned()
        throws Exception
    {
        // setup
        PropertyTree existingContentData = new PropertyTree();
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
            xDataService( this.xDataService ).
            siteService( this.siteService ).
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
        PropertyTree existingContentData = new PropertyTree();
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
            xDataService( this.xDataService ).
            siteService( this.siteService ).
            build();

        final Node mockNode = Node.create().build();
        Mockito.when( nodeService.getById( NodeId.from( existingContent.getId() ) ) ).thenReturn( mockNode );
        Mockito.when( translator.fromNode( mockNode, true ) ).thenReturn( existingContent );

        // exercise
        command.execute();

        // verify
        Mockito.verify( nodeService, Mockito.never() ).update( Mockito.isA( UpdateNodeParams.class ) );
    }

    private Content createContent( final PropertyTree contentData )
    {
        return Content.create().
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
