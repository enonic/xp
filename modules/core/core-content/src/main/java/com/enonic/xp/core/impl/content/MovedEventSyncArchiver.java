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
import com.enonic.xp.content.FindContentByParentParams;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
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
        getRoots().forEach( content -> content.getTargetContext()
            .runWith( () -> contentService.archive(
                ArchiveContentParams.create().contentId( content.getTargetContent().getId() ).stopInherit( false ).build() ) ) );

    }

    private Set<ContentToSync> getRoots()
    {
        final List<ContentToSync> contentsToSync =
            contents.stream().filter( content -> isToSyncContent( content.getTargetContent() ) ).collect( Collectors.toList() );

        final Map<Content, Context> allArchived = getAllArchived( contentsToSync );
        final Set<ContentId> allArchivedIds = allArchived.keySet().stream().map( Content::getId ).collect( Collectors.toSet() );

        final Set<Content> toExclude = allArchived.entrySet()
            .stream()
            .filter( entry -> !( entry.getKey().getInherit().contains( ContentInheritType.CONTENT ) &&
                allArchivedIds.containsAll( getInboundDependencies( entry ).getSet() ) ) )
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

    private ContentIds getInboundDependencies( Map.Entry<Content, Context> entry )
    {
        return entry.getValue()
            .callWith( () -> this.contentService.find( ContentQuery.create()
                                                           .queryFilter( BooleanFilter.create()
                                                                             .must( IdFilter.create()
                                                                                        .fieldName( ContentIndexPath.REFERENCES.getPath() )
                                                                                        .value( entry.getKey().getId().toString() )
                                                                                        .build() )
                                                                             .mustNot( IdFilter.create()
                                                                                           .fieldName( ContentIndexPath.ID.getPath() )
                                                                                           .value( entry.getKey().getId().toString() )
                                                                                           .build() )
                                                                             .build() )
                                                           .size( -1 )
                                                           .build() ).getContentIds() );
    }

    private Map<Content, Context> getAllArchived( final List<ContentToSync> contentToSync )
    {
        final Stream<Map.Entry<Content, Context>> childrenStream = contentToSync.stream()
            .map( content -> content.getTargetContext()
                .callWith( () -> this.contentService.findByParent(
                        FindContentByParentParams.create().parentId( content.getSourceContent().getId() ).recursive( true ).size( -1 ).build() )
                    .getContents()
                    .stream()
                    .map( child -> Maps.immutableEntry( child, ContextAccessor.current() ) ) ) )
            .flatMap( s -> s );

        final Stream<Map.Entry<Content, Context>> parentStream =
            contentToSync.stream().map( c -> Maps.immutableEntry( c.getTargetContent(), c.getTargetContext() ) );

        return Stream.concat( childrenStream, parentStream ).collect( Collectors.toMap( Map.Entry::getKey, Map.Entry::getValue ) );
    }

    static class Builder
        extends MovedEventSyncSynchronizer.Builder<Builder>
    {
        @Override
        MovedEventSyncArchiver build()
        {
            validate();
            return new MovedEventSyncArchiver( this );
        }
    }
}
