package com.enonic.wem.web.rest.rpc.content;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.query.ContentIndexQueryResult;
import com.enonic.wem.api.query.DateHistogramFacetResultEntry;
import com.enonic.wem.api.query.DateHistogramFacetResultSet;
import com.enonic.wem.api.query.FacetResultSet;
import com.enonic.wem.api.query.FacetsResultSet;
import com.enonic.wem.api.query.TermsFacetResultSet;
import com.enonic.wem.web.json.JsonResult;

public class FindContentJsonResult
    extends JsonResult
{
    private final Contents contents;

    private final ContentIndexQueryResult contentIndexQueryResult;

    FindContentJsonResult( final Contents contents, final ContentIndexQueryResult contentIndexQueryResult )
    {
        this.contentIndexQueryResult = contentIndexQueryResult;
        this.contents = contents;
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "success", true );
        json.put( "total", contents.getSize() );
        json.put( "contents", serialize( contents.getList() ) );

        final FacetsResultSet facetsResultSet = contentIndexQueryResult.getFacetsResultSet();

        if ( facetsResultSet != null )
        {
            final ArrayNode facets = json.putArray( "facets" );

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
            }
        }
    }

    private void serializeFacet( final ObjectNode json, final DateHistogramFacetResultSet dateHistogramFacetResultSet )
    {
        json.put( "name", dateHistogramFacetResultSet.getName() );

        final ArrayNode terms = json.putArray( "terms" );

        final Set<DateHistogramFacetResultEntry> resultEntries = dateHistogramFacetResultSet.getResultEntries();

        for ( DateHistogramFacetResultEntry entry : resultEntries )
        {
            ObjectNode facetObject = terms.addObject();
            facetObject.put( "time", entry.getTime() );
            facetObject.put( "count", entry.getCount() );

        }
    }

    private void serializeFacet( final ObjectNode json, final TermsFacetResultSet termsFacetResultSet )
    {
        json.put( "name", termsFacetResultSet.getName() );

        final ArrayNode terms = json.putArray( "terms" );

        final Map<String, Integer> results = termsFacetResultSet.getResults();

        for ( String term : results.keySet() )
        {
            ObjectNode facetObject = terms.addObject();
            facetObject.put( "name", term );
            facetObject.put( "count", results.get( term ) );
        }
    }

    private JsonNode serialize( final List<Content> list )
    {
        final ArrayNode contentsNode = arrayNode();
        for ( Content content : list )
        {
            final ObjectNode contentJson = contentsNode.addObject();
            ContentJsonTemplate.forContentListing( contentJson, content );
        }
        return contentsNode;
    }


}
