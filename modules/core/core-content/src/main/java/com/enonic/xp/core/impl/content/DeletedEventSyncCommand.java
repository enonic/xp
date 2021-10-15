package com.enonic.xp.core.impl.content;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentInheritType;
import com.enonic.xp.content.DeleteContentParams;

final class DeletedEventSyncCommand
    extends AbstractContentEventSyncCommand
{
    DeletedEventSyncCommand( final Builder builder )
    {
        super( builder );
    }

    public static Builder create()
    {
        return new Builder();
    }

    protected void doSync()
    {
        params.getContents().forEach( this::doSync );
    }

    private void doSync( final ContentToSync content )
    {
        content.getTargetContext().runWith( () -> {

            if ( isToSyncDelete( content.getTargetContent() ) )
            {
                if ( needToDelete( content ) )
                {
                    final DeleteContentParams deleteParams =
                        DeleteContentParams.create().contentPath( content.getTargetContent().getPath() ).deleteOnline( true ).build();

                    contentService.deleteWithoutFetch( deleteParams );
                }
            }
        } );
    }

    private boolean isToSyncDelete( final Content targetContent )
    {
        return targetContent.getInherit().contains( ContentInheritType.CONTENT );
    }

    private boolean needToDelete( final ContentToSync content )
    {
        return content.getSourceContext().callWith( () -> !contentService.contentExists( content.getTargetContent().getId() ) ) &&
            contentService.getDependencies( content.getTargetContent().getId() ).getInbound().isEmpty() &&
            !contentService.getById( content.getTargetContent().getId() ).hasChildren();
    }

    public static class Builder
        extends AbstractContentEventSyncCommand.Builder<Builder>
    {
        void validate()
        {
            super.validate();
            Preconditions.checkArgument( params.getContents().stream().allMatch( content -> content.getSourceContent() == null ),
                                         "sourceContent must be null." );
            Preconditions.checkArgument( params.getContents().stream().allMatch( content -> content.getTargetContent() != null ),
                                         "targetContent must be set." );
        }

        public DeletedEventSyncCommand build()
        {
            validate();
            return new DeletedEventSyncCommand( this );
        }
    }
}
