package com.enonic.xp.core.impl.content;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentInheritType;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.RenameContentParams;

final class RenamedEventSyncCommand
    extends AbstractContentEventSyncCommand
{

    RenamedEventSyncCommand( final Builder builder )
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
        this.params.getContents().forEach( this::doSync );
    }

    private void doSync( final ContentToSync content )
    {
        content.getTargetContext().runWith( () -> {
            if ( isToSyncName( content.getTargetContent() ) )
            {
                if ( needToRename( content.getSourceContent(), content.getTargetContent() ) )
                {
                    final ContentName newName = ContentName.from(
                        buildNewPath( content.getTargetContent().getParentPath(), content.getSourceContent().getName() ).getName() );

                    contentService.rename( RenameContentParams.create()
                                               .contentId( content.getTargetContent().getId() )
                                               .newName( newName )
                                               .stopInherit( !newName.equals( content.getSourceContent().getName() ) )
                                               .build() );
                }
            }
        } );
    }

    private boolean needToRename( final Content sourceContent, final Content targetContent )
    {
        return !targetContent.getName().equals( sourceContent.getName() );
    }

    private boolean isToSyncName( final Content targetContent )
    {
        return targetContent.getInherit().contains( ContentInheritType.NAME );
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

        public RenamedEventSyncCommand build()
        {
            validate();
            return new RenamedEventSyncCommand( this );
        }
    }
}
