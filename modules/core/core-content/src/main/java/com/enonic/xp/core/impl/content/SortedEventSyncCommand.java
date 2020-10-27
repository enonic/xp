package com.enonic.xp.core.impl.content;

import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentInheritType;
import com.enonic.xp.content.SetContentChildOrderParams;

final class SortedEventSyncCommand
    extends AbstractContentEventSyncCommand
{
    public SortedEventSyncCommand( final Builder builder )
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
        if ( isToSyncSort( params.getTargetContent() ) )
        {
            if ( needToSort( params.getSourceContent(), params.getTargetContent() ) )
            {
                final SetContentChildOrderParams sortParams = SetContentChildOrderParams.create().
                    childOrder( params.getSourceContent().getChildOrder() ).
                    contentId( params.getSourceContent().getId() ).
                    stopInherit( false ).
                    build();

                contentService.setChildOrder( sortParams );
            }
        }
    }

    private boolean isToSyncSort( final Content targetContent )
    {
        return targetContent.getInherit().contains( ContentInheritType.SORT );
    }

    private boolean needToSort( final Content sourceContent, final Content targetContent )
    {
        return !Objects.equals( sourceContent.getChildOrder(), targetContent.getChildOrder() );
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

        public SortedEventSyncCommand build()
        {
            validate();
            return new SortedEventSyncCommand( this );
        }
    }
}
