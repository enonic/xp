package com.enonic.xp.core.impl.content;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentInheritType;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.RenameContentParams;

final class RenamedEventSyncCommand
    extends AbstractContentEventSyncCommand
{

    public RenamedEventSyncCommand( final Builder builder )
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
        if ( isToSyncName( params.getTargetContent() ) )
        {
            if ( needToRename( params.getSourceContent(), params.getTargetContent() ) )
            {
                final ContentName newName = ContentName.from(
                    buildNewPath( params.getTargetContent().getParentPath(), params.getSourceContent().getName() ).getName() );

                contentService.rename( RenameContentParams.create().
                    contentId( params.getTargetContent().getId() ).
                    newName( newName ).
                    stopInherit( !newName.equals( params.getSourceContent().getName() ) ).
                    build() );
            }
        }
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
            Preconditions.checkNotNull( params.getSourceContent(), "sourceContent must be set." );
            Preconditions.checkNotNull( params.getTargetContent(), "targetContent must be set." );
        }

        public RenamedEventSyncCommand build()
        {
            validate();
            return new RenamedEventSyncCommand( this );
        }
    }
}
