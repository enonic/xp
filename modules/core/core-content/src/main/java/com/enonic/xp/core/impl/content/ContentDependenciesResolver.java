package com.enonic.xp.core.impl.content;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.enonic.xp.content.ContentDependencies;
import com.enonic.xp.content.ContentDependenciesAggregation;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentIndexPath;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.GetContentByIdsParams;
import com.enonic.xp.query.filter.BooleanFilter;
import com.enonic.xp.query.filter.IdFilter;
import com.enonic.xp.schema.content.ContentTypeName;

import static java.util.stream.Collectors.toList;

public class ContentDependenciesResolver
{
    private final ContentService contentService;

    public ContentDependenciesResolver( final ContentService contentService )
    {
        this.contentService = contentService;
    }

    public ContentDependencies resolve( final ContentId contentId )
    {
        final List<ContentDependenciesAggregation> inbound = this.resolveInboundDependenciesAggregation( contentId );

        final List<ContentDependenciesAggregation> outbound = this.resolveOutboundDependenciesAggregation( contentId );

        return ContentDependencies.create().inboundDependencies( inbound ).outboundDependencies( outbound ).build();
    }

    private List<ContentDependenciesAggregation> resolveInboundDependenciesAggregation( final ContentId contentId )
    {
        if ( contentId == null )
        {
            return List.of();
        }

        final ContentIds referencesIds = this.contentService.find( ContentQuery.create()
                                                                       .queryFilter( BooleanFilter.create()
                                                                                         .must( IdFilter.create()
                                                                                                    .fieldName(
                                                                                                        ContentIndexPath.REFERENCES.getPath() )
                                                                                                    .value( contentId.toString() )
                                                                                                    .build() )
                                                                                         .mustNot( IdFilter.create()
                                                                                                       .fieldName(
                                                                                                           ContentIndexPath.ID.getPath() )
                                                                                                       .value( contentId.toString() )
                                                                                                       .build() )
                                                                                         .build() )
                                                                       .size( -1 )
                                                                       .build() ).getContentIds();

        final Contents references = this.contentService.getByIds( new GetContentByIdsParams( referencesIds ) );

        return buildAggregations( references );

    }

    private List<ContentDependenciesAggregation> resolveOutboundDependenciesAggregation( final ContentId contentId )
    {
        final Contents dependencies =
            this.contentService.getByIds( new GetContentByIdsParams( this.contentService.getOutboundDependencies( contentId ) ) );

        return buildAggregations( dependencies );
    }

    private List<ContentDependenciesAggregation> buildAggregations( final Contents dependencies )
    {
        final Map<ContentTypeName, ContentDependenciesAggregation.Builder> aggregationJsonMap = new HashMap<>();

        dependencies.forEach( existingContent -> {
            final ContentTypeName contentTypeName = existingContent.getType();

            final ContentDependenciesAggregation.Builder aggregation = aggregationJsonMap.computeIfAbsent( contentTypeName, key -> {
                final ContentDependenciesAggregation.Builder builder = ContentDependenciesAggregation.create();
                builder.type( key );

                return builder;
            } );
            aggregation.addContentId( existingContent.getId() );
        } );

        return aggregationJsonMap.values().stream().map( ContentDependenciesAggregation.Builder::build ).collect( toList() );
    }

}
