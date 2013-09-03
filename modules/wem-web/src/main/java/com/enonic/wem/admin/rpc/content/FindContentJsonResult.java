package com.enonic.wem.admin.rpc.content;

import java.util.Set;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.admin.json.JsonResult;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.query.ContentIndexQueryResult;
import com.enonic.wem.api.content.query.ContentQueryHit;
import com.enonic.wem.api.facet.DateHistogramFacet;
import com.enonic.wem.api.facet.DateHistogramFacetEntry;
import com.enonic.wem.api.facet.Facet;
import com.enonic.wem.api.facet.Facets;
import com.enonic.wem.api.facet.QueryFacet;
import com.enonic.wem.api.facet.RangeFacet;
import com.enonic.wem.api.facet.RangeFacetEntry;
import com.enonic.wem.api.facet.TermsFacet;
import com.enonic.wem.api.facet.TermsFacetEntry;

public class FindContentJsonResult
    extends JsonResult
{

    // This is temporary until 18/4. The findContent should not render content json
    private final Contents contents;

    private final ContentIndexQueryResult contentIndexQueryResult;

    FindContentJsonResult( final Contents contents, final ContentIndexQueryResult contentIndexQueryResult )
    {
        this.contentIndexQueryResult = contentIndexQueryResult;

        // This is temporary until 18/4. The findContent should not render content json
        this.contents = contents;
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "success", true );
        json.put( "total", contentIndexQueryResult.getContentIds().size() );
        json.put( "contents", serialize( contentIndexQueryResult.getContentQueryHits() ) );

        final Facets facets = contentIndexQueryResult.getFacets();

        if ( facets != null )
        {
            final ArrayNode facetsNode = json.putArray( "facets" );

            for ( Facet facet : facets )
            {
                if ( facet instanceof TermsFacet )
                {
                    serializeFacet( facetsNode.addObject(), (TermsFacet) facet );
                }
                else if ( facet instanceof DateHistogramFacet )
                {
                    serializeFacet( facetsNode.addObject(), (DateHistogramFacet) facet );
                }
                else if ( facet instanceof RangeFacet )
                {
                    serializeFacet( facetsNode.addObject(), (RangeFacet) facet );
                }
                else if ( facet instanceof QueryFacet )
                {
                    serializeFacet( facetsNode.addObject(), (QueryFacet) facet );
                }
            }

        }
    }

    private void serializeFacet( final ObjectNode json, final DateHistogramFacet dateHistogramFacet )
    {
        json.put( "name", dateHistogramFacet.getName() );
        json.put( "_type", "dateHistogram" );

        final ArrayNode terms = json.putArray( "terms" );

        final Set<DateHistogramFacetEntry> resultEntries = dateHistogramFacet.getResultEntries();

        for ( DateHistogramFacetEntry entry : resultEntries )
        {
            ObjectNode facetObject = terms.addObject();
            facetObject.put( "time", entry.getTime() );
            facetObject.put( "count", entry.getCount() );

        }
    }

    private void serializeFacet( final ObjectNode json, final RangeFacet rangeFacet )
    {
        json.put( "name", rangeFacet.getName() );
        json.put( "_type", "range" );

        final ArrayNode terms = json.putArray( "ranges" );

        final Set<RangeFacetEntry> resultEntries = rangeFacet.getResultEntries();

        for ( RangeFacetEntry entry : resultEntries )
        {
            ObjectNode facetObject = terms.addObject();
            facetObject.put( "from", entry.getFrom() );
            facetObject.put( "to", entry.getTo() );
            facetObject.put( "total_count", entry.getCount() );
        }
    }


    private void serializeFacet( final ObjectNode json, final TermsFacet termsFacet )
    {
        final String facetName = termsFacet.getName();
        json.put( "name", facetName );
        json.put( "_type", "terms" );

        final ArrayNode terms = json.putArray( "terms" );

        final Set<TermsFacetEntry> results = termsFacet.getResults();

        for ( TermsFacetEntry result : results )
        {
            ObjectNode facetObject = terms.addObject();
            facetObject.put( "name", result.getTerm() );
            facetObject.put( "displayName", result.getDisplayName() );
            facetObject.put( "count", result.getCount() );
        }
    }

    private void serializeFacet( final ObjectNode json, final QueryFacet queryFacet )
    {
        json.put( "name", queryFacet.getName() );
        json.put( "_type", "query" );
        json.put( "count", queryFacet.getCount() );
    }

    private JsonNode serialize( final Set<ContentQueryHit> contentQueryHits )
    {
        final ArrayNode contentsNode = arrayNode();

        int i = 1;
        for ( ContentQueryHit contentQueryHit : contentQueryHits )
        {
            final ObjectNode contentJson = contentsNode.addObject();
            contentJson.put( "order", i++ );
            contentJson.put( "score", contentQueryHit.getScore() );

            // This is temporary until 18/4. The findContent should not render content json
            addContentData( contentQueryHit, contentJson );
        }
        return contentsNode;
    }

    private void addContentData( final ContentQueryHit contentQueryHit, final ObjectNode contentJson )
    {
        final Content content = findContent( contentQueryHit.getContentId() );
        if ( content != null )
        {
            ContentJsonTemplate.forContentListing( contentJson, content );
        }
    }

    private Content findContent( ContentId contentId )
    {
        for ( Content content : contents.getList() )
        {
            if ( content.getId().equals( contentId ) )
            {
                return content;
            }
        }

        return null;
    }

}
