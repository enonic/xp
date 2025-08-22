package com.enonic.xp.core.impl.content;

import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentInheritType;
import com.enonic.xp.content.UpdateContentParams;

final class ManualOrderUpdatedEventSyncCommand
    extends AbstractContentEventSyncCommand
{
    ManualOrderUpdatedEventSyncCommand( final Builder builder )
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
        params.getContents().forEach( this::doSync );
    }

    private void doSync( final ContentToSync content )
    {
        content.getTargetContext().runWith( () -> {

            if ( needToUpdateManualOrderValue( content ) )
            {
                if ( isToSyncManualOrderUpdated( content ) )
                {
                    contentService.update( updateManualOrderValueParams( content.getSourceContent() ) );
                }
            }
        } );
    }

    private boolean needToUpdateManualOrderValue( final ContentToSync content )
    {
        return !Objects.equals( content.getTargetContent().getManualOrderValue(), content.getSourceContent().getManualOrderValue() );
    }

    private boolean isToSyncManualOrderUpdated( final ContentToSync content )
    {
        if ( contentService.contentExists( content.getTargetContent().getParentPath() ) )
        {
            final Content targetParent = contentService.getByPath( content.getTargetContent().getParentPath() );
            if ( targetParent.getChildOrder().isManualOrder() )
            {
                final Content sourceParent =
                    content.getSourceContext().callWith( () -> contentService.getByPath( content.getSourceContent().getParentPath() ) );

                return targetParent.getId().equals( sourceParent.getId() ) && targetParent.getInherit().contains( ContentInheritType.SORT );
            }
        }
        return false;
    }

    private UpdateContentParams updateManualOrderValueParams( final Content source )
    {
        return new UpdateContentParams().contentId( source.getId() )
            .requireValid( false )
            .stopInherit( false )
            .editor( edit -> edit.manualOrderValue = source.getManualOrderValue() );
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
        public ManualOrderUpdatedEventSyncCommand build()
        {
            validate();
            return new ManualOrderUpdatedEventSyncCommand( this );
        }
    }
}
