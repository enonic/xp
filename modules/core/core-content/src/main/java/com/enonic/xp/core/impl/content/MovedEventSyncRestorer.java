package com.enonic.xp.core.impl.content;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.enonic.xp.archive.RestoreContentParams;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;

import static com.enonic.xp.content.ContentConstants.CONTENT_ROOT_PATH_ATTRIBUTE;

final class MovedEventSyncRestorer
    extends MovedEventSyncSynchronizer
{
    MovedEventSyncRestorer( final Builder builder )
    {
        super( builder );
    }

    static Builder create()
    {
        return new Builder();
    }

    @Override
    protected void execute()
    {
        final List<ContentToSync> contentToSync =
            contents.stream().filter( content -> isToSyncContent( content.getTargetContent() ) ).collect( Collectors.toList() );

        getRoots( contentToSync ).forEach( content -> {

            final Content sourceParent =
                content.getSourceContext().callWith( () -> contentService.getByPath( content.getSourceContent().getParentPath() ) );

            final Context targetContextToRestore = ContextBuilder.from( content.getTargetContext() )
                .attribute( CONTENT_ROOT_PATH_ATTRIBUTE, content.getSourceContext().getAttribute( CONTENT_ROOT_PATH_ATTRIBUTE ) )
                .build();

            final ContentPath targetParentPath = targetContextToRestore.callWith(
                () -> contentService.getByIdOptional( sourceParent.getId() ).map( Content::getPath ).orElse( ContentPath.ROOT ) );

            content.getTargetContext()
                .runWith( () -> contentService.restore( RestoreContentParams.create()
                                                            .contentId( content.getTargetContent().getId() )
                                                            .path( targetParentPath )
                                                            .stopInherit( false )
                                                            .build() ) );
        } );

    }

    private Set<ContentToSync> getRoots( final List<ContentToSync> contentToSync )
    {
        final Set<Content> contents = getToRestore( contentToSync );
        final Set<ContentPath> paths = contents.stream().map( Content::getPath ).collect( Collectors.toSet() );

        return contentToSync.stream()
            .filter( content -> paths.stream().noneMatch( path -> content.getTargetContent().getPath().isChildOf( path ) ) )
            .collect( Collectors.toSet() );
    }

    private Set<Content> getToRestore( final List<ContentToSync> contentToSync )
    {
        return contentToSync.stream().map( ContentToSync::getTargetContent ).collect( Collectors.toSet() );
    }

    static class Builder
        extends MovedEventSyncSynchronizer.Builder<Builder>
    {
        @Override
        MovedEventSyncRestorer build()
        {
            return new MovedEventSyncRestorer( this );
        }
    }
}
