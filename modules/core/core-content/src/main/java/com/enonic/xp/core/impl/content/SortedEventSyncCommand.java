package com.enonic.xp.core.impl.content;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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
        contentToSync.forEach( this::doSync );
    }

    private void doSync( final ContentToSync content )
    {
        final Content targetContent = content.getTargetContent();
        final Content sourceContent = content.getSourceContent();

        content.getTargetCtx().runWith( () -> {
            if ( isToSyncSort( targetContent ) )
            {
                if ( needToSort( sourceContent, targetContent ) )
                {
                    final SortContentParams sortParams =
                        SortContentParams.create().childOrder( sourceContent.getChildOrder() ).contentId( sourceContent.getId() ).build();

                    layersContentService.sort( sortParams );
                }
                if ( sourceContent.getChildOrder().isManualOrder() )
                {
                    final List<ContentToSync> childrenToSync =
                        layersContentService.getByIds( layersContentService.findAllChildren( targetContent.getPath() ) )
                            .stream()
                            .map( childTargetContent -> content.getSourceCtx()
                                .callWith( () -> layersContentService.getById( childTargetContent.getId() ) )
                                .map( childSourceContent -> ContentToSync.create()
                                    .sourceCtx( content.getSourceCtx() )
                                    .targetCtx( content.getTargetCtx() )
                                    .sourceContent( childSourceContent )
                                    .targetContent( childTargetContent )
                                    .build() ) )
                            .filter( Optional::isPresent )
                            .map( Optional::get )
                            .collect( Collectors.toList() );

                    if ( !childrenToSync.isEmpty() )
                    {
                        UpdatedEventSyncCommand.create()
                            .contentService( layersContentService )
                            .contentToSync( childrenToSync )
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
        public SortedEventSyncCommand build()
        {
            validate();
            return new SortedEventSyncCommand( this );
        }
    }
}
