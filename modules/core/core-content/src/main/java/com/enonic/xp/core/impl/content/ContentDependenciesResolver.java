package com.enonic.xp.core.impl.content;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.enonic.xp.aggregation.BucketAggregation;
import com.enonic.xp.content.ContentDependencies;
import com.enonic.xp.content.ContentDependenciesAggregation;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIndexPath;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.FindContentIdsByQueryResult;
import com.enonic.xp.content.GetContentByIdsParams;
import com.enonic.xp.query.aggregation.TermsAggregationQuery;
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
        final Collection<ContentDependenciesAggregation> inbound = this.resolveInboundDependenciesAggregation( contentId );

        final Collection<ContentDependenciesAggregation> outbound = this.resolveOutboundDependenciesAggregation( contentId );

        return ContentDependencies.create().inboundDependencies( inbound ).outboundDependencies( outbound ).build();
    }

    private Collection<ContentDependenciesAggregation> resolveInboundDependenciesAggregation( final ContentId contentId )
    {
        if ( contentId == null )
        {
            return new HashSet<>();
        }

        final FindContentIdsByQueryResult result = this.contentService.find( ContentQuery.create().
            queryFilter( BooleanFilter.create().
                must( IdFilter.create().
                    fieldName( ContentIndexPath.REFERENCES.getPath() ).
                    value( contentId.toString() ).
                    build() ).
                mustNot( IdFilter.create().
                    fieldName( ContentIndexPath.ID.getPath() ).
                    value( contentId.toString() ).
                    build() ).
                build() ).
            aggregationQuery( TermsAggregationQuery.create( "type" ).
                fieldName( "type" ).
                orderDirection( TermsAggregationQuery.Direction.DESC ).
                build() ).
            build() );

        final BucketAggregation bucketAggregation = (BucketAggregation) result.getAggregations().get( "type" );

        return bucketAggregation.getBuckets().getSet().stream().map( ContentDependenciesAggregation::new ).collect( toList() );
    }

    private Collection<ContentDependenciesAggregation> resolveOutboundDependenciesAggregation( final ContentId contentId )
    {
        final Map<ContentTypeName, Long> aggregationJsonMap = new HashMap<>();

        final Contents contents =
            this.contentService.getByIds( new GetContentByIdsParams( this.contentService.getOutboundDependencies( contentId ) ) );

        contents.forEach( existingContent -> {
            final ContentTypeName contentTypeName = existingContent.getType();
            final Long count = aggregationJsonMap.containsKey( contentTypeName ) ? aggregationJsonMap.get( contentTypeName ) + 1 : 1;
            aggregationJsonMap.put( contentTypeName, count );
        } );

        return aggregationJsonMap.entrySet().
            stream().
            map( entry -> new ContentDependenciesAggregation( entry.getKey(), entry.getValue() ) ).
            collect( toList() );
    }

}
