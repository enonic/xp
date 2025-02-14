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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import com.enonic.xp.content.EditableContent;
import com.enonic.xp.content.EditableSite;
import com.enonic.xp.content.Media;
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
    private static final Logger LOG = LoggerFactory.getLogger( UpdateContentCommand.class );

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

        Content editedContent = editContent( params.getEditor(), contentBeforeChange );

        boolean commitBeforeChange = false;

        if ( params.stopInherit() )
        {
            final Set<ContentInheritType> currentInherit = editedContent.getInherit();

            if ( currentInherit.contains( ContentInheritType.CONTENT ) || currentInherit.contains( ContentInheritType.NAME ) )
            {
                final EnumSet<ContentInheritType> newInherit = EnumSet.copyOf( currentInherit );

                if ( currentInherit.contains( ContentInheritType.CONTENT ) )
                {
                    commitBeforeChange = true;
                    newInherit.remove( ContentInheritType.CONTENT );
                }

                newInherit.remove( ContentInheritType.NAME );
                editedContent = Content.create( editedContent ).setInherit( newInherit ).build();
            }
        }

        editedContent = processContent( contentBeforeChange, editedContent );

        final Attachments attachmentsBeforeChange = contentBeforeChange.getAttachments();
        final Attachments attachments = mergeExistingAndUpdatedAttachments( attachmentsBeforeChange );

        if ( contentBeforeChange.equals( editedContent ) && attachmentsBeforeChange.equals( attachments ) )
        {
            return contentBeforeChange;
        }

        validateBlockingChecks( editedContent );

        final ValidationErrors.Builder validationErrorsBuilder = ValidationErrors.create();

        if ( !params.isClearAttachments() && contentBeforeChange.getValidationErrors() != null )
        {
            contentBeforeChange.getValidationErrors()
                .stream()
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
            .contentTypeService( this.contentTypeService ).validationErrorsBuilder( validationErrorsBuilder ).build().execute();

        if ( params.isRequireValid() )
        {
            validationErrors.stream().findFirst().ifPresent( validationError -> {
                throw new ContentDataValidationException( validationError.getMessage() );
            } );
        }

        if ( commitBeforeChange )
        {
            nodeService.commit( NodeCommitEntry.create().message( "Base inherited version" ).build(),
                                NodeIds.from( NodeId.from( params.getContentId() ) ) );
        }

        editedContent = Content.create( editedContent )
            .valid( !validationErrors.hasErrors() )
            .validationErrors( validationErrors )
            .modifiedTime( Instant.now() ).attachments( attachments ).modifier( getCurrentUser().getKey() )
            .build();

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

    private void validateBlockingChecks( final Content editedContent )
    {
        validatePropertyTree( editedContent );
        ContentPublishInfoPreconditions.check( editedContent.getPublishInfo() );

        if ( editedContent.getType().isImageMedia() )
        {
            validateImageMediaProperties( editedContent );
        }
    }

    private void validateImageMediaProperties( final Content editedContent )
    {
        if ( !( editedContent instanceof Media ) )
        {
            return;
        }
        final Media mediaContent = (Media) editedContent;

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

        public UpdateContentCommand build()
        {
            validate();
            return new UpdateContentCommand( this );
        }

    }

}
