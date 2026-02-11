package com.enonic.xp.core.impl.content;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.content.AttachmentValidationError;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentDataValidationException;
import com.enonic.xp.content.ContentEditor;
import com.enonic.xp.content.EditableContent;
import com.enonic.xp.content.Media;
import com.enonic.xp.content.PatchableContent;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.content.ValidationErrors;
import com.enonic.xp.content.WorkflowInfo;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.core.impl.content.processor.ContentProcessor;
import com.enonic.xp.core.impl.content.processor.ProcessUpdateParams;
import com.enonic.xp.core.impl.content.processor.ProcessUpdateResult;
import com.enonic.xp.media.MediaInfo;
import com.enonic.xp.node.NodeAccessException;
import com.enonic.xp.node.PatchNodeParams;
import com.enonic.xp.node.PatchNodeResult;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfigsDataSerializer;
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
            throw ContentNodeHelper.toContentAccessException( e );
        }
    }

    private Content doExecute()
    {
        final PatchNodeParams patchNodeParams = PatchNodeParamsFactory.create()
            .contentId( params.getContentId() )
            .editor( content -> {
                Content editedContent;

                editedContent = editContent( params.getEditor(), content );
                editedContent = mergeExistingAndUpdatedAttachments( editedContent );
                editedContent = processContent( editedContent );

                if ( isContentTheSame( content, editedContent ) )
                {
                    return content;
                }

                checkAccess( content, editedContent );

                editedContent = editContentMetadata( editedContent );
                editedContent = afterUpdate( editedContent );

                validate( editedContent );

                return editedContent;
            } )
            .createAttachments( params.getCreateAttachments() )
            .versionAttributes( ContentAttributesHelper.versionHistoryAttr( ContentAttributesHelper.UPDATE_ATTR ) )
            .contentTypeService( this.contentTypeService )
            .xDataService( this.xDataService )
            .pageDescriptorService( this.pageDescriptorService )
            .partDescriptorService( this.partDescriptorService )
            .layoutDescriptorService( this.layoutDescriptorService )
            .siteService( this.siteService )
            .build()
            .produce();

        final PatchNodeResult result = this.nodeService.patch( patchNodeParams );

        return ContentNodeTranslator.fromNode( result.getResult( ContextAccessor.current().getBranch() ) );
    }

    private Content editContentMetadata( Content content )
    {
        final PatchableContent patchableContent = new PatchableContent( content );
        patchableContent.workflowInfo.setValue( WorkflowInfo.inProgress() );
        patchableContent.validationErrors.setPatcher( c -> validateContent( c.source ) );
        patchableContent.valid.setPatcher( c -> !c.validationErrors.getProducedValue().hasErrors() );
        patchableContent.modifier.setValue( getCurrentUserKey() );
        patchableContent.modifiedTime.setValue( Instant.now() );

        return patchableContent.build();
    }

    private ValidationErrors validateContent( final Content editedContent )
    {
        final ValidationErrors.Builder validationErrorsBuilder = ValidationErrors.create();

        if ( !params.isClearAttachments() && editedContent.getValidationErrors() != null &&
            editedContent.getValidationErrors().hasErrors() )
        {
            editedContent.getValidationErrors()
                .stream()
                .filter( validationError -> validationError instanceof AttachmentValidationError )
                .map( validationError -> (AttachmentValidationError) validationError )
                .filter( validationError -> !params.getRemoveAttachments().contains( validationError.getAttachment() ) )
                .forEach( validationErrorsBuilder::add );
        }

        return ValidateContentDataCommand.create()
            .contentId( editedContent.getId() )
            .data( editedContent.getData() )
            .extraDatas( editedContent.getAllExtraData() )
            .contentTypeName( editedContent.getType() )
            .contentName( editedContent.getName() )
            .displayName( editedContent.getDisplayName() )
            .createAttachments( params.getCreateAttachments() )
            .page( editedContent.getPage() )
            .contentValidators( this.contentValidators )
            .contentTypeService( this.contentTypeService )
            .validationErrorsBuilder( validationErrorsBuilder )
            .build()
            .execute();
    }

    private Content mergeExistingAndUpdatedAttachments( final Content content )
    {
        if ( params.getCreateAttachments().isEmpty() && params.getRemoveAttachments().isEmpty() && !params.isClearAttachments() )
        {
            return content;
        }

        final Attachments originalAttachments = content.getAttachments();

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
        return Content.create( content ).attachments( Attachments.from( attachments.values() ) ).build();
    }

    private Content processContent( Content content )
    {
        for ( final ContentProcessor contentProcessor : this.contentProcessors )
        {
            if ( contentProcessor.supports( content.getType() ) )
            {
                final ProcessUpdateParams processUpdateParams =
                    ProcessUpdateParams.create().mediaInfo( mediaInfo ).content( content ).build();
                final ProcessUpdateResult result = contentProcessor.processUpdate( processUpdateParams );
                content = result.getContent();
            }
        }
        return content;
    }

    private Content editContent( final ContentEditor editor, final Content original )
    {
        final EditableContent editableContent = new EditableContent( original );
        if ( editor != null )
        {
            editor.edit( editableContent );
        }

        editableContent.extraDatas = mergeExtraData( original.getType(), editableContent.data,
                                                     original.getPath().isRoot() ? original.getPath() : original.getParentPath(),
                                                     editableContent.extraDatas );

        return Content.create( editableContent.build() ).build();
    }

    private void validate( final Content editedContent )
    {
        if ( params.isRequireValid() )
        {
            editedContent.getValidationErrors().stream().findFirst().ifPresent( validationError -> {
                throw new ContentDataValidationException( validationError.getMessage() );
            } );
        }

        validateContentData( editedContent.getType(), editedContent.getData() );
        validateMixins( editedContent.getAllExtraData() );
        validatePage( editedContent.getPage() );
        validateSiteConfigs( editedContent.getData() );

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

    private void checkAccess( final Content contentBeforeChange, final Content editedContent )
    {
        if ( contentBeforeChange instanceof Site originalSite && editedContent instanceof Site editedSite &&
            !Objects.equals( SiteConfigsDataSerializer.fromData( originalSite.getData().getRoot() ),
                             SiteConfigsDataSerializer.fromData( editedSite.getData().getRoot() ) ) )
        {
            checkOwnerAccess();
        }
    }

    public static class Builder
        extends AbstractCreatingOrUpdatingContentCommand.Builder<Builder>
    {
        private final UpdateContentParams params;

        private MediaInfo mediaInfo;

        Builder( final UpdateContentParams params )
        {
            this.params = params;
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
            Objects.requireNonNull( params, "params cannot be null" );
        }

        UpdateContentCommand build()
        {
            validate();
            return new UpdateContentCommand( this );
        }

    }

}
