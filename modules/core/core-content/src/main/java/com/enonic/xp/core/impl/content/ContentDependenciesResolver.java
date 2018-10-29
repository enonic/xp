package com.enonic.xp.core.impl.content;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

import com.enonic.xp.content.Content;
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

        final Contents contents = this.contentService.getByIds( new GetContentByIdsParams( result.getContentIds() ) );

        return makeContentDependenciesAggregations( contents );
    }

    private Collection<ContentDependenciesAggregation> resolveOutboundDependenciesAggregation( final ContentId contentId )
    {
        final Contents contents =
            this.contentService.getByIds( new GetContentByIdsParams( this.contentService.getOutboundDependencies( contentId ) ) );

        return makeContentDependenciesAggregations( contents );
    }

    private Collection<ContentDependenciesAggregation> makeContentDependenciesAggregations( final Contents contents )
    {
        final Map<ContentTypeName, List<Content>> aggregationJsonMap = Maps.newHashMap();

        contents.forEach( existingContent -> {
            final ContentTypeName contentTypeName = existingContent.getType();
            if ( aggregationJsonMap.containsKey( contentTypeName ) )
            {
                aggregationJsonMap.get( contentTypeName ).add( existingContent );
            }
            else
            {
                final List<Content> list = new ArrayList<>();
                list.add( existingContent );
                aggregationJsonMap.put( contentTypeName, list );
            }
        } );

        return aggregationJsonMap.entrySet().
            stream().
            map( entry -> new ContentDependenciesAggregation( entry.getKey(), Contents.from( entry.getValue() ) ) ).
            collect( toList() );
    }

}
