package com.enonic.xp.core.impl.content;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Maps;

import com.enonic.xp.archive.ArchiveContentParams;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentIndexPath;
import com.enonic.xp.content.ContentInheritType;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.context.Context;
import com.enonic.xp.query.filter.BooleanFilter;
import com.enonic.xp.query.filter.IdFilter;

final class MovedEventSyncArchiver
    extends MovedEventSyncSynchronizer
{
    private MovedEventSyncArchiver( final Builder builder )
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
        getRoots().forEach( content -> content.getTargetCtx()
            .runWith( () -> layersContentService.archive(
                ArchiveContentParams.create().contentId( content.getTargetContent().getId() ).build() ) ) );
    }

    private Set<ContentToSync> getRoots()
    {
        final List<ContentToSync> contentsToSync =
            contents.stream().filter( content -> isToSyncContent( content.getTargetContent() ) ).collect( Collectors.toList() );

        final Map<Content, Context> allArchived = getAllArchived( contentsToSync );
        final Set<ContentId> allArchivedIds = allArchived.keySet().stream().map( Content::getId ).collect( Collectors.toSet() );

        final Set<Content> toExclude = allArchived.entrySet()
            .stream()
            .filter( entry -> !( entry.getKey().getInherit().contains( ContentInheritType.CONTENT ) && allArchivedIds.containsAll(
                entry.getValue().callWith( () -> getInboundDependencies( entry.getKey().getId() ) ).getSet() ) ) )
            .map( Map.Entry::getKey )
            .collect( Collectors.toSet() );

        final Set<Content> toArchive = contentsToSync.stream()
            .map( ContentToSync::getTargetContent )
            .filter( content -> !toExclude.contains( content ) )
            .filter( content -> toExclude.stream().noneMatch( exclude -> exclude.getPath().isChildOf( content.getPath() ) ) )
            .collect( Collectors.toSet() );

        final Set<ContentPath> paths = toArchive.stream().map( Content::getPath ).collect( Collectors.toSet() );

        final Set<Content> rootContents = toArchive.stream()
            .filter( content -> paths.stream().noneMatch( path -> content.getPath().isChildOf( path ) ) )
            .collect( Collectors.toSet() );

        return contentsToSync.stream()
            .filter( content -> rootContents.contains( content.getTargetContent() ) )
            .collect( Collectors.toSet() );
    }

    private ContentIds getInboundDependencies( final ContentId contentId )
    {
        final ContentQuery query = ContentQuery.create()
            .queryFilter( BooleanFilter.create()
                              .must( IdFilter.create()
                                         .fieldName( ContentIndexPath.REFERENCES.getPath() )
                                         .value( contentId.toString() )
                                         .build() )
                              .mustNot( IdFilter.create().fieldName( ContentIndexPath.ID.getPath() ).value( contentId.toString() ).build() )
                              .build() )
            .size( -1 )
            .build();
        return this.layersContentService.find( query ).getContentIds();
    }

    private Map<Content, Context> getAllArchived( final List<ContentToSync> contentToSync )
    {
        final Stream<Map.Entry<Content, Context>> parentStream =
            contentToSync.stream().map( c -> Maps.immutableEntry( c.getTargetContent(), c.getTargetCtx() ) );

        final Stream<Map.Entry<Content, Context>> childrenStream = contentToSync.stream()
            .flatMap( c -> c.getTargetCtx()
                .callWith( () -> layersContentService.getByIds( layersContentService.findAllByParent( c.getTargetContent().getPath() ) )
                    .stream()
                    .map( child -> Maps.immutableEntry( child, c.getTargetCtx() ) ) ) );

        return Stream.concat( parentStream, childrenStream )
            .collect( Collectors.toMap( Map.Entry::getKey, Map.Entry::getValue, ( a, _ ) -> a ) );
    }

    static class Builder
        extends MovedEventSyncSynchronizer.Builder<Builder>
    {
        @Override
        MovedEventSyncArchiver build()
        {
            return new MovedEventSyncArchiver( this );
        }
    }
}
