package com.enonic.wem.core.content;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.InputSupplier;

import com.enonic.wem.api.blob.Blob;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.GetContentById;
import com.enonic.wem.api.command.content.UpdateContent;
import com.enonic.wem.api.command.content.ValidateContentData;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentDataValidationException;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.content.attachment.Attachments;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.thumb.Thumbnail;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.PropertyVisitor;
import com.enonic.wem.api.data.type.ValueTypes;
import com.enonic.wem.api.entity.DeleteNodeByPathParams;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.NodeService;
import com.enonic.wem.api.entity.UpdateNodeParams;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.validator.DataValidationError;
import com.enonic.wem.api.schema.content.validator.DataValidationErrors;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.index.IndexService;

import static com.enonic.wem.api.content.Content.newContent;

public class UpdateContentHandler
    extends CommandHandler<UpdateContent>
{
    private static final String THUMBNAIL_MIME_TYPE = "image/png";

    private IndexService indexService;

    private NodeService nodeService;

    private final static Logger LOG = LoggerFactory.getLogger( UpdateContentHandler.class );

    @Override
    public void handle()
        throws Exception
    {
        final ContentNodeTranslator translator = new ContentNodeTranslator( this.context.getClient() );

        final GetContentById getContentByIdCommand = new GetContentById( command.getContentId() );
        final Content contentBeforeChange = new GetContentByIdService( this.context, getContentByIdCommand, nodeService ).execute();

        final Content.EditBuilder editBuilder = command.getEditor().edit( contentBeforeChange );

        if ( !editBuilder.isChanges() && command.getUpdateAttachments() == null )
        {
            command.setResult( contentBeforeChange );
            return;
        }

        Content editedContent = editBuilder.build();

        validateEditedContent( contentBeforeChange, editedContent );

        editedContent = newContent( editedContent ).
            modifier( command.getModifier() ).build();

        final Thumbnail thumbnail = resoveThumbnail( command, editedContent );
        editedContent = newContent( editedContent ).thumbnail( thumbnail ).build();

        final Attachments attachments;

        if ( command.getUpdateAttachments() != null )
        {
            attachments = command.getUpdateAttachments().getAttachments();
        }
        else
        {
            attachments = getCurrentAttachments( command.getContentId() );
        }

        final UpdateNodeParams updateNodeParams = translator.toUpdateNodeCommand( editedContent, attachments );

        final Node editedNode = nodeService.update( updateNodeParams ).getPersistedNode();

        final Content persistedContent = translator.fromNode( editedNode );

        deleteRemovedEmbeddedContent( contentBeforeChange, persistedContent );

        command.setResult( persistedContent );
    }

    private Attachments getCurrentAttachments( final ContentId contentId )
    {
        return this.context.getClient().execute( Commands.attachment().getAll().contentId( contentId ) );
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
                final NodePath nodePath = ContentNodeHelper.translateContentPathToNodePath( embeddedContentBeforeEdit.getPath() );

                final DeleteNodeByPathParams params = new DeleteNodeByPathParams( nodePath );
                nodeService.deleteByPath( params );
            }
        }
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
                final Content content =
                    new GetContentByIdService( context, new GetContentById( property.getContentId() ), nodeService ).execute();

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

    private Thumbnail resoveThumbnail( final UpdateContent command, final Content content )
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
                mediaAttachment = command.getUpdateAttachments().getAttachments().first();
            }
            if ( mediaAttachment != null )
            {
                return createThumbnail( mediaAttachment );
            }
        }
        return null;
    }

    private Thumbnail createThumbnail( final Attachment attachment )
    {
        final Blob originalImage = context.getClient().execute( Commands.blob().get( attachment.getBlobKey() ) );
        final InputSupplier<ByteArrayInputStream> inputSupplier = ThumbnailFactory.resolve( originalImage );
        final Blob thumbnailBlob;
        try
        {
            thumbnailBlob = context.getClient().execute( Commands.blob().create( inputSupplier.getInput() ) );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Failed to create blob for thumbnail attachment: " + e.getMessage() );
        }

        return Thumbnail.from( thumbnailBlob.getKey(), THUMBNAIL_MIME_TYPE, thumbnailBlob.getLength() );
    }

    private ContentType getContentType( final ContentTypeName contentTypeName )
    {
        return context.getClient().execute( Commands.contentType().get().byName().contentTypeName( contentTypeName ) );
    }

    @Inject
    public void setIndexService( final IndexService indexService )
    {
        this.indexService = indexService;
    }

    @Inject
    public void setNodeService( final NodeService nodeService )
    {
        this.nodeService = nodeService;
    }
}
