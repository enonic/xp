package com.enonic.wem.core.content;


import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.elasticsearch.common.joda.time.DateTimeUtils;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.blob.BlobService;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.UpdateContentParams;
import com.enonic.wem.api.content.attachment.AttachmentService;
import com.enonic.wem.api.content.attachment.Attachments;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.core.entity.Node;
import com.enonic.wem.core.entity.NodeId;
import com.enonic.wem.core.entity.NodeService;
import com.enonic.wem.core.entity.UpdateNodeParams;
import com.enonic.wem.core.entity.dao.NodeNotFoundException;

import static com.enonic.wem.api.content.Content.editContent;
import static com.enonic.wem.api.content.Content.newContent;

public class UpdateContentCommandTest
{
    private static final Instant CREATED_TIME = LocalDateTime.of( 2013, 1, 1, 12, 0, 0, 0 ).toInstant( ZoneOffset.UTC );

    private static final Instant UPDATED_TIME = LocalDateTime.of( 2013, 1, 1, 13, 0, 0, 0 ).toInstant( ZoneOffset.UTC );

    private final AttachmentService attachmentService = Mockito.mock( AttachmentService.class );

    private final ContentTypeService contentTypeService = Mockito.mock( ContentTypeService.class );

    private final NodeService nodeService = Mockito.mock( NodeService.class );

    private final BlobService blobService = Mockito.mock( BlobService.class );

    private final ContentNodeTranslator translator = Mockito.mock( ContentNodeTranslator.class );

    //@Ignore // Rewriting content stuff to node
    @Test(expected = ContentNotFoundException.class)
    public void given_content_not_found_when_handle_then_NOT_FOUND_is_returned()
        throws Exception
    {
        // setup
        DateTimeUtils.setCurrentMillisFixed( UPDATED_TIME.toEpochMilli() );

        ContentData existingContentData = new ContentData();
        existingContentData.add( Property.newString( "myData", "aaa" ) );

        final ContentData unchangedContentData = new ContentData();
        unchangedContentData.add( Property.newString( "myData", "aaa" ) );

        final ContentId contentId = ContentId.from( "mycontent" );

        UpdateContentParams params = new UpdateContentParams().
            modifier( PrincipalKey.from( "system:user:admin" ) ).
            contentId( contentId ).
            editor( toBeEdited -> editContent( toBeEdited ).contentData( unchangedContentData ) );

        UpdateContentCommand command = UpdateContentCommand.create( params ).
            contentTypeService( this.contentTypeService ).
            attachmentService( this.attachmentService ).
            nodeService( this.nodeService ).
            blobService( this.blobService ).
            translator( this.translator ).
            build();

        Mockito.when( attachmentService.getAll( contentId ) ).thenReturn( Attachments.empty() );

        Mockito.when( nodeService.getById( Mockito.isA( NodeId.class ) ) ).thenThrow( new NodeNotFoundException( "Node not found" ) );

        // exercise
        command.execute();
    }


    @Test
    public void contentDao_update_not_invoked_when_nothing_is_changed()
        throws Exception
    {
        // setup
        DateTimeUtils.setCurrentMillisFixed( UPDATED_TIME.toEpochMilli() );

        ContentData existingContentData = new ContentData();
        existingContentData.add( Property.newString( "myData", "aaa" ) );

        Content existingContent = createContent( existingContentData );

        final ContentData unchangedContentData = new ContentData();
        unchangedContentData.add( Property.newString( "myData", "aaa" ) );

        UpdateContentParams params = new UpdateContentParams().
            modifier( PrincipalKey.from( "system:user:admin" ) ).
            contentId( existingContent.getId() ).
            editor( toBeEdited -> editContent( toBeEdited ).contentData( unchangedContentData ) );

        UpdateContentCommand command = UpdateContentCommand.create( params ).
            contentTypeService( this.contentTypeService ).
            attachmentService( this.attachmentService ).
            nodeService( this.nodeService ).
            blobService( this.blobService ).
            translator( this.translator ).
            build();

        final Node mockNode = Node.newNode().build();
        Mockito.when( nodeService.getById( NodeId.from( existingContent.getId() ) ) ).
            thenReturn( mockNode );
        Mockito.when( translator.fromNode( mockNode ) ).thenReturn( existingContent );

        // exercise
        command.execute();

        // verify
        Mockito.verify( nodeService, Mockito.never() ).update( Mockito.isA( UpdateNodeParams.class ) );
    }

    private Content createContent( final ContentData contentData )
    {
        return newContent().
            id( ContentId.from( "1" ) ).
            parentPath( ContentPath.ROOT ).
            name( "mycontent" ).
            createdTime( CREATED_TIME ).
            displayName( "MyContent" ).
            owner( PrincipalKey.from( "system:user:admin" ) ).
            contentData( contentData ).
            build();
    }
}
