package com.enonic.xp.core.impl.content;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.security.DigestInputStream;
import java.time.Instant;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.content.AttachmentValidationError;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentAccessException;
import com.enonic.xp.content.ContentDataValidationException;
import com.enonic.xp.content.ContentEditor;
import com.enonic.xp.content.ContentInheritType;
import com.enonic.xp.content.ContentModifier;
import com.enonic.xp.content.EditableContent;
import com.enonic.xp.content.EditableSite;
import com.enonic.xp.content.Media;
import com.enonic.xp.content.ModifiableContent;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.content.ValidationErrors;
import com.enonic.xp.content.processor.ContentProcessor;
import com.enonic.xp.content.processor.ProcessUpdateParams;
import com.enonic.xp.content.processor.ProcessUpdateResult;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.core.impl.content.validate.InputValidator;
import com.enonic.xp.core.internal.HexCoder;
import com.enonic.xp.core.internal.security.MessageDigests;
import com.enonic.xp.inputtype.InputTypes;
import com.enonic.xp.media.MediaInfo;
import com.enonic.xp.node.ModifyNodeResult;
import com.enonic.xp.node.NodeAccessException;
import com.enonic.xp.node.NodeCommitEntry;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.site.Site;
import com.enonic.xp.util.BinaryReference;

final class UpdateContentCommand
    extends AbstractCreatingOrUpdatingContentCommand
{
    private final UpdateContentParams params;

    private final MediaInfo mediaInfo;

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
        validateCreateAttachments( params.getCreateAttachments() );

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

        Content editedContent;

        editedContent = editContent( params.getEditor(), contentBeforeChange );
        editedContent = processContent( contentBeforeChange, editedContent );

        if ( contentBeforeChange.equals( editedContent ) && contentBeforeChange.getAttachments().equals( editedContent.getAttachments() ) )
        {
            return contentBeforeChange;
        }

        editedContent = editContentMetadata( getContentModifier(), editedContent );

        validate( editedContent );

        if ( isStoppingInheritContent( editedContent.getInherit() ) )
        {
            nodeService.commit( NodeCommitEntry.create().message( "Base inherited version" ).build(),
                                NodeIds.from( NodeId.from( params.getContentId() ) ) );
        }

        final UpdateNodeParams updateNodeParams = UpdateNodeParamsFactory.create()
            .editedContent( editedContent )
            .createAttachments( params.getCreateAttachments() )
            .branches( Branches.from( ContextAccessor.current().getBranch() ) )
            .contentTypeService( this.contentTypeService )
            .xDataService( this.xDataService )
            .pageDescriptorService( this.pageDescriptorService )
            .partDescriptorService( this.partDescriptorService )
            .layoutDescriptorService( this.layoutDescriptorService )
            .contentDataSerializer( this.translator.getContentDataSerializer() )
            .siteService( this.siteService )
            .build()
            .produce();

        final ModifyNodeResult result = this.nodeService.modify( updateNodeParams );

        return translator.fromNode( result.getResult( ContextAccessor.current().getBranch() ), true );

    }

    private Content editContentMetadata( ContentModifier contentModifier, Content content )
    {
        final ModifiableContent modifiableContent = new ModifiableContent( content );

        contentModifier.modify( modifiableContent );

        return modifiableContent.build();
    }

    private ContentModifier getContentModifier()
    {
        return edit -> {
            edit.inherit.setModifier( c -> stopInherit( c.inherit.originalValue ) );

            edit.modifier.setValue( getCurrentUser().getKey() );
            edit.modifiedTime.setValue( Instant.now() );

            edit.attachments.setModifier( c -> mergeExistingAndUpdatedAttachments( c.attachments.originalValue ) );
            edit.validationErrors.setModifier( c -> validateContent( c.source ) );
            edit.valid.setModifier( c -> !c.validationErrors.getProducedValue().hasErrors() );
        };
    }

    private boolean isStoppingInheritContent( final Set<ContentInheritType> currentInherit )
    {
        return params.stopInherit() && currentInherit.contains( ContentInheritType.CONTENT );
    }

    private Set<ContentInheritType> stopInherit( final Set<ContentInheritType> currentInherit )
    {
        if ( params.stopInherit() )
        {
            if ( currentInherit.contains( ContentInheritType.CONTENT ) || currentInherit.contains( ContentInheritType.NAME ) )
            {
                final EnumSet<ContentInheritType> newInherit = EnumSet.copyOf( currentInherit );

                newInherit.remove( ContentInheritType.CONTENT );
                newInherit.remove( ContentInheritType.NAME );

                return newInherit;
            }
        }
        return currentInherit;
    }

    private ValidationErrors validateContent( final Content editedContent )
    {
        final ValidationErrors.Builder validationErrorsBuilder = ValidationErrors.create();

        if ( !params.isClearAttachments() && editedContent.getValidationErrors() != null )
        {
            editedContent.getValidationErrors().stream()
                .filter( validationError -> validationError instanceof AttachmentValidationError )
                .map( validationError -> (AttachmentValidationError) validationError )
                .filter( validationError -> !params.getRemoveAttachments().contains( validationError.getAttachment() ) )
                .forEach( validationErrorsBuilder::add );
        }

        final ValidationErrors validationErrors = ValidateContentDataCommand.create()
            .contentId( editedContent.getId() )
            .data( editedContent.getData() )
            .extraDatas( editedContent.getAllExtraData() )
            .contentTypeName( editedContent.getType() )
            .contentName( editedContent.getName() )
            .displayName( editedContent.getDisplayName() )
            .createAttachments( params.getCreateAttachments() )
            .contentValidators( this.contentValidators )
            .contentTypeService( this.contentTypeService )
            .validationErrorsBuilder( validationErrorsBuilder )
            .build()
            .execute();

        return validationErrors;
    }

    private Attachments mergeExistingAndUpdatedAttachments( final Attachments originalAttachments )
    {
        if ( params.getCreateAttachments().isEmpty() && params.getRemoveAttachments().isEmpty() && !params.isClearAttachments() )
        {
            return originalAttachments;
        }

        final Map<BinaryReference, Attachment> attachments = new LinkedHashMap<>();
        if ( !params.isClearAttachments() )
        {
            originalAttachments.stream().forEach( a -> attachments.put( a.getBinaryReference(), a ) );
            params.getRemoveAttachments().stream().forEach( attachments::remove );
        }

        // added attachments with same BinaryReference will replace existing ones
        for ( final CreateAttachment createAttachment : params.getCreateAttachments() )
        {
            final Attachment.Builder builder = Attachment.create()
                .name( createAttachment.getName() )
                .label( createAttachment.getLabel() )
                .mimeType( createAttachment.getMimeType() )
                .textContent( createAttachment.getTextContent() );
            populateByteSourceProperties( createAttachment.getByteSource(), builder );

            final Attachment attachment = builder.build();
            attachments.put( attachment.getBinaryReference(), attachment );
        }
        return Attachments.from( attachments.values() );
    }

    private static void populateByteSourceProperties( final ByteSource byteSource, Attachment.Builder builder )
    {
        try
        {
            final InputStream inputStream = byteSource.openStream();
            final DigestInputStream digestInputStream = new DigestInputStream( inputStream, MessageDigests.sha512() );
            try (inputStream; digestInputStream)
            {
                final long size = ByteStreams.exhaust( digestInputStream );
                builder.size( size );
            }
            builder.sha512( HexCoder.toHex( digestInputStream.getMessageDigest().digest() ) );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    private Content processContent( final Content originalContent, Content editedContent )
    {
        final ContentType contentType = getContentType( editedContent.getType() );

        for ( final ContentProcessor contentProcessor : this.contentProcessors )
        {
            if ( contentProcessor.supports( contentType ) )
            {
                final ProcessUpdateParams processUpdateParams = ProcessUpdateParams.create()
                    .contentType( contentType )
                    .mediaInfo( mediaInfo )
                    .createAttachments( params.getCreateAttachments() )
                    .originalContent( originalContent )
                    .editedContent( editedContent )
                    .build();
                final ProcessUpdateResult result = contentProcessor.processUpdate( processUpdateParams );

                if ( result != null )
                {
                    editedContent = editContent( result.getEditor(), editedContent );
                }
            }
        }
        return editedContent;
    }

    private Content editContent( final ContentEditor editor, final Content original )
    {
        final EditableContent editableContent = original.isSite() ? new EditableSite( (Site) original ) : new EditableContent( original );
        if ( editor != null )
        {
            editor.edit( editableContent );
        }
        return editableContent.build();
    }

    private void validate( final Content editedContent )
    {
        if ( params.isRequireValid() )
        {
            editedContent.getValidationErrors().stream().findFirst().ifPresent( validationError -> {
                throw new ContentDataValidationException( validationError.getMessage() );
            } );
        }

        validatePropertyTree( editedContent );
        ContentPublishInfoPreconditions.check( editedContent.getPublishInfo() );

        if ( editedContent.getType().isImageMedia() )
        {
            validateImageMediaProperties( editedContent );
        }
    }

    private void validateImageMediaProperties( final Content editedContent )
    {
        if ( !( editedContent instanceof final Media mediaContent ) )
        {
            return;
        }

        try
        {
            // validate focal point values
            mediaContent.getFocalPoint();
        }
        catch ( IllegalArgumentException e )
        {
            throw new IllegalArgumentException( "Invalid property for content: " + e.getMessage(), e );
        }
    }

    private void validatePropertyTree( final Content editedContent )
    {
        final ContentType contentType = getContentType( editedContent.getType() );

        try
        {
            InputValidator.create()
                .form( contentType.getForm() )
                .inputTypeResolver( InputTypes.BUILTIN )
                .build()
                .validate( editedContent.getData() );
        }
        catch ( final Exception e )
        {
            throw new IllegalArgumentException( "Invalid property for content: " + editedContent.getPath(), e );
        }
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

        UpdateContentCommand build()
        {
            validate();
            return new UpdateContentCommand( this );
        }

    }

}
