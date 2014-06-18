package com.enonic.wem.core.content;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.io.InputSupplier;

import com.enonic.wem.api.blob.Blob;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentDataValidationException;
import com.enonic.wem.api.content.UpdateContentParams;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.content.attachment.AttachmentService;
import com.enonic.wem.api.content.attachment.Attachments;
import com.enonic.wem.api.content.thumb.Thumbnail;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.UpdateNodeParams;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.GetContentTypeParams;
import com.enonic.wem.api.schema.content.validator.DataValidationError;
import com.enonic.wem.api.schema.content.validator.DataValidationErrors;

import static com.enonic.wem.api.content.Content.newContent;
import static com.enonic.wem.api.content.ContentConstants.DEFAULT_CONTEXT;

final class UpdateContentCommand
    extends AbstractContentCommand
{
    private static final String THUMBNAIL_MIME_TYPE = "image/png";

    private final static Logger LOG = LoggerFactory.getLogger( UpdateContentCommand.class );

    private final AttachmentService attachmentService;

    private final UpdateContentParams params;

    private UpdateContentCommand( final Builder builder )
    {
        super( builder );
        this.attachmentService = builder.attachmentService;
        this.params = builder.params;
    }

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

        final Thumbnail mediaThumbnail = resolveMediaThumbnail( editedContent );
        if ( mediaThumbnail != null )
        {
            editedContent = newContent( editedContent ).thumbnail( mediaThumbnail ).build();
        }

        final Attachments attachments;

        if ( this.params.getUpdateAttachments() != null )
        {
            attachments = this.params.getUpdateAttachments().getAttachments();
        }
        else
        {
            attachments = attachmentService.getAll( this.params.getContentId() );
        }

        final UpdateNodeParams updateNodeParams = translator.toUpdateNodeCommand( editedContent, attachments );

        final Node editedNode = this.nodeService.update( updateNodeParams, DEFAULT_CONTEXT );

        return translator.fromNode( editedNode );
    }

    private void validateEditedContent( final Content persistedContent, final Content edited )
    {
        persistedContent.checkIllegalEdit( edited );

        if ( !edited.isDraft() )
        {
            validateContentData( edited );
        }
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

    private Thumbnail resolveMediaThumbnail( final Content content )
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

    public static Builder create( final UpdateContentParams params )
    {
        return new Builder( params );
    }

    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private AttachmentService attachmentService;

        private final UpdateContentParams params;

        public Builder( final UpdateContentParams params )
        {
            this.params = params;
        }

        public Builder attachmentService( final AttachmentService attachmentService )
        {
            this.attachmentService = attachmentService;
            return this;
        }

        void validate()
        {
            Preconditions.checkNotNull( attachmentService );
            Preconditions.checkNotNull( params );
        }

        public UpdateContentCommand build()
        {
            validate();
            return new UpdateContentCommand( this );
        }

    }

}
