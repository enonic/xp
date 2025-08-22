package com.enonic.xp.core.impl.content;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import com.enonic.xp.archive.ArchiveConstants;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentInheritType;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.MoveContentParams;
import com.enonic.xp.content.RenameContentParams;

import static com.enonic.xp.content.ContentConstants.CONTENT_ROOT_PATH_ATTRIBUTE;

final class MovedEventSyncCommand
    extends AbstractContentEventSyncCommand
{
    MovedEventSyncCommand( final Builder builder )
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
        final List<ContentToSync> toArchive = new ArrayList<>();
        final List<ContentToSync> toRestore = new ArrayList<>();
        final List<ContentToSync> toMove = new ArrayList<>();

        params.getContents().forEach( contentToSync -> {
            if ( !contentToSync.getSourceContext()
                .getAttribute( CONTENT_ROOT_PATH_ATTRIBUTE )
                .equals( contentToSync.getTargetContext().getAttribute( CONTENT_ROOT_PATH_ATTRIBUTE ) ) )
            {
                if ( ArchiveConstants.ARCHIVE_ROOT_PATH
                    .equals( contentToSync.getSourceContext().getAttribute( CONTENT_ROOT_PATH_ATTRIBUTE ) ) )
                {
                    toArchive.add( contentToSync );
                }
                else if ( ArchiveConstants.ARCHIVE_ROOT_PATH
                    .equals( contentToSync.getTargetContext().getAttribute( CONTENT_ROOT_PATH_ATTRIBUTE ) ) )
                {
                    toRestore.add( contentToSync );
                }
            }
            else
            {
                toMove.add( contentToSync );
            }
        } );

        if ( !toArchive.isEmpty() )
        {
            MovedEventSyncArchiver.create().contentService( contentService ).addContents( toArchive ).build().execute();
        }
        if ( !toRestore.isEmpty() )
        {
            MovedEventSyncRestorer.create().contentService( contentService ).addContents( toRestore ).build().execute();
        }
        if ( !toMove.isEmpty() )
        {
            this.doMove( toMove );
        }
    }

    private void doMove( final List<ContentToSync> contents )
    {
        final Set<ContentPath> paths =
            contents.stream().map( content -> content.getSourceContent().getPath() ).collect( Collectors.toSet() );

        final List<ContentToSync> rootsToSync = contents.stream()
            .filter( content -> !paths.contains( content.getSourceContent().getParentPath() ) )
            .collect( Collectors.toList() );

        rootsToSync.forEach( content -> {

            if ( isToSync( content.getTargetContent() ) )
            {
                content.getTargetContext().runWith( () -> {
                    final Content sourceParent =
                        content.getSourceContext().callWith( () -> contentService.getByPath( content.getSourceContent().getParentPath() ) );
                    final Content sourceRoot = content.getSourceContext().callWith( () -> contentService.getByPath( ContentPath.ROOT ) );

                    final ContentPath targetParentPath = contentService.contentExists( sourceParent.getId() )
                        ? contentService.getById( sourceParent.getId() ).getPath()
                        : sourceRoot.getId().equals( sourceParent.getId() ) ? ContentPath.ROOT : null;

                    if ( targetParentPath == null )
                    {
                        return;
                    }

                    if ( !targetParentPath.equals( content.getTargetContent().getParentPath() ) )
                    {
                        final ContentPath newPath = buildNewPath( targetParentPath, content.getTargetContent().getName() );

                        if ( !Objects.equals( newPath.getName(), content.getTargetContent().getPath().getName() ) )
                        {
                            contentService.rename( RenameContentParams.create()
                                                       .contentId( content.getTargetContent().getId() )
                                                       .newName( newPath.getName() )
                                                       .build() );
                        }
                        contentService.move( MoveContentParams.create()
                                                 .contentId( content.getTargetContent().getId() )
                                                 .parentContentPath( targetParentPath )
                                                 .stopInherit( false )
                                                 .build() );
                    }
                } );
            }
        } );
    }

    private boolean isToSync( final Content targetContent )
    {
        return targetContent.getInherit().contains( ContentInheritType.PARENT );
    }

    public static class Builder
        extends AbstractContentEventSyncCommand.Builder<Builder>
    {
        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkArgument( params.getContents().stream().allMatch( content -> content.getSourceContent() != null ),
                                         "sourceContent must be set" );
            Preconditions.checkArgument( params.getContents().stream().allMatch( content -> content.getTargetContent() != null ),
                                         "targetContent must be set" );
        }

        @Override
        public MovedEventSyncCommand build()
        {
            validate();
            return new MovedEventSyncCommand( this );
        }
    }
}
