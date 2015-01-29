package com.enonic.wem.core.content;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentDataValidationException;
import com.enonic.wem.api.content.ContentEditor;
import com.enonic.wem.api.content.ContentUpdatedEvent;
import com.enonic.wem.api.content.EditableContent;
import com.enonic.wem.api.content.UpdateContentParams;
import com.enonic.wem.api.media.MediaInfo;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.UpdateNodeParams;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.GetContentTypeParams;
import com.enonic.wem.api.schema.content.validator.DataValidationError;
import com.enonic.wem.api.schema.content.validator.DataValidationErrors;
import com.enonic.wem.api.thumb.Thumbnail;

final class UpdateContentCommand
    extends AbstractContentCommand
{
    private final static Logger LOG = LoggerFactory.getLogger( UpdateContentCommand.class );

    private final UpdateContentParams params;

    private final ProxyContentProcessor proxyProcessor;

    private UpdateContentCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
        this.proxyProcessor = new ProxyContentProcessor( builder.mediaInfo );
    }

    public static Builder create( final UpdateContentParams params )
    {
        return new Builder( params );
    }

    public static Builder create( final AbstractContentCommand source )
    {
        return new Builder( source );
    }

    Content execute()
    {
        params.validate();

        return doExecute();
    }

    private Content doExecute()
    {
        final Content contentBeforeChange = getContent( params.getContentId() );

        Content editedContent = editContent( params.getEditor(), contentBeforeChange );

        if ( contentBeforeChange.equals( editedContent ) )
        {
            return contentBeforeChange;
        }

        final boolean validated = validateEditedContent( editedContent );

        editedContent = Content.newContent( editedContent ).
            modifier( this.params.getModifier() ).
            validated( validated ).
            build();

        final ProcessUpdateResult processUpdateResult =
            proxyProcessor.processEdit( contentBeforeChange.getType(), params, params.getCreateAttachments() );
        if ( processUpdateResult != null )
        {
            if ( processUpdateResult.editor != null )
            {
                editedContent = editContent( processUpdateResult.editor, editedContent );
            }
            this.params.createAttachments( processUpdateResult.createAttachments );
        }

        if ( !editedContent.hasThumbnail() )
        {
            final Thumbnail mediaThumbnail = resolveMediaThumbnail( editedContent );
            if ( mediaThumbnail != null )
            {
                editedContent = Content.newContent( editedContent ).thumbnail( mediaThumbnail ).build();
            }
        }

        final UpdateNodeParams updateNodeParams = translator.toUpdateNodeCommand( editedContent, this.params.getCreateAttachments() );
        final Node editedNode = this.nodeService.update( updateNodeParams );

        eventPublisher.publish( new ContentUpdatedEvent( editedContent.getId() ) );

        return translator.fromNode( editedNode );
    }

    private Content editContent( final ContentEditor editor, final Content original )
    {
        final EditableContent editableContent = new EditableContent( original );
        editor.edit( editableContent );
        return editableContent.build();
    }

    private boolean validateEditedContent( final Content edited )
    {
        final DataValidationErrors dataValidationErrors = validate( edited.getType(), edited.getData() );

        for ( DataValidationError error : dataValidationErrors )
        {
            LOG.info( "*** DataValidationError: " + error.getErrorMessage() );
        }

        if ( dataValidationErrors.hasErrors() )
        {
            if ( this.params.isRequireValid() )
            {
                throw new ContentDataValidationException( dataValidationErrors.getFirst().getErrorMessage() );
            }
            else
            {
                return false;
            }
        }

        return true;
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
            /*Attachment mediaAttachment = this.params.byName( content.getName().toString() );

            if ( mediaAttachment == null )
            {
                mediaAttachment = this.params.getCreateAttachments().getAttachments().first();
            }
            if ( mediaAttachment != null )
            {
                return createThumbnail( mediaAttachment );
            }*/
        }
        return null;
    }

    private ContentType getContentType( final ContentTypeName contentTypeName )
    {
        return contentTypeService.getByName( new GetContentTypeParams().contentTypeName( contentTypeName ) );
    }

    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private UpdateContentParams params;

        private MediaInfo mediaInfo;

        Builder( final UpdateContentParams params )
        {
            this.params = params;
        }

        Builder( final AbstractContentCommand source )
        {
            super( source );

        }

        Builder params( final UpdateContentParams value )
        {
            this.params = value;
            return this;
        }

        Builder mediaInfo( final MediaInfo value )
        {
            this.mediaInfo = value;
            return this;
        }

        void validate()
        {
            Preconditions.checkNotNull( params );
        }

        public UpdateContentCommand build()
        {
            validate();
            return new UpdateContentCommand( this );
        }

    }

}
