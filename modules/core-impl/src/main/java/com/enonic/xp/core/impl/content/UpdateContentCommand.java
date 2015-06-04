package com.enonic.xp.core.impl.content;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentAccessException;
import com.enonic.xp.content.ContentChangeEvent;
import com.enonic.xp.content.ContentDataValidationException;
import com.enonic.xp.content.ContentEditor;
import com.enonic.xp.content.ContentUpdatedEvent;
import com.enonic.xp.content.EditableContent;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.content.UpdateContentTranslatorParams;
import com.enonic.xp.form.InvalidDataException;
import com.enonic.xp.icon.Thumbnail;
import com.enonic.xp.media.MediaInfo;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAccessException;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.schema.content.validator.DataValidationError;
import com.enonic.xp.schema.content.validator.DataValidationErrors;
import com.enonic.xp.schema.content.validator.InputValidator;

final class UpdateContentCommand
    extends AbstractCreatingOrUpdatingContentCommand
{
    private final static Logger LOG = LoggerFactory.getLogger( UpdateContentCommand.class );

    private final UpdateContentParams params;

    private final MediaInfo mediaInfo;

    //private final ProxyContentProcessor proxyProcessor;

    private UpdateContentCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
        this.mediaInfo = builder.mediaInfo;

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
        try
        {
            return doExecute();
        }
        catch ( NodeAccessException e )
        {
            throw new ContentAccessException( e );
        }
    }

    private Content doExecute()
    {
        final Content contentBeforeChange = getContent( params.getContentId() );

        Content editedContent = editContent( params.getEditor(), contentBeforeChange );

        if ( contentBeforeChange.equals( editedContent ) && params.getCreateAttachments() == null )
        {
            return contentBeforeChange;
        }

        validateBlockingChecks( editedContent );
        final boolean validated = validateNonBlockingChecks( editedContent );

        editedContent = Content.newContent( editedContent ).
            valid( validated ).
            build();
        editedContent = processContent( contentBeforeChange, editedContent );
        editedContent = attachThumbnail( editedContent );
        editedContent = setModifiedTime( editedContent );

        final UpdateContentTranslatorParams updateContentTranslatorParams = UpdateContentTranslatorParams.create().
            editedContent( editedContent ).
            createAttachments( this.params.getCreateAttachments() ).
            modifier( getCurrentUser().getKey() ).
            build();

        final UpdateNodeParams updateNodeParams = translator.toUpdateNodeParams( updateContentTranslatorParams );
        final Node editedNode = this.nodeService.update( updateNodeParams );

        eventPublisher.publish( new ContentUpdatedEvent( editedContent.getId() ) );

        eventPublisher.publish( ContentChangeEvent.from( ContentChangeEvent.ContentChangeType.UPDATE, editedContent.getPath() ) );

        return translator.fromNode( editedNode );
    }

    private Content processContent( final Content contentBeforeChange, Content editedContent )
    {
        final ContentType contentType = this.contentTypeService.getByName( GetContentTypeParams.from( editedContent.getType() ) );

        final ProxyContentProcessor proxyContentProcessor = ProxyContentProcessor.create().
            mediaInfo( this.mediaInfo ).
            mixinService( this.mixinService ).
            contentType( contentType ).
            build();

        final ProcessUpdateResult processUpdateResult =
            proxyContentProcessor.processEdit( contentBeforeChange.getType(), params, params.getCreateAttachments() );
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

    private Content attachThumbnail( final Content editedContent )
    {
        if ( !editedContent.hasThumbnail() )
        {
            final Thumbnail mediaThumbnail = resolveMediaThumbnail( editedContent );
            if ( mediaThumbnail != null )
            {
                return Content.newContent( editedContent ).thumbnail( mediaThumbnail ).build();
            }
        }
        return editedContent;
    }

    private Content editContent( final ContentEditor editor, final Content original )
    {
        final EditableContent editableContent = new EditableContent( original );
        if ( editor != null )
        {
            editor.edit( editableContent );
        }
        return editableContent.build();
    }

    private Content setModifiedTime( final Content content )
    {
        return Content.newContent( content ).
            modifiedTime( Instant.now() ).
            build();
    }

    private void validateBlockingChecks( final Content editedContent )
    {
        validatePropertyTree( editedContent );
    }

    private void validatePropertyTree( final Content editedContent )
    {
        final ContentType contentType =
            contentTypeService.getByName( new GetContentTypeParams().contentTypeName( editedContent.getType() ) );
        try
        {
            InputValidator.
                create().
                contentType( contentType ).
                build().
                validate( editedContent.getData() );
        }
        catch ( InvalidDataException e )
        {
            throw new IllegalArgumentException( "Incorrect property for content: " + editedContent.getPath(), e );
        }
    }

    private boolean validateNonBlockingChecks( final Content edited )
    {
        final DataValidationErrors dataValidationErrors = ValidateContentDataCommand.create().
            contentData( edited.getData() ).
            contentType( edited.getType() ).
            extradatas( edited.getAllExtraData() ).
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

        @Override
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
