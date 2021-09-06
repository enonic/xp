package com.enonic.xp.core.impl.content;

import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.xp.archive.ArchiveConstants;
import com.enonic.xp.archive.ArchiveContentParams;
import com.enonic.xp.archive.RestoreContentParams;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentInheritType;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.MoveContentParams;
import com.enonic.xp.content.RenameContentParams;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;

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
        if ( isToSyncParent( params.getTargetContent() ) )
        {
            final Content sourceParent =
                params.getSourceContext().callWith( () -> contentService.getByPath( params.getSourceContent().getParentPath() ) );
            final Content sourceRoot = params.getSourceContext().callWith( () -> contentService.getByPath( ContentPath.ROOT ) );

            params.getTargetContext().runWith( () -> {


                    if ( !params.getSourceContext()
                        .getAttribute( "contentRootPath" )
                        .equals( params.getTargetContext().getAttribute( "contentRootPath" ) ) )
                    {
                        if ( ArchiveConstants.ARCHIVE_ROOT_PATH.toString().equals( params.getSourceContext().getAttribute( "contentRootPath" ).toString() ) )
                        {
                            contentService.archive( ArchiveContentParams.create().contentId( params.getTargetContent().getId() ).build() );
                        }
                        else if ( ArchiveConstants.ARCHIVE_ROOT_PATH.toString().equals( params.getTargetContext().getAttribute( "contentRootPath" ).toString() ) )
                        {
                            final Context
                                targetContextToRestore = ContextBuilder.from( params.getTargetContext() ).attribute( "contentRootPath", params.getSourceContext().getAttribute( "contentRootPath" ) ).build();

                            final ContentPath targetParentPath = targetContextToRestore.callWith( () -> contentService.contentExists( sourceParent.getId() )
                                ? contentService.getById( sourceParent.getId() ).getPath()
                                : ContentPath.ROOT);

                            contentService.restore( RestoreContentParams.create()
                                                        .contentId( params.getTargetContent().getId() )
                                                        .path( targetParentPath )
                                                        .build() );
                        }
                    }
                    else
                    {
                        final ContentPath targetParentPath = contentService.contentExists( sourceParent.getId() )
                            ? contentService.getById( sourceParent.getId() ).getPath()
                            : sourceRoot.getId().equals( sourceParent.getId() ) ? ContentPath.ROOT : null;

                        if(targetParentPath == null) {
                            return;
                        }

                        if ( !targetParentPath.equals( params.getTargetContent().getParentPath() ) )
                        {
                            final ContentPath newPath = buildNewPath( targetParentPath, params.getTargetContent().getName() );

                            if ( !Objects.equals( newPath.getName(), params.getTargetContent().getPath().getName() ) )
                            {
                                contentService.rename( RenameContentParams.create()
                                                           .contentId( params.getTargetContent().getId() )
                                                           .newName( ContentName.from( newPath.getName() ) )
                                                           .build() );
                            }
                            contentService.move( MoveContentParams.create()
                                                     .contentId( params.getTargetContent().getId() )
                                                     .parentContentPath( targetParentPath )
                                                     .stopInherit( false )
                                                     .build() );
                        }

                }
            } );
        }
    }

    private boolean isToSyncParent( final Content targetContent )
    {
        return targetContent.getInherit().contains( ContentInheritType.PARENT );
    }

    public static class Builder
        extends AbstractContentEventSyncCommand.Builder<Builder>
    {
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( params.getSourceContent(), "sourceContent must be set." );
            Preconditions.checkNotNull( params.getTargetContent(), "targetContent must be set." );
        }

        public MovedEventSyncCommand build()
        {
            validate();
            return new MovedEventSyncCommand( this );
        }
    }
}
