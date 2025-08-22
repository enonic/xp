package com.enonic.xp.core.impl.content;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentInheritType;
import com.enonic.xp.content.FindContentByParentParams;
import com.enonic.xp.content.SetContentChildOrderParams;

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
                    final SetContentChildOrderParams sortParams = SetContentChildOrderParams.create()
                        .childOrder( content.getSourceContent().getChildOrder() )
                        .contentId( content.getSourceContent().getId() )
                        .stopInherit( false )
                        .build();

                    contentService.setChildOrder( sortParams );
                }
                if ( needToReorderChildren( content.getSourceContent() ) )
                {
                    final List<ContentToSync> childrenToSync = contentService.findByParent(
                        FindContentByParentParams.create().parentId( content.getTargetContent().getId() ).size( -1 ).build() )
                        .getContents()
                        .stream()
                        .map( currContent -> {
                            final Content sourceContent = content.getSourceContext()
                                .callWith( () -> contentService.contentExists( currContent.getId() ) ? contentService.getById(
                                    currContent.getId() ) : null );

                            return sourceContent != null ? ContentToSync.create()
                                .sourceContext( content.getSourceContext() )
                                .targetContext( content.getTargetContext() )
                                .sourceContent( sourceContent )
                                .targetContent( currContent )
                                .build() : null;
                        } )
                        .filter( Objects::nonNull )
                        .collect( Collectors.toList() );

                    if ( !childrenToSync.isEmpty() )
                    {
                        ManualOrderUpdatedEventSyncCommand.create()
                            .contentService( contentService )
                            .params( ContentEventSyncCommandParams.create().addContents( childrenToSync ).build() )
                            .build()
                            .sync();
                    }
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

    private boolean needToReorderChildren( final Content sourceContent )
    {
        return sourceContent.getChildOrder().isManualOrder();
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
