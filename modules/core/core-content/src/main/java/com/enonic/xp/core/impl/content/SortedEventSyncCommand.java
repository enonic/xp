package com.enonic.xp.core.impl.content;

import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentInheritType;
import com.enonic.xp.content.SortContentParams;

final class SortedEventSyncCommand
    extends AbstractContentEventSyncCommand
{
    SortedEventSyncCommand( final Builder builder )
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
            if ( isToSyncSort( content.getTargetContent() ) )
            {
                if ( needToSort( content.getSourceContent(), content.getTargetContent() ) )
                {
                    final SortContentParams sortParams = SortContentParams.create()
                        .childOrder( content.getSourceContent().getChildOrder() )
                        .contentId( content.getSourceContent().getId() )
                        .stopInherit( false )
                        .build();

                    contentService.sort( sortParams );
                }
            }
        } );
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
        public SortedEventSyncCommand build()
        {
            validate();
            return new SortedEventSyncCommand( this );
        }
    }
}
