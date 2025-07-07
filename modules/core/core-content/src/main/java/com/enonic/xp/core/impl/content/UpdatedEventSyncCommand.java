package com.enonic.xp.core.impl.content;

import java.util.Objects;
import java.util.Optional;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteSource;

import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentInheritType;
import com.enonic.xp.content.ModifyContentParams;
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

                        if ( patched )
                        {
                            final ModifyContentParams modifyParams = modifyParams( content.getSourceContent() );

                            //attachments

                            contentService.modify( modifyParams );
                        }
                        else
                        {
                            final UpdateContentParams updateParams = updateParams( content.getSourceContent() );

                            doSyncAttachments( content, updateParams );

                            contentService.update( updateParams );
                        }

                    }
                }
            }
        } );
    }

    private void doSyncAttachments( final ContentToSync content, final UpdateContentParams updateParams )
    {
        final Attachments sourceAttachments = content.getSourceContent().getAttachments();
        final Attachments targetAttachments = content.getTargetContent().getAttachments();
        if ( !sourceAttachments.equals( targetAttachments ) )
        {
            updateParams.clearAttachments( true );

            final CreateAttachments.Builder createAttachments = CreateAttachments.create();

            sourceAttachments.forEach( ( sourceAttachment ) -> {
                final ByteSource sourceBinary = content.getSourceContext()
                    .callWith(
                        () -> contentService.getBinary( content.getSourceContent().getId(), sourceAttachment.getBinaryReference() ) );

                final CreateAttachment createAttachment = CreateAttachment.create()
                    .name( sourceAttachment.getName() )
                    .mimeType( sourceAttachment.getMimeType() )
                    .byteSource( sourceBinary )
                    .text( sourceAttachment.getTextContent() )
                    .label( sourceAttachment.getLabel() )
                    .build();

                createAttachments.add( createAttachment );

            } );

            updateParams.createAttachments( createAttachments.build() );
        }
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
            sourceContent.isValid() != targetContent.isValid();
    }

    private UpdateContentParams updateParams( final Content source )
    {
        return new UpdateContentParams().contentId( source.getId() ).requireValid( false ).stopInherit( false ).editor( edit -> {
            edit.data = source.getData();
            edit.extraDatas = source.getAllExtraData();
            edit.displayName = source.getDisplayName();
            edit.owner = source.getOwner();
            edit.language = source.getLanguage();
            edit.workflowInfo = source.getWorkflowInfo();
            edit.page = source.getPage();
            edit.thumbnail = source.getThumbnail();
            edit.processedReferences = ContentIds.create().addAll( source.getProcessedReferences() );
        } );
    }

    private ModifyContentParams modifyParams( final Content source )
    {
        return ModifyContentParams.create().contentId( source.getId() )
//            .branches( Branches.from( ContentConstants.BRANCH_DRAFT ))
//                                .createAttachments(  ) TODO: patch binaries?
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
                modifiable.parentPath.setValue( source.getParentPath() );
                modifiable.name.setValue( source.getName() );
                modifiable.childOrder.setValue( source.getChildOrder() );
                modifiable.originProject.setValue( source.getOriginProject() );
                modifiable.originalParentPath.setValue( source.getOriginalParentPath() );
                modifiable.originalName.setValue( source.getOriginalName() );
                modifiable.archivedTime.setValue( source.getArchivedTime() );
                modifiable.archivedBy.setValue( source.getArchivedBy() );
            } ).build();
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
