package com.enonic.xp.core.impl.content;

import java.util.EnumSet;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteSource;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentAlreadyExistsException;
import com.enonic.xp.content.ContentInheritType;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ImportContentParams;
import com.enonic.xp.project.ProjectName;

final class CreatedEventSyncCommand
    extends AbstractContentEventSyncCommand
{
    private static final Logger LOG = LoggerFactory.getLogger( CreatedEventSyncCommand.class );

    CreatedEventSyncCommand( final Builder builder )
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
        try
        {
            content.getSourceContext().runWith( () -> {
                contentService.getByPathOptional( content.getSourceContent().getParentPath() )
                    .map( Content::getId )
                    .ifPresent( parentId -> content.getTargetContext().runWith( () -> {
                        if ( content.getSourceContent().getParentPath().isRoot() )
                        {
                            contentService.importContent( createImportParams( content, ContentPath.ROOT ) );
                        }
                        else
                        {
                            contentService.getByIdOptional( parentId )
                                .map( Content::getPath )
                                .ifPresent( path -> contentService.importContent( createImportParams( content, path ) ) );
                        }
                    } ) );
            } );

        }
        catch ( ContentAlreadyExistsException e )
        {
            LOG.warn( "content [{}] already exists.", content.getId() );
        }
    }

    private ImportContentParams createImportParams( final ContentToSync content, final ContentPath parentPath )
    {
        final CreateAttachments.Builder attachments = CreateAttachments.create();

        final Content sourceContent = content.getSourceContent();
        for ( Attachment attachment : sourceContent.getAttachments() )
        {
            final ByteSource binary =
                content.getSourceContext().callWith( () -> contentService.getBinary( content.getId(), attachment.getBinaryReference() ) );
            attachments.add( CreateAttachment.create()
                                 .name( attachment.getName() )
                                 .label( attachment.getLabel() )
                                 .mimeType( attachment.getMimeType() )
                                 .text( attachment.getTextContent() )
                                 .byteSource( binary )
                                 .build() );
        }

        final ContentPath targetPath = buildNewPath( parentPath, sourceContent.getName(), null );

        final EnumSet<ContentInheritType> inheritTypes = Objects.equals( sourceContent.getName(), targetPath.getName() )
            ? EnumSet.allOf( ContentInheritType.class )
            : EnumSet.complementOf( EnumSet.of( ContentInheritType.NAME ) );

        return ImportContentParams.create()
            .importContent( sourceContent )
            .targetPath( targetPath )
            .attachments( attachments.build() )
            .inherit( inheritTypes )
            .originProject( ProjectName.from( content.getSourceContext().getRepositoryId() ) )
            .build();
    }

    public static class Builder
        extends AbstractContentEventSyncCommand.Builder<Builder>
    {

        @Override
        void validate()
        {
            Preconditions.checkArgument( contentToSync.stream().allMatch( content -> content.getSourceContent() != null ),
                                         "sourceContent must be set" );
            Preconditions.checkArgument( contentToSync.stream().allMatch( content -> content.getTargetContent() == null ),
                                         "targetContent must be null" );
        }

        @Override
        public CreatedEventSyncCommand build()
        {
            validate();
            return new CreatedEventSyncCommand( this );
        }
    }
}
