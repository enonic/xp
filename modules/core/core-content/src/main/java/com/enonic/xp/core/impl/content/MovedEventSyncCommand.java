package com.enonic.xp.core.impl.content;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import com.enonic.xp.archive.ArchiveConstants;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentInheritType;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.MoveContentParams;

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

        for ( ContentToSync contentToSync : contentToSync )
        {
            if ( !contentToSync.getSourceCtx()
                .getAttribute( CONTENT_ROOT_PATH_ATTRIBUTE )
                .equals( contentToSync.getTargetCtx().getAttribute( CONTENT_ROOT_PATH_ATTRIBUTE ) ) )
            {
                if ( ArchiveConstants.ARCHIVE_ROOT_PATH.equals( contentToSync.getSourceCtx().getAttribute( CONTENT_ROOT_PATH_ATTRIBUTE ) ) )
                {
                    toArchive.add( contentToSync );
                }
                else if ( ArchiveConstants.ARCHIVE_ROOT_PATH.equals(
                    contentToSync.getTargetCtx().getAttribute( CONTENT_ROOT_PATH_ATTRIBUTE ) ) )
                {
                    toRestore.add( contentToSync );
                }
            }
            else
            {
                toMove.add( contentToSync );
            }
        }

        if ( !toArchive.isEmpty() )
        {
            MovedEventSyncArchiver.create().contentService( layersContentService ).addContents( toArchive ).build().execute();
        }
        if ( !toRestore.isEmpty() )
        {
            MovedEventSyncRestorer.create().contentService( layersContentService ).addContents( toRestore ).build().execute();
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

        final List<ContentToSync> rootsToSync =
            contents.stream().filter( content -> !paths.contains( content.getSourceContent().getParentPath() ) ).toList();

        for ( ContentToSync content : rootsToSync )
        {
            if ( isToSync( content.getTargetContent() ) )
            {
                content.getTargetCtx().runWith( () -> {
                    final ContentId sourceParentId = content.getSourceCtx()
                        .callWith( () -> layersContentService.getByPath( content.getSourceContent().getParentPath() ).orElseThrow() )
                        .getId();

                    layersContentService.getById( sourceParentId )
                        .map( Content::getPath )
                        .or( () -> sourceParentId.equals( content.getSourceCtx()
                                                              .callWith(
                                                                  () -> layersContentService.getByPath( ContentPath.ROOT ).orElseThrow() )
                                                              .getId() ) ? Optional.of( ContentPath.ROOT ) : Optional.empty() )
                        .ifPresent( targetParentPath -> {
                            if ( !content.getTargetContent().getPath().equals( content.getSourceContent().getPath() ) )
                            {
                                final ContentPath newPath =
                                    buildNewPath( targetParentPath, content.getSourceContent().getName(), content.getTargetContent() );

                                layersContentService.move( MoveContentParams.create()
                                                               .contentId( content.getTargetContent().getId() )
                                                               .newName( newPath.getName() )
                                                               .parentContentPath( targetParentPath )
                                                               .build() );
                            }
                        } );
                } );
            }
        }
    }

    private boolean isToSync( final Content targetContent )
    {
        return targetContent.getInherit().contains( ContentInheritType.PARENT ) ||
            targetContent.getInherit().contains( ContentInheritType.NAME );
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
        public MovedEventSyncCommand build()
        {
            validate();
            return new MovedEventSyncCommand( this );
        }
    }
}
