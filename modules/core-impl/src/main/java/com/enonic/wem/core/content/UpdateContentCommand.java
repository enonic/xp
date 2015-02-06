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
import com.enonic.wem.api.content.UpdateContentTranslatorParams;
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
    extends AbstractCreatingOrUpdatingContentCommand
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

    public static Builder create( final AbstractCreatingOrUpdatingContentCommand source )
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
            valid( validated ).
            build();
        editedContent = processContent( contentBeforeChange, editedContent );
        editedContent = attachThumbnai( editedContent );

        final UpdateContentTranslatorParams updateContentTranslatorParams = UpdateContentTranslatorParams.create().
            editedContent( editedContent ).
            createAttachments( this.params.getCreateAttachments() ).
            modifier( getCurrentUser().getKey() ).
            build();

        final UpdateNodeParams updateNodeParams = translator.toUpdateNodeParams( updateContentTranslatorParams );
        final Node editedNode = this.nodeService.update( updateNodeParams );

        eventPublisher.publish( new ContentUpdatedEvent( editedContent.getId() ) );

        return translator.fromNode( editedNode );
    }

    private Content processContent( final Content contentBeforeChange, Content editedContent )
    {
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
        return editedContent;
    }

    private Content attachThumbnai( Content editedContent )
    {
        if ( !editedContent.hasThumbnail() )
        {
            final Thumbnail mediaThumbnail = resolveMediaThumbnail( editedContent );
            if ( mediaThumbnail != null )
            {
                editedContent = Content.newContent( editedContent ).thumbnail( mediaThumbnail ).build();
            }
        }
        return editedContent;
    }

    private Content editContent( final ContentEditor editor, final Content original )
    {
        final EditableContent editableContent = new EditableContent( original );
        editor.edit( editableContent );
        return editableContent.build();
    }

    private boolean validateEditedContent( final Content edited )
    {
        final DataValidationErrors dataValidationErrors = ValidateContentDataCommand.create().
            contentData( edited.getData() ).
            contentType( edited.getType() ).
            metadatas( edited.getAllMetadata() ).
            mixinService( this.mixinService ).
            moduleService( this.moduleService ).
            contentTypeService( this.contentTypeService ).
            build().
            execute();

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

        return null;
    }

    private ContentType getContentType( final ContentTypeName contentTypeName )
    {
        return contentTypeService.getByName( new GetContentTypeParams().contentTypeName( contentTypeName ) );
    }

    public static class Builder
        extends AbstractCreatingOrUpdatingContentCommand.Builder<Builder>
    {
        private UpdateContentParams params;

        private MediaInfo mediaInfo;

        Builder( final UpdateContentParams params )
        {
            this.params = params;
        }

        Builder( final AbstractCreatingOrUpdatingContentCommand source )
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
            super.validate();
            Preconditions.checkNotNull( params );
        }

        public UpdateContentCommand build()
        {
            validate();
            return new UpdateContentCommand( this );
        }

    }

}
