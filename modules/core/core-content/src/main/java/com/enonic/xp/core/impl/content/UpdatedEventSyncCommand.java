package com.enonic.xp.core.impl.content;

import java.util.Objects;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteSource;

import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentInheritType;
import com.enonic.xp.content.PatchContentParams;
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
        contentToSync.forEach( this::doSync );
    }

    private void doSync( final ContentToSync content )
    {
        content.getTargetCtx().runWith( () -> {
            if ( isContentSyncable( content.getSourceContent(), content.getTargetContent() ) )
            {
                if ( isToSyncData( content.getTargetContent() ) )
                {
                    if ( needToUpdate( content.getSourceContent(), content.getTargetContent() ) )
                    {
                        final PatchContentParams.Builder updateParams = updateParams( content.getSourceContent() );

                        doSyncAttachments( content, updateParams );

                        contentService.patch( updateParams.build() );
                    }
                }
            }
        } );
    }

    private void doSyncAttachments( ContentToSync content, PatchContentParams.Builder patchParams )
    {
        if ( !content.getSourceContent().getAttachments().equals( content.getTargetContent().getAttachments() ) )
        {
            patchParams.createAttachments( buildAttachmentsFromSource( content ) );
        }
    }

    private CreateAttachments buildAttachmentsFromSource( ContentToSync content )
    {
        CreateAttachments.Builder attachmentsBuilder = CreateAttachments.create();

        content.getSourceContent().getAttachments().forEach( sourceAttachment -> {
            ByteSource sourceBinary = content.getSourceCtx()
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
        return WorkflowState.READY.equals( sourceContent.getWorkflowInfo().getState() ) ||
            !WorkflowState.READY.equals( targetContent.getWorkflowInfo().getState() );
    }

    private boolean needToUpdate( final Content sourceContent, final Content targetContent )
    {
        return !Objects.equals( sourceContent.getData(), targetContent.getData() ) ||
            !Objects.equals( sourceContent.getAllExtraData(), targetContent.getAllExtraData() ) ||
            !Objects.equals( sourceContent.getPage(), targetContent.getPage() ) ||
            !Objects.equals( sourceContent.getDisplayName(), targetContent.getDisplayName() ) ||
            !Objects.equals( sourceContent.getOwner(), targetContent.getOwner() ) ||
            !Objects.equals( sourceContent.getLanguage(), targetContent.getLanguage() ) ||
            !Objects.equals( sourceContent.getWorkflowInfo(), targetContent.getWorkflowInfo() ) ||
            !Objects.equals( sourceContent.getProcessedReferences(), targetContent.getProcessedReferences() ) ||
            !Objects.equals( sourceContent.getAttachments(), targetContent.getAttachments() ) ||
            !Objects.equals( sourceContent.getValidationErrors(), targetContent.getValidationErrors() ) ||
            !Objects.equals( sourceContent.getType(), targetContent.getType() ) ||
            !Objects.equals( sourceContent.getCreatedTime(), targetContent.getCreatedTime() ) ||
            !Objects.equals( sourceContent.getModifiedTime(), targetContent.getModifiedTime() ) ||
            !Objects.equals( sourceContent.getModifier(), targetContent.getModifier() ) ||
            !Objects.equals( sourceContent.getCreator(), targetContent.getCreator() ) ||
            !Objects.equals( sourceContent.getArchivedBy(), targetContent.getArchivedBy() ) ||
            !Objects.equals( sourceContent.getArchivedTime(), targetContent.getArchivedTime() ) ||
            !Objects.equals( sourceContent.getVariantOf(), targetContent.getVariantOf() ) ||
            !Objects.equals( sourceContent.getChildOrder(), targetContent.getChildOrder() ) ||
            !Objects.equals( sourceContent.getManualOrderValue(), targetContent.getManualOrderValue() ) ||
            sourceContent.isValid() != targetContent.isValid();
    }

    private PatchContentParams.Builder updateParams( final Content source )
    {
        return PatchContentParams.create().contentId( source.getId() ).patcher( edit -> {
            edit.data.setValue( source.getData() );
            edit.extraDatas.setValue( source.getAllExtraData() );
            edit.displayName.setValue( source.getDisplayName() );
            edit.owner.setValue( source.getOwner() );
            edit.language.setValue( source.getLanguage() );
            edit.workflowInfo.setValue( source.getWorkflowInfo() );
            edit.page.setValue( source.getPage() );
            edit.processedReferences.setValue( ContentIds.create().addAll( source.getProcessedReferences() ).build() );

            edit.attachments.setValue( source.getAttachments() );

            edit.type.setValue( source.getType() );
            edit.createdTime.setValue( source.getCreatedTime() );
            edit.modifiedTime.setValue( source.getModifiedTime() );
            edit.modifier.setValue( source.getModifier() );
            edit.creator.setValue( source.getCreator() );
            edit.archivedBy.setValue( source.getArchivedBy() );
            edit.archivedTime.setValue( source.getArchivedTime() );
            edit.variantOf.setValue( source.getVariantOf() );

            edit.manualOrderValue.setValue( source.getManualOrderValue() );
            edit.childOrder.setValue( source.getChildOrder() );

            edit.valid.setValue( source.isValid() );
            edit.validationErrors.setValue( source.getValidationErrors() );
        } );
    }

    public static class Builder
        extends AbstractContentEventSyncCommand.Builder<Builder>
    {
        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkArgument( contentToSync.stream().allMatch( content -> content.getSourceContent() != null ),
                                         "sourceContent must be set" );
            Preconditions.checkArgument( contentToSync.stream().allMatch( content -> content.getTargetContent() != null ),
                                         "targetContent must be set" );
        }

        @Override
        public UpdatedEventSyncCommand build()
        {
            validate();
            return new UpdatedEventSyncCommand( this );
        }
    }
}
