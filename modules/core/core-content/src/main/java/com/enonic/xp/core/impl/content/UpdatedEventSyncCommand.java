package com.enonic.xp.core.impl.content;

import java.util.Objects;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteSource;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.AttachmentNames;
import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentInheritType;
import com.enonic.xp.content.Media;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.content.WorkflowState;
import com.enonic.xp.icon.Thumbnail;
import com.enonic.xp.media.MediaInfo;
import com.enonic.xp.media.MediaInfoService;
import com.enonic.xp.schema.content.ContentTypeFromMimeTypeResolver;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.util.BinaryReferences;

final class UpdatedEventSyncCommand
    extends AbstractContentEventSyncCommand
{
    private final MediaInfoService mediaInfoService;

    UpdatedEventSyncCommand( final Builder builder )
    {
        super( builder );
        this.mediaInfoService = builder.mediaInfoService;
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
                        final UpdateContentParams updateParams = updateParams( content.getSourceContent() );

                        doSyncMedia( content, updateParams );
                        doSyncThumbnail( content, updateParams );

                        contentService.update( updateParams );
                    }
                }
            }
        } );
    }

    private void doSyncMedia( final ContentToSync content, final UpdateContentParams updateParams )
    {
        if ( content.getSourceContent() instanceof Media )
        {
            final Media sourceMedia = (Media) content.getSourceContent();

            final Attachment mediaAttachment = sourceMedia.getMediaAttachment();

            final ByteSource sourceBinary = content.getSourceContext()
                .callWith( () -> contentService.getBinary( sourceMedia.getId(), mediaAttachment.getBinaryReference() ) );
            final MediaInfo mediaInfo = content.getSourceContext().callWith( () -> mediaInfoService.parseMediaInfo( sourceBinary ) );

            final ContentTypeName type = ContentTypeFromMimeTypeResolver.resolve( mediaAttachment.getMimeType() );

            final CreateAttachment createAttachment = CreateAttachment.create()
                .name( mediaAttachment.getName() )
                .mimeType( mediaAttachment.getMimeType() )
                .label( "source" )
                .byteSource( sourceBinary )
                .text( type != null && type.isTextualMedia() ? mediaInfo.getTextContent() : null )
                .build();

            updateParams.clearAttachments( true ).createAttachments( CreateAttachments.from( createAttachment ) );
        }
    }

    private void doSyncThumbnail( final ContentToSync content, final UpdateContentParams updateParams )
    {
        if ( !Objects.equals( content.getSourceContent().getThumbnail(), content.getTargetContent().getThumbnail() ) )
        {
            final Thumbnail sourceThumbnail = content.getSourceContent().getThumbnail();

            if ( sourceThumbnail != null )
            {
                final ByteSource sourceBinary = content.getSourceContext()
                    .callWith( () -> contentService.getBinary( content.getSourceContent().getId(), sourceThumbnail.getBinaryReference() ) );

                final CreateAttachment createThumbnail = CreateAttachment.create()
                    .name( AttachmentNames.THUMBNAIL )
                    .mimeType( sourceThumbnail.getMimeType() )
                    .byteSource( sourceBinary )
                    .build();

                final CreateAttachments.Builder createAttachments = CreateAttachments.create().add( createThumbnail );
                if ( updateParams.getCreateAttachments() != null )
                {
                    createAttachments.add( updateParams.getCreateAttachments() );
                }

                updateParams.createAttachments( createAttachments.build() );
            }
            else
            {
                final Thumbnail targetThumbnail = content.getTargetContent().getThumbnail();
                updateParams.removeAttachments( BinaryReferences.from( targetThumbnail.getBinaryReference() ) );
            }
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
            sourceContent.isValid() != targetContent.isValid();
    }

    private UpdateContentParams updateParams( final Content source )
    {
        return new UpdateContentParams().contentId( source.getId() )
            .modifier( PrincipalKey.ofAnonymous() )
            .requireValid( false )
            .stopInherit( false )
            .editor( edit -> {
                edit.data = source.getData();
                edit.extraDatas = source.getAllExtraData();
                edit.displayName = source.getDisplayName();
                edit.owner = source.getOwner();
                edit.language = source.getLanguage();
                edit.workflowInfo = source.getWorkflowInfo();
                edit.page = source.getPage();
                edit.thumbnail = source.getThumbnail();
                edit.valid = source.isValid();
                edit.processedReferences = ContentIds.create().addAll( source.getProcessedReferences() );
            } );
    }

    public static class Builder
        extends AbstractContentEventSyncCommand.Builder<Builder>
    {
        private MediaInfoService mediaInfoService;

        public Builder mediaInfoService( final MediaInfoService mediaInfoService )
        {
            this.mediaInfoService = mediaInfoService;
            return this;
        }

        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( mediaInfoService, "mediaInfoService must be set." );
            Preconditions.checkArgument( params.getContents().stream().allMatch( content -> content.getSourceContent() != null ),
                                         "sourceContent must be set." );
            Preconditions.checkArgument( params.getContents().stream().allMatch( content -> content.getTargetContent() != null ),
                                         "targetContent must be set." );
        }

        public UpdatedEventSyncCommand build()
        {
            validate();
            return new UpdatedEventSyncCommand( this );
        }
    }
}
