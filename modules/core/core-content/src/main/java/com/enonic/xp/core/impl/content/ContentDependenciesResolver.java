package com.enonic.xp.core.impl.content;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.codehaus.jparsec.util.Lists;

import com.google.common.collect.Maps;

import com.enonic.xp.aggregation.BucketAggregation;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentDependencies;
import com.enonic.xp.content.ContentDependenciesAggregation;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.FindContentIdsByQueryResult;
import com.enonic.xp.content.GetContentByIdsParams;
import com.enonic.xp.core.impl.content.serializer.PageDataSerializer;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.query.aggregation.TermsAggregationQuery;
import com.enonic.xp.query.parser.QueryParser;
import com.enonic.xp.schema.content.ContentTypeName;

public class ContentDependenciesResolver
{
    private static final PageDataSerializer PAGE_SERIALIZER = new PageDataSerializer( ContentPropertyNames.PAGE );

    final ContentService contentService;

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
            queryExpr( QueryParser.parse( "_references = '" + contentId.toString() + "'" ) ).
            aggregationQuery( TermsAggregationQuery.create( "type" ).
                fieldName( "type" ).
                orderDirection( TermsAggregationQuery.Direction.DESC ).
                build() ).
            build() );

        final BucketAggregation bucketAggregation = (BucketAggregation) result.getAggregations().get( "type" );

        return bucketAggregation.getBuckets().getSet().stream().map( bucket -> new ContentDependenciesAggregation( bucket ) ).collect(
            Collectors.toList() );
    }

    private Collection<ContentDependenciesAggregation> resolveOutboundDependenciesAggregation( final ContentId contentId )
    {

        final Content content = this.contentService.getById( contentId );

        Map<ContentTypeName, Long> aggregationJsonMap = Maps.newHashMap();

        final List<ContentId> contentIds = Lists.arrayList();

        final PropertySet contentPageData = new PropertyTree().getRoot();
        if ( content.getPage() != null )
        {
            PAGE_SERIALIZER.toData( content.getPage(), contentPageData );
        }

        Stream.concat( content.getData().getProperties( ValueTypes.REFERENCE ).stream(),
                       contentPageData.getProperties( ValueTypes.REFERENCE ).stream() ).forEach( property -> {
            if ( !contentId.toString().equals( property.getValue().toString() ) )
            {
                contentIds.add( ContentId.from( property.getValue().toString() ) );
            }
        } );

        final Contents contents = this.contentService.getByIds( new GetContentByIdsParams( ContentIds.from( contentIds ) ) );

        contents.forEach( existingContent -> {
            final ContentTypeName contentTypeName = existingContent.getType();
            final Long count = aggregationJsonMap.containsKey( contentTypeName ) ? aggregationJsonMap.get( contentTypeName ) + 1 : 1;
            aggregationJsonMap.put( contentTypeName, count );
        } );

        return aggregationJsonMap.entrySet().
            stream().
            map( entry -> new ContentDependenciesAggregation( entry.getKey(), entry.getValue() ) ).
            collect( Collectors.toList() );
    }

}
