package com.enonic.xp.core.impl.content;

import java.util.Objects;
import java.util.Optional;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteSource;

import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentInheritType;
import com.enonic.xp.content.ModifyContentParams;
import com.enonic.xp.content.ModifyContentResult;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.content.WorkflowState;

final class UpdatedEventSyncCommand
    extends AbstractContentEventSyncCommand
{
    UpdatedEventSyncCommand( final Builder builder )
    {
        super( builder );
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    protected void doSync()
    {
        params.getContents().forEach( this::doSync );
    }

    private void doSync( final ContentToSync content )
    {
        content.getTargetContext().runWith( () -> {
            if ( isContentSyncable( content.getSourceContent(), content.getTargetContent() ) )
            {
                if ( isToSyncData( content.getTargetContent() ) )
                {
                    if ( needToUpdate( content.getSourceContent(), content.getTargetContent() ) )
                    {
                        final Boolean patched = Optional.ofNullable( params.getEventMetadata().get( "patched" ) )
                            .map( f -> Boolean.valueOf( f.toString() ) )
                            .orElse( false );

//                        if ( patched )
//                        {
//                            final ModifyContentParams.Builder modifyParams = modifyParams( content.getSourceContent() );
//
//                            doSyncAttachments( content, modifyParams );
//                            //attachments
//
//                            contentService.modify( modifyParams.build() );
//                        }
//                        else
//                        {
                        final ModifyContentParams.Builder updateParams = updateParams( content.getSourceContent() );

                            doSyncAttachments( content, updateParams );

                        final ModifyContentResult c = contentService.modify( updateParams.build() );
                        c.getContentId();
//                        }

                    }
                }
            }
        } );
    }

    private void doSyncAttachments( ContentToSync content, ModifyContentParams.Builder modifyParams )
    {
        if ( !content.getSourceContent().getAttachments().equals( content.getTargetContent().getAttachments() ) )
        {
            modifyParams.createAttachments( buildAttachmentsFromSource( content ) );
        }
    }

    private void doSyncAttachments( ContentToSync content, UpdateContentParams updateParams )
    {
        if ( !content.getSourceContent().getAttachments().equals( content.getTargetContent().getAttachments() ) )
        {
            updateParams.clearAttachments( true );
            updateParams.createAttachments( buildAttachmentsFromSource( content ) );
        }
    }

    private CreateAttachments buildAttachmentsFromSource( ContentToSync content )
    {
        CreateAttachments.Builder attachmentsBuilder = CreateAttachments.create();

        content.getSourceContent().getAttachments().forEach( sourceAttachment -> {
            ByteSource sourceBinary = content.getSourceContext()
                .callWith( () -> contentService.getBinary( content.getSourceContent().getId(), sourceAttachment.getBinaryReference() ) );

            attachmentsBuilder.add( CreateAttachment.create()
                                        .name( sourceAttachment.getName() )
                                        .mimeType( sourceAttachment.getMimeType() )
                                        .byteSource( sourceBinary )
                                        .text( sourceAttachment.getTextContent() )
                                        .label( sourceAttachment.getLabel() )
                                        .build() );
        } );

        return attachmentsBuilder.build();
    }

    private boolean isToSyncData( final Content targetContent )
    {
        return targetContent.getInherit().contains( ContentInheritType.CONTENT );
    }

    private boolean isContentSyncable( final Content sourceContent, final Content targetContent )
    {
        if ( WorkflowState.READY.equals( targetContent.getWorkflowInfo().getState() ) )
        {
            return WorkflowState.READY.equals( sourceContent.getWorkflowInfo().getState() );
        }
        return true;
    }

    private boolean needToUpdate( final Content sourceContent, final Content targetContent )
    {
        return !Objects.equals( sourceContent.getData(), targetContent.getData() ) ||
            !Objects.equals( sourceContent.getAllExtraData(), targetContent.getAllExtraData() ) ||
            !Objects.equals( sourceContent.getDisplayName(), targetContent.getDisplayName() ) ||
            !Objects.equals( sourceContent.getOwner(), targetContent.getOwner() ) ||
            !Objects.equals( sourceContent.getLanguage(), targetContent.getLanguage() ) ||
            !Objects.equals( sourceContent.getWorkflowInfo(), targetContent.getWorkflowInfo() ) ||
            !Objects.equals( sourceContent.getPage(), targetContent.getPage() ) ||
            !Objects.equals( sourceContent.getThumbnail(), targetContent.getThumbnail() ) ||
            !Objects.equals( sourceContent.getProcessedReferences(), targetContent.getProcessedReferences() ) ||
            !Objects.equals( sourceContent.getAttachments(), targetContent.getAttachments() ) ||
            !Objects.equals( sourceContent.getValidationErrors(), targetContent.getValidationErrors() ) ||
            sourceContent.isValid() != targetContent.isValid();
    }

    private ModifyContentParams.Builder updateParams( final Content source )
    {
        return ModifyContentParams.create().contentId( source.getId() )/*.requireValid( false ).stopInherit( false )*/.modifier( edit -> {
            edit.data.setValue( source.getData() );
            edit.extraDatas.setValue( source.getAllExtraData() );
            edit.displayName.setValue( source.getDisplayName() );
            edit.owner.setValue( source.getOwner() );
            edit.language.setValue( source.getLanguage() );
            edit.workflowInfo.setValue( source.getWorkflowInfo() );
            edit.page.setValue( source.getPage() );
            edit.thumbnail.setValue( source.getThumbnail() );
            edit.processedReferences.setValue( ContentIds.create().addAll( source.getProcessedReferences() ).build() );

            edit.attachments.setValue( source.getAttachments() );

            edit.valid.setValue( source.isValid() );
            edit.validationErrors.setValue( source.getValidationErrors() );
        } );
    }

    private ModifyContentParams.Builder modifyParams( final Content source )
    {
        return ModifyContentParams.create().contentId( source.getId() )
            .modifier( modifiable -> {
                modifiable.displayName.setValue( source.getDisplayName() );
                modifiable.data.setValue( source.getData() );
                modifiable.extraDatas.setValue( source.getAllExtraData() );
                modifiable.page.setValue( source.getPage() );
                modifiable.valid.setValue( source.isValid() );
                modifiable.thumbnail.setValue( source.getThumbnail() );
                modifiable.owner.setValue( source.getOwner() );
                modifiable.language.setValue( source.getLanguage() );
                modifiable.creator.setValue( source.getCreator() );
                modifiable.createdTime.setValue( source.getCreatedTime() );
                modifiable.modifier.setValue( source.getModifier() );
                modifiable.modifiedTime.setValue( source.getModifiedTime() );
                modifiable.publishInfo.setValue( source.getPublishInfo() );
                modifiable.processedReferences.setValue( source.getProcessedReferences() );
                modifiable.workflowInfo.setValue( source.getWorkflowInfo() );
                modifiable.manualOrderValue.setValue( source.getManualOrderValue() );
                modifiable.inherit.setValue( source.getInherit() );
                modifiable.variantOf.setValue( source.getVariantOf() );
                modifiable.attachments.setValue( source.getAttachments() );
                modifiable.validationErrors.setValue( source.getValidationErrors() );
                modifiable.type.setValue( source.getType() );
//                modifiable.parentPath.setValue( source.getParentPath() ); TODO: verify
//                modifiable.name.setValue( source.getName() ); TODO: verify
                modifiable.childOrder.setValue( source.getChildOrder() );
                modifiable.originProject.setValue( source.getOriginProject() );
                modifiable.originalParentPath.setValue( source.getOriginalParentPath() );
                modifiable.originalName.setValue( source.getOriginalName() );
                modifiable.archivedTime.setValue( source.getArchivedTime() );
                modifiable.archivedBy.setValue( source.getArchivedBy() );
            } );
    }

    public static class Builder
        extends AbstractContentEventSyncCommand.Builder<Builder>
    {
        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkArgument( params.getContents().stream().allMatch( content -> content.getSourceContent() != null ),
                                         "sourceContent must be set." );
            Preconditions.checkArgument( params.getContents().stream().allMatch( content -> content.getTargetContent() != null ),
                                         "targetContent must be set." );
        }

        @Override
        public UpdatedEventSyncCommand build()
        {
            validate();
            return new UpdatedEventSyncCommand( this );
        }
    }
}
