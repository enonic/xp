package com.enonic.xp.core.impl.content;

import java.util.EnumSet;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteSource;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentAlreadyExistsException;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentInheritType;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ImportContentParams;
import com.enonic.xp.node.BinaryAttachment;
import com.enonic.xp.node.BinaryAttachments;
import com.enonic.xp.node.InsertManualStrategy;
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
                if ( contentService.contentExists( content.getSourceContent().getParentPath() ) )
                {
                    final ContentId parentId = contentService.getByPath( content.getSourceContent().getParentPath() ).getId();
                    content.getTargetContext().runWith( () -> {
                        if ( content.getSourceContent().getParentPath().isRoot() )
                        {
                            syncRootContent( content );
                        }
                        else if ( contentService.contentExists( parentId ) )
                        {
                            syncChildContent( parentId, content );
                        }
                    } );
                }
            } );

        }
        catch ( ContentAlreadyExistsException e )
        {
            LOG.warn( "content [{}] already exists.", content.getId() );
        }
    }

    private void syncRootContent( final ContentToSync content )
    {
        contentService.importContent( createImportParams( content, ContentPath.ROOT, null ) );
    }

    private void syncChildContent( final ContentId parentId, final ContentToSync content )
    {
        final Content targetParent = contentService.getById( parentId );

        contentService.importContent( createImportParams( content, targetParent.getPath(), targetParent.getChildOrder().isManualOrder()
            ? InsertManualStrategy.MANUAL
            : null ) );
    }

    private ImportContentParams createImportParams( final ContentToSync content, final ContentPath parentPath,
                                                    final InsertManualStrategy insertManualStrategy )
    {
        final BinaryAttachments.Builder builder = BinaryAttachments.create();

        content.getSourceContent().getAttachments().forEach( attachment -> {
            final ByteSource binary =
                content.getSourceContext().callWith( () -> contentService.getBinary( content.getId(), attachment.getBinaryReference() ) );
            builder.add( new BinaryAttachment( attachment.getBinaryReference(), binary ) );
        } );

        final ContentPath targetPath = buildNewPath( parentPath, content.getSourceContent().getName(), null );

        final EnumSet<ContentInheritType> inheritTypes = Objects.equals( content.getSourceContent().getName(), targetPath.getName() )
            ? EnumSet.allOf( ContentInheritType.class )
            : EnumSet.complementOf( EnumSet.of( ContentInheritType.NAME ) );

        return ImportContentParams.create()
            .importContent( content.getSourceContent() )
            .targetPath( targetPath )
            .binaryAttachments( builder.build() )
            .inherit( inheritTypes )
            .originProject( ProjectName.from( content.getSourceContext().getRepositoryId() ) )
            .importPermissionsOnCreate( false )
            .insertManualStrategy( insertManualStrategy )
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
