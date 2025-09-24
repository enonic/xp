package com.enonic.xp.core.impl.content;

import java.time.Instant;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiPredicate;

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
import com.enonic.xp.content.Media;
import com.enonic.xp.content.PatchableContent;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.content.ValidationErrors;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.core.impl.content.processor.ContentProcessor;
import com.enonic.xp.core.impl.content.processor.ProcessUpdateParams;
import com.enonic.xp.core.impl.content.processor.ProcessUpdateResult;
import com.enonic.xp.core.impl.content.validate.InputValidator;
import com.enonic.xp.exception.ForbiddenAccessException;
import com.enonic.xp.inputtype.InputTypes;
import com.enonic.xp.media.MediaInfo;
import com.enonic.xp.node.NodeAccessException;
import com.enonic.xp.node.NodeCommitEntry;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.PatchNodeParams;
import com.enonic.xp.node.PatchNodeResult;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectRole;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.security.auth.AuthenticationInfo;
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
        editedContent = processContent( editedContent );
        editedContent = editContentMetadata( editedContent );

        if ( isContentTheSame().test( contentBeforeChange, editedContent ) )
        {
            return contentBeforeChange;
        }

        validateProjectAccess( contentBeforeChange, editedContent );

        validate( editedContent );

        if ( isStoppingInheritContent( contentBeforeChange.getInherit() ) )
        {
            nodeService.commit( NodeCommitEntry.create().message( "Base inherited version" ).build(),
                                NodeIds.from( NodeId.from( params.getContentId() ) ) );
        }

        final PatchNodeParams patchNodeParams = PatchNodeParamsFactory.create()
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

        final PatchNodeResult result = this.nodeService.patch( patchNodeParams );

        return translator.fromNode( result.getResult( ContextAccessor.current().getBranch() ), true );
    }

    private Content editContentMetadata( Content content )
    {
        final PatchableContent patchableContent = new PatchableContent( content );
        patchableContent.inherit.setPatcher( c -> stopInherit( c.inherit.originalValue ) );
        patchableContent.attachments.setPatcher( c -> mergeExistingAndUpdatedAttachments( c.attachments.originalValue ) );
        patchableContent.modifier.setValue( getCurrentUser().getKey() );
        patchableContent.modifiedTime.setValue( Instant.now() );
        patchableContent.validationErrors.setPatcher( c -> validateContent( c.source ) );
        patchableContent.valid.setPatcher( c -> !c.validationErrors.getProducedValue().hasErrors() );
        return patchableContent.build();
    }

    private BiPredicate<Content, Content> isContentTheSame()
    {
        return ( c1, c2 ) -> Objects.equals( c1.getId(), c2.getId() ) && Objects.equals( c1.getPath(), c2.getPath() )
            && Objects.equals( c1.getDisplayName(), c2.getDisplayName() ) &&
            Objects.equals( c1.getType(), c2.getType() ) && Objects.equals( c1.getCreator(), c2.getCreator() ) &&
            Objects.equals( c1.getOwner(), c2.getOwner() ) && Objects.equals( c1.getCreatedTime(), c2.getCreatedTime() ) &&
            Objects.equals( c1.getInherit(), c2.getInherit() ) && Objects.equals( c1.getOriginProject(), c2.getOriginProject() ) &&
            Objects.equals( c1.getChildOrder(), c2.getChildOrder() ) &&
            Objects.equals( c1.getPermissions(), c2.getPermissions() ) && Objects.equals( c1.getAttachments(), c2.getAttachments() ) &&
            Objects.equals( c1.getData(), c2.getData() ) && Objects.equals( c1.getAllExtraData(), c2.getAllExtraData() ) &&
            Objects.equals( c1.getPage(), c2.getPage() ) && Objects.equals( c1.getLanguage(), c2.getLanguage() ) &&
            Objects.equals( c1.getPublishInfo(), c2.getPublishInfo() ) && Objects.equals( c1.getWorkflowInfo(), c2.getWorkflowInfo() ) &&
            Objects.equals( c1.getManualOrderValue(), c2.getManualOrderValue() ) &&
            Objects.equals( c1.getOriginalName(), c2.getOriginalName() ) &&
            Objects.equals( c1.getOriginalParentPath(), c2.getOriginalParentPath() ) &&
            Objects.equals( c1.getArchivedTime(), c2.getArchivedTime() ) && Objects.equals( c1.getArchivedBy(), c2.getArchivedBy() ) &&
            Objects.equals( c1.getVariantOf(), c2.getVariantOf() );
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

        if ( !params.isClearAttachments() && editedContent.getValidationErrors() != null &&
            editedContent.getValidationErrors().hasErrors() )
        {
            editedContent.getValidationErrors().stream()
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
                final ProcessUpdateParams processUpdateParams = ProcessUpdateParams.create()
                    .mediaInfo( mediaInfo )
                    .content( content )
                    .build();
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
        return Content.create( editableContent.build() ).build();
    }

    private void validateProjectAccess( final Content originalContent, final Content editedContent )
    {
        if ( originalContent instanceof Site originalSite && editedContent instanceof Site editedSite &&
            !Objects.equals( originalSite.getSiteConfigs(), editedSite.getSiteConfigs() ) )
        {
            final Context context = ContextAccessor.current();
            final AuthenticationInfo authInfo = context.getAuthInfo();
            final ProjectName projectName = ProjectName.from( context.getRepositoryId() );

            if ( !ProjectAccessHelper.hasAccess( authInfo, projectName, ProjectRole.OWNER ) )
            {
                throw new ForbiddenAccessException( authInfo.getUser() );
            }
        }
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
            Objects.requireNonNull( params, "params cannot be null" );
        }

        UpdateContentCommand build()
        {
            validate();
            return new UpdateContentCommand( this );
        }

    }

}
