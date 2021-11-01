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
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.MoveContentParams;
import com.enonic.xp.content.RenameContentParams;

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
                .getAttribute( "contentRootPath" )
                .equals( contentToSync.getTargetContext().getAttribute( "contentRootPath" ) ) )
            {
                if ( ArchiveConstants.ARCHIVE_ROOT_PATH.toString()
                    .equals( contentToSync.getSourceContext().getAttribute( "contentRootPath" ).toString() ) )
                {
                    toArchive.add( contentToSync );
                }
                else if ( ArchiveConstants.ARCHIVE_ROOT_PATH.toString()
                    .equals( contentToSync.getTargetContext().getAttribute( "contentRootPath" ).toString() ) )
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

        this.doMove( toMove );
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
                                                       .newName( ContentName.from( newPath.getName() ) )
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

//    private void move()
//    {
//        params.getContents().stream().filter( content -> isToSyncParent( content.getTargetContent() ) ).forEach( this::doMove );
//    }

    private boolean isToSync( final Content targetContent )
    {
        return targetContent.getInherit().contains( ContentInheritType.PARENT );
    }

    public static class Builder
        extends AbstractContentEventSyncCommand.Builder<Builder>
    {
        void validate()
        {
            super.validate();
            Preconditions.checkArgument( params.getContents().stream().allMatch( content -> content.getSourceContent() != null ),
                                         "sourceContent must be set." );
            Preconditions.checkArgument( params.getContents().stream().allMatch( content -> content.getTargetContent() != null ),
                                         "targetContent must be set." );
        }

        public MovedEventSyncCommand build()
        {
            validate();
            return new MovedEventSyncCommand( this );
        }
    }
}
