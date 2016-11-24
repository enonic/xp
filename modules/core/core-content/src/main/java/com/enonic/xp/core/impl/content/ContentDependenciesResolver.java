package com.enonic.xp.core.impl.content;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.Maps;

import com.enonic.xp.aggregation.BucketAggregation;
import com.enonic.xp.content.ContentDependencies;
import com.enonic.xp.content.ContentDependenciesAggregation;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.FindContentIdsByQueryResult;
import com.enonic.xp.content.GetContentByIdsParams;
import com.enonic.xp.query.aggregation.TermsAggregationQuery;
import com.enonic.xp.query.parser.QueryParser;
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
        final FindContentIdsByQueryResult result = this.contentService.find( ContentQuery.create().
            queryExpr( QueryParser.parse( "_references = '" + contentId.toString() + "' AND _id != '" + contentId.toString() + "'" ) ).
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
        final Map<ContentTypeName, Long> aggregationJsonMap = Maps.newHashMap();

        final Contents contents = this.contentService.getByIds(
            new GetContentByIdsParams( this.contentService.getOutboundDependencies( contentId ) ) );

        contents.forEach(existingContent -> {
            final ContentTypeName contentTypeName = existingContent.getType();
            final Long count = aggregationJsonMap.containsKey(contentTypeName) ? aggregationJsonMap.get(contentTypeName) + 1 : 1;
            aggregationJsonMap.put(contentTypeName, count);
        });

        return aggregationJsonMap.entrySet().
            stream().
            map( entry -> new ContentDependenciesAggregation( entry.getKey(), entry.getValue() ) ).
            collect( toList() );
    }

}
