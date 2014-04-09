package com.enonic.wem.core.content;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.InputSupplier;

import com.enonic.wem.api.blob.Blob;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentDataValidationException;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.UpdateContentParams;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.content.attachment.AttachmentService;
import com.enonic.wem.api.content.attachment.Attachments;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.thumb.Thumbnail;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.PropertyVisitor;
import com.enonic.wem.api.data.type.ValueTypes;
import com.enonic.wem.api.entity.DeleteNodeByPathParams;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.UpdateNodeParams;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.GetContentTypeParams;
import com.enonic.wem.api.schema.content.validator.DataValidationError;
import com.enonic.wem.api.schema.content.validator.DataValidationErrors;

import static com.enonic.wem.api.content.Content.newContent;

final class UpdateContentCommand
    extends AbstractContentCommand<UpdateContentCommand>
{
    private static final String THUMBNAIL_MIME_TYPE = "image/png";

    private final static Logger LOG = LoggerFactory.getLogger( UpdateContentCommand.class );

    private AttachmentService attachmentService;

    private UpdateContentParams params;

    Content execute()
    {
        params.validate();

        return doExecute();
    }

    private Content doExecute()
    {
        final Content contentBeforeChange = getContent( params.getContentId() );
        final Content.EditBuilder editBuilder = this.params.getEditor().edit( contentBeforeChange );

        if ( !editBuilder.isChanges() && this.params.getUpdateAttachments() == null )
        {
            return contentBeforeChange;
        }

        Content editedContent = editBuilder.build();

        validateEditedContent( contentBeforeChange, editedContent );

        editedContent = newContent( editedContent ).modifier( this.params.getModifier() ).build();

        final Thumbnail thumbnail = resolveThumbnail( editedContent );
        editedContent = newContent( editedContent ).thumbnail( thumbnail ).build();

        final Attachments attachments;

        if ( this.params.getUpdateAttachments() != null )
        {
            attachments = this.params.getUpdateAttachments().getAttachments();
        }
        else
        {
            attachments = attachmentService.getAll( this.params.getContentId() );
        }

        final UpdateNodeParams updateNodeParams = getTranslator().toUpdateNodeCommand( editedContent, attachments );

        final Node editedNode = this.nodeService.update( updateNodeParams ).getPersistedNode();

        final Content persistedContent = getTranslator().fromNode( editedNode );

        deleteRemovedEmbeddedContent( contentBeforeChange, persistedContent );

        return persistedContent;
    }

    private void deleteRemovedEmbeddedContent( final Content persistedContent, final Content editedContent )
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
                this.nodeService.deleteByPath( params );
            }
        }
    }

    private void validateEditedContent( final Content persistedContent, final Content edited )
    {
        persistedContent.checkIllegalEdit( edited );

        if ( !edited.isDraft() )
        {
            validateContentData( edited );
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
                final Content content = getContent( property.getContentId() );

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

    private void validateContentData( final Content modifiedContent )
    {
        final DataValidationErrors dataValidationErrors = validate( modifiedContent.getType(), modifiedContent.getContentData() );

        for ( DataValidationError error : dataValidationErrors )
        {
            LOG.info( "*** DataValidationError: " + error.getErrorMessage() );
        }
        if ( dataValidationErrors.hasErrors() )
        {
            throw new ContentDataValidationException( dataValidationErrors.getFirst().getErrorMessage() );
        }
    }

    private Thumbnail resolveThumbnail( final Content content )
    {
        final ContentType contentType = getContentType( content.getType() );

        if ( contentType.getSuperType() == null )
        {
            return null;
        }

        if ( contentType.getSuperType().isMedia() )
        {
            Attachment mediaAttachment = this.params.getAttachment( content.getName().toString() );

            if ( mediaAttachment == null )
            {
                mediaAttachment = this.params.getUpdateAttachments().getAttachments().first();
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
        final Blob originalImage = blobService.get( attachment.getBlobKey() );
        final InputSupplier<ByteArrayInputStream> inputSupplier = ThumbnailFactory.resolve( originalImage );
        final Blob thumbnailBlob;
        try
        {
            thumbnailBlob = blobService.create( inputSupplier.getInput() );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Failed to create blob for thumbnail attachment: " + e.getMessage() );
        }

        return Thumbnail.from( thumbnailBlob.getKey(), THUMBNAIL_MIME_TYPE, thumbnailBlob.getLength() );
    }

    private ContentType getContentType( final ContentTypeName contentTypeName )
    {
        return contentTypeService.getByName( new GetContentTypeParams().contentTypeName( contentTypeName ) );
    }

    UpdateContentCommand attachmentService( final AttachmentService attachmentService )
    {
        this.attachmentService = attachmentService;
        return this;
    }

    UpdateContentCommand params( final UpdateContentParams params )
    {
        this.params = params;
        return this;
    }
}
