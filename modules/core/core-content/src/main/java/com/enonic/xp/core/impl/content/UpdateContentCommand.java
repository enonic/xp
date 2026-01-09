package com.enonic.xp.core.impl.content;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.content.AttachmentValidationError;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentDataValidationException;
import com.enonic.xp.content.ContentEditor;
import com.enonic.xp.content.ContentInheritType;
import com.enonic.xp.content.EditableContent;
import com.enonic.xp.content.Media;
import com.enonic.xp.content.PatchableContent;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.content.ValidationErrors;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.core.impl.content.processor.ContentProcessor;
import com.enonic.xp.core.impl.content.processor.ProcessUpdateParams;
import com.enonic.xp.core.impl.content.processor.ProcessUpdateResult;
import com.enonic.xp.media.MediaInfo;
import com.enonic.xp.node.NodeAccessException;
import com.enonic.xp.node.NodeCommitEntry;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
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
            throw ContentNodeHelper.toContentAccessException( e );
        }
    }

    private Content doExecute()
    {
        final Content contentBeforeChange = getContent( params.getContentId() );

        Content editedContent;

        editedContent = editContent( params.getEditor(), contentBeforeChange );
        editedContent = processContent( editedContent );

        final List<String> modifiedFields = ContentAttributesHelper.modifiedFields( contentBeforeChange, editedContent );

        editedContent =
            editContentMetadata( editedContent, modifiedFields.stream().anyMatch( ContentAttributesHelper.EDITORIAL_FIELDS::contains ) );

        if ( isContentTheSame().test( contentBeforeChange, editedContent ) )
        {
            return contentBeforeChange;
        }

        checkAccess( contentBeforeChange, editedContent );
        validate( editedContent );

        if ( contentBeforeChange.getInherit().contains( ContentInheritType.CONTENT ) )
        {
            nodeService.commit( NodeCommitEntry.create().message( "Base inherited version" ).build(),
                                NodeIds.from( NodeId.from( params.getContentId() ) ) );
        }

        final PatchNodeParams patchNodeParams = PatchNodeParamsFactory.create()
            .editedContent( editedContent )
            .createAttachments( params.getCreateAttachments() )
            .versionAttributes( ContentAttributesHelper.updateVersionHistoryAttr( modifiedFields ) )
            .branches( Branches.from( ContextAccessor.current().getBranch() ) )
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

    private Content editContentMetadata( Content content, final boolean editModifier )
    {
        final PatchableContent patchableContent = new PatchableContent( content );
        patchableContent.inherit.setPatcher( c -> stopDataInherit( c.inherit.originalValue ) );
        patchableContent.attachments.setPatcher( c -> mergeExistingAndUpdatedAttachments( c.attachments.originalValue ) );
        if ( editModifier )
        {
            patchableContent.modifier.setValue( getCurrentUserKey() );
            patchableContent.modifiedTime.setValue( Instant.now() );
        }
        patchableContent.validationErrors.setPatcher( c -> validateContent( c.source ) );
        patchableContent.valid.setPatcher( c -> !c.validationErrors.getProducedValue().hasErrors() );
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
            checkAdminAccess();
        }
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
            Objects.requireNonNull( params, "params cannot be null" );
        }

        UpdateContentCommand build()
        {
            validate();
            return new UpdateContentCommand( this );
        }

    }

}
