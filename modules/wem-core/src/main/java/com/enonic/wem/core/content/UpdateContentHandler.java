package com.enonic.wem.core.content;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

import javax.inject.Inject;
import javax.jcr.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.InputSupplier;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.blob.Blob;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.CreateContent;
import com.enonic.wem.api.command.content.GetContentById;
import com.enonic.wem.api.command.content.UpdateContent;
import com.enonic.wem.api.command.content.ValidateContentData;
import com.enonic.wem.api.command.entity.DeleteNodeByPath;
import com.enonic.wem.api.command.entity.UpdateNode;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentDataValidationException;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.content.attachment.Attachments;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.PropertyVisitor;
import com.enonic.wem.api.data.type.ValueTypes;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.validator.DataValidationError;
import com.enonic.wem.api.schema.content.validator.DataValidationErrors;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.entity.DeleteNodeByPathService;
import com.enonic.wem.core.entity.UpdateNodeHandler;
import com.enonic.wem.core.index.IndexService;
import com.enonic.wem.core.relationship.RelationshipService;
import com.enonic.wem.core.relationship.SyncRelationshipsCommand;

import static com.enonic.wem.api.content.Content.newContent;
import static com.enonic.wem.api.content.attachment.Attachment.newAttachment;

public class UpdateContentHandler
    extends CommandHandler<UpdateContent>
{
    private static final String THUMBNAIL_MIME_TYPE = "image/png";

    private RelationshipService relationshipService;

    private IndexService indexService;

    private final static Logger LOG = LoggerFactory.getLogger( UpdateContentHandler.class );

    @Override
    public void handle()
        throws Exception
    {
        final ContentNodeTranslator translator = new ContentNodeTranslator( this.context.getClient() );

        final GetContentById getContentByIdCommand = new GetContentById( command.getContentId() );
        final Content contentBeforeChange = new GetContentByIdService( this.context, getContentByIdCommand ).execute();

        final Content.EditBuilder editBuilder = command.getEditor().edit( contentBeforeChange );

        if ( !editBuilder.isChanges() )
        {
            command.setResult( contentBeforeChange );
            return;
        }

        Content editedContent = editBuilder.build();

        // TODO: Fix this
        //validateEditedContent( contentBeforeChange, editedContent );

        editedContent = newContent( editedContent ).
            modifier( command.getModifier() ).build();

        Attachments attachments = command.getAttachments();
        final Attachment thumbnailAttachment = resolveThumbnailAttachment( command, editedContent );
        if ( thumbnailAttachment != null )
        {
            attachments = attachments.add( thumbnailAttachment );
        }
        final UpdateNode updateNodeCommand = translator.toUpdateNodeCommand( editedContent, attachments );

        final UpdateNodeHandler updateNodeHandler = UpdateNodeHandler.create().
            context( this.context ).
            command( updateNodeCommand ).
            indexService( this.indexService ).
            build();
        updateNodeHandler.handle();

        final Node editedNode = updateNodeCommand.getResult().getPersistedNode();
        final Content persistedContent = translator.fromNode( editedNode );

        deleteRemovedEmbeddedContent( contentBeforeChange, persistedContent );

        command.setResult( persistedContent );
    }

    private void deleteRemovedEmbeddedContent( final Content persistedContent, final Content editedContent )
    throws Exception
    {
        final Map<ContentId, Content> embeddedContentsBeforeEdit = resolveEmbeddedContent( persistedContent.getContentData() );

        final Map<ContentId, Content> embeddedContentsToKeep = resolveEmbeddedContent( editedContent.getContentData() );

        // delete embedded contents not longer to keep
        for ( Content embeddedContentBeforeEdit : embeddedContentsBeforeEdit.values() )
        {
            if ( !embeddedContentsToKeep.containsKey( embeddedContentBeforeEdit.getId() ) )
            {
                final NodePath nodePathToEmbeddedContentNode =
                    EmbeddedNodePathFactory.create( embeddedContentBeforeEdit.getParentPath(), embeddedContentBeforeEdit.getName() );
                final DeleteNodeByPath deleteNodeByPathCommand = new DeleteNodeByPath( nodePathToEmbeddedContentNode );
                new DeleteNodeByPathService( this.context.getJcrSession(), indexService, deleteNodeByPathCommand ).execute();
            }
        }
    }


    private void syncRelationships( final Content persistedContent, final Content temporaryContent )
    {
        final Session session = context.getJcrSession();
        final Client client = context.getClient();

        final ContentId contentId = persistedContent.getId();

        relationshipService.syncRelationships( new SyncRelationshipsCommand().
            client( client ).
            jcrSession( session ).
            contentType( persistedContent.getType() ).
            contentToUpdate( contentId ).
            contentBeforeEditing( persistedContent.getContentData() ).
            contentAfterEditing( temporaryContent.getContentData() ) );
    }

    private void validateEditedContent( final Content persistedContent, final Content edited )
    {
        persistedContent.checkIllegalEdit( edited );

        if ( !edited.isDraft() )
        {
            validateContentData( context, edited );
        }
    }

    private ImmutableMap<ContentId, Content> resolveEmbeddedContent( final ContentData contentData )
    {
        final ImmutableMap.Builder<ContentId, Content> embeddedContent = new ImmutableMap.Builder<>();
        new PropertyVisitor()
        {
            @Override
            public void visit( final Property property )
            {

                final Content content = new GetContentByIdService( context, new GetContentById( property.getContentId() ) ).execute();

                if ( content != null )
                {
                    if ( content.isEmbedded() )
                    {
                        embeddedContent.put( content.getId(), content );
                    }
                }

            }
        }.restrictType( ValueTypes.CONTENT_ID ).traverse( contentData );

        return embeddedContent.build();
    }

    private void validateContentData( final CommandContext context, final Content modifiedContent )
    {
        final ValidateContentData validateContentData = Commands.content().validate();
        validateContentData.contentType( modifiedContent.getType() );
        validateContentData.contentData( modifiedContent.getContentData() );

        final DataValidationErrors dataValidationErrors = context.getClient().execute( validateContentData );

        for ( DataValidationError error : dataValidationErrors )
        {
            LOG.info( "*** DataValidationError: " + error.getErrorMessage() );
        }
        if ( dataValidationErrors.hasErrors() )
        {
            throw new ContentDataValidationException( dataValidationErrors.getFirst().getErrorMessage() );
        }
    }

    private Attachment resolveThumbnailAttachment( final UpdateContent command, final Content content )
    {
        final ContentType contentType = getContentType( content.getType() );
        if ( contentType.getSuperType() == null )
        {
            return null;
        }

        if ( contentType.getSuperType().isMedia() )
        {
            Attachment mediaAttachment = command.getAttachment( content.getName().toString() );
            if ( mediaAttachment == null )
            {
                mediaAttachment = command.getAttachments().first();
            }
            if ( mediaAttachment != null )
            {
                return createThumbnailAttachment( mediaAttachment );
            }
        }
        return null;
    }

    private Attachment createThumbnailAttachment( final Attachment attachment )
    {
        final Blob originalImage = context.getClient().execute( Commands.blob().get( attachment.getBlobKey() ) );
        final InputSupplier<ByteArrayInputStream> inputSupplier = ImageThumbnailResolver.resolve( originalImage );
        final Blob thumbnailBlob;
        try
        {
            thumbnailBlob = context.getClient().execute( Commands.blob().create( inputSupplier.getInput() ) );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Failed to create blob for thumbnail attachment: " + e.getMessage() );
        }

        return newAttachment( attachment ).
            blobKey( thumbnailBlob.getKey() ).
            name( CreateContent.THUMBNAIL_NAME ).
            mimeType( THUMBNAIL_MIME_TYPE ).
            size( thumbnailBlob.getLength() ).
            build();
    }

    private ContentType getContentType( final ContentTypeName contentTypeName )
    {
        return context.getClient().execute( Commands.contentType().get().byName().contentTypeName( contentTypeName ) );
    }

    @Inject
    public void setRelationshipService( final RelationshipService relationshipService )
    {
        this.relationshipService = relationshipService;
    }

    @Inject
    public void setIndexService( final IndexService indexService )
    {
        this.indexService = indexService;
    }

}
