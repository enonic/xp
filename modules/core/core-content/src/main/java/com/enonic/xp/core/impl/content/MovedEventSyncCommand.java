package com.enonic.xp.core.impl.content;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentInheritType;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.MoveContentParams;
import com.enonic.xp.content.RenameContentParams;

final class MovedEventSyncCommand
    extends AbstractContentEventSyncCommand
{
    public MovedEventSyncCommand( final Builder builder )
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
                final ContentPath targetParentPath = contentService.contentExists( sourceParent.getId() )
                    ? contentService.getById( sourceParent.getId() ).getPath()
                    : sourceRoot.getId().equals( sourceParent.getId() ) ? ContentPath.ROOT : null;

                if ( targetParentPath != null )
                {
                    if ( !targetParentPath.equals( params.getTargetContent().getParentPath() ) )
                    {
                        final ContentPath newPath = buildNewPath( targetParentPath, params.getTargetContent().getName() );

                        if ( !params.getTargetContent().getPath().equals( newPath ) )
                        {
                            contentService.rename( RenameContentParams.create().
                                contentId( params.getTargetContent().getId() ).
                                newName( ContentName.from( newPath.getName() ) ).
                                build() );
                        }

                        contentService.move( MoveContentParams.create().
                            contentId( params.getTargetContent().getId() ).
                            parentContentPath( targetParentPath ).
                            stopInherit( false ).
                            build() );

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
