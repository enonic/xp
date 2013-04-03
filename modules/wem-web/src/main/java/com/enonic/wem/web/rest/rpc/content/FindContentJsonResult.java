package com.enonic.wem.web.rest.rpc.content;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.google.common.collect.Lists;

import com.enonic.wem.api.content.query.ContentIndexQueryResult;
import com.enonic.wem.api.content.query.ContentQueryHit;
import com.enonic.wem.api.query.DateHistogramFacetResultEntry;
import com.enonic.wem.api.query.DateHistogramFacetResultSet;
import com.enonic.wem.api.query.FacetResultSet;
import com.enonic.wem.api.query.FacetsResultSet;
import com.enonic.wem.api.query.QueryFacetResultSet;
import com.enonic.wem.api.query.RangeFacetResultEntry;
import com.enonic.wem.api.query.RangeFacetResultSet;
import com.enonic.wem.api.query.TermsFacetResultSet;
import com.enonic.wem.web.json.JsonResult;

public class FindContentJsonResult
    extends JsonResult
{
    private final ContentIndexQueryResult contentIndexQueryResult;

    FindContentJsonResult( final ContentIndexQueryResult contentIndexQueryResult )
    {
        this.contentIndexQueryResult = contentIndexQueryResult;
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "success", true );
        json.put( "total", contentIndexQueryResult.getContentIds().size() );
        json.put( "contents", serialize( contentIndexQueryResult.getContentQueryHits() ) );

        final FacetsResultSet facetsResultSet = contentIndexQueryResult.getFacetsResultSet();

        if ( facetsResultSet != null )
        {
            final ArrayNode facets = json.putArray( "facets" );

            final List<QueryFacetResultSet> queries = Lists.newArrayList();

            for ( FacetResultSet facetResultSet : facetsResultSet )
            {
                if ( facetResultSet instanceof TermsFacetResultSet )
                {
                    serializeFacet( facets.addObject(), (TermsFacetResultSet) facetResultSet );
                }
                else if ( facetResultSet instanceof DateHistogramFacetResultSet )
                {
                    serializeFacet( facets.addObject(), (DateHistogramFacetResultSet) facetResultSet );
                }
                else if ( facetResultSet instanceof RangeFacetResultSet )
                {
                    serializeFacet( facets.addObject(), (RangeFacetResultSet) facetResultSet );
                }
                else if ( facetResultSet instanceof QueryFacetResultSet )
                {
                    queries.add( (QueryFacetResultSet) facetResultSet );
                }
            }

            serializeFacet( facets.addObject(), queries );
        }
    }

    private void serializeFacet( final ObjectNode json, final DateHistogramFacetResultSet dateHistogramFacetResultSet )
    {
        json.put( "name", dateHistogramFacetResultSet.getName() );
        json.put( "_type", "dateHistogram" );

        final ArrayNode terms = json.putArray( "terms" );

        final Set<DateHistogramFacetResultEntry> resultEntries = dateHistogramFacetResultSet.getResultEntries();

        for ( DateHistogramFacetResultEntry entry : resultEntries )
        {
            ObjectNode facetObject = terms.addObject();
            facetObject.put( "time", entry.getTime() );
            facetObject.put( "count", entry.getCount() );

        }
    }

    private void serializeFacet( final ObjectNode json, final RangeFacetResultSet rangeFacetResultSet )
    {
        json.put( "name", rangeFacetResultSet.getName() );
        json.put( "_type", "range" );

        final ArrayNode terms = json.putArray( "ranges" );

        final Set<RangeFacetResultEntry> resultEntries = rangeFacetResultSet.getResultEntries();

        for ( RangeFacetResultEntry entry : resultEntries )
        {
            ObjectNode facetObject = terms.addObject();
            facetObject.put( "from", entry.getFrom() );
            facetObject.put( "to", entry.getTo() );
            facetObject.put( "total_count", entry.getCount() );
        }
    }


    private void serializeFacet( final ObjectNode json, final TermsFacetResultSet termsFacetResultSet )
    {
        json.put( "name", termsFacetResultSet.getName() );
        json.put( "_type", "terms" );

        final ArrayNode terms = json.putArray( "terms" );

        final Map<String, Integer> results = termsFacetResultSet.getResults();

        for ( String term : results.keySet() )
        {
            ObjectNode facetObject = terms.addObject();
            facetObject.put( "name", term );
            facetObject.put( "count", results.get( term ) );
        }
    }

    private void serializeFacet( final ObjectNode json, final List<QueryFacetResultSet> queries )
    {
        json.put( "name", "ranges" );
        json.put( "_type", "terms" );

        final ArrayNode terms = json.putArray( "terms" );

        for ( final QueryFacetResultSet queryFacetResultSet : queries )
        {
            final ObjectNode facetObject = terms.addObject();
            facetObject.put( "name", queryFacetResultSet.getName() );
            facetObject.put( "_type", "query" );
            facetObject.put( "count", queryFacetResultSet.getCount() );
        }
    }

    private JsonNode serialize( final Set<ContentQueryHit> contentQueryHits )
    {
        final ArrayNode contentsNode = arrayNode();

        int i = 1;
        for ( ContentQueryHit contentQueryHit : contentQueryHits )
        {
            final ObjectNode contentJson = contentsNode.addObject();
            contentJson.put( "order", i++ );
            contentJson.put( "id", contentQueryHit.getContentId().toString() );
            contentJson.put( "score", contentQueryHit.getScore() );
        }
        return contentsNode;
    }


}
