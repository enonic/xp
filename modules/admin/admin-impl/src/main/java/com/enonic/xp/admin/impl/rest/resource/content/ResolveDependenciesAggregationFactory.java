package com.enonic.xp.admin.impl.rest.resource.content;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.Maps;

import com.enonic.xp.admin.impl.json.content.DependenciesAggregationJson;
import com.enonic.xp.admin.impl.json.content.DependenciesJson;
import com.enonic.xp.admin.impl.rest.resource.schema.content.ContentTypeIconUrlResolver;
import com.enonic.xp.aggregation.BucketAggregation;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.FindContentByQueryParams;
import com.enonic.xp.content.FindContentByQueryResult;
import com.enonic.xp.content.ResolveDependenciesAggregationResult;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.query.aggregation.TermsAggregationQuery;
import com.enonic.xp.query.parser.QueryParser;
import com.enonic.xp.schema.content.ContentTypeName;

public class ResolveDependenciesAggregationFactory
{

    final ContentTypeIconUrlResolver contentTypeIconUrlResolver;

    final ContentService contentService;

    public ResolveDependenciesAggregationFactory(final ContentTypeIconUrlResolver contentTypeIconUrlResolver, final ContentService contentService) {
        this.contentTypeIconUrlResolver = contentTypeIconUrlResolver;
        this.contentService = contentService;
    }

    public DependenciesJson create( final ContentId contentId)
    {
        final List<DependenciesAggregationJson> inbound = this.resolveInboundDependenciesAggregation( contentId ).stream().
            map( aggregation -> {
                return new DependenciesAggregationJson( aggregation, this.contentTypeIconUrlResolver);
            } ).collect( Collectors.toList() );

        final List<DependenciesAggregationJson> outbound = this.resolveOutboundDependenciesAggregation( contentId ).stream().
            map( aggregation -> {
                return new DependenciesAggregationJson( aggregation, this.contentTypeIconUrlResolver );
            } ).collect( Collectors.toList() );

        return new DependenciesJson( inbound, outbound );
    }

    private Collection<ResolveDependenciesAggregationResult> resolveInboundDependenciesAggregation( final ContentId contentId ) {

        final FindContentByQueryResult result = this.contentService.find( FindContentByQueryParams.create().
            contentQuery( ContentQuery.create().
                queryExpr( QueryParser.parse( "_references = '" + contentId.toString() + "'" ) ).
                aggregationQuery( TermsAggregationQuery.create( "type" ).
                    fieldName( "type" ).
                    orderDirection( TermsAggregationQuery.Direction.DESC ).
                    build() ).
                build() ).
            build() );

        final BucketAggregation bucketAggregation = (BucketAggregation) result.getAggregations().get( "type" );

        return bucketAggregation.getBuckets().getSet().stream().map( bucket -> {
            return new ResolveDependenciesAggregationResult( bucket );
        } ).collect( Collectors.toList() );
    }

    private Collection<ResolveDependenciesAggregationResult> resolveOutboundDependenciesAggregation(final ContentId contentId) {

        final Content content = this.contentService.getById( contentId );
        content.getData().getProperties( ValueTypes.REFERENCE );

        Map<ContentTypeName, ResolveDependenciesAggregationResult> aggregationJsonMap = Maps.newHashMap();

        content.getData().getProperties( ValueTypes.REFERENCE ).forEach( property -> {
            final ContentId curContentID = ContentId.from( property.getValue().toString() );
            final Content curContent = this.contentService.getById( curContentID );

            if(curContent != null)
            {
                final ContentTypeName contentTypeName = curContent.getType();
                if ( aggregationJsonMap.containsKey(contentTypeName) ) {
                    aggregationJsonMap.get( contentTypeName ).increaseCount();
                } else {
                    aggregationJsonMap.put( contentTypeName, new ResolveDependenciesAggregationResult( contentTypeName.toString(), 1l ) );
                }
            }
        });
        return aggregationJsonMap.values();
    }

}
