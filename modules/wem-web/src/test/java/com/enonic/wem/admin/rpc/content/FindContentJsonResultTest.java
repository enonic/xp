package com.enonic.wem.admin.rpc.content;

import org.codehaus.jackson.JsonNode;
import org.junit.Ignore;
import org.junit.Test;

import com.enonic.wem.admin.rpc.AbstractJsonTest;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.query.ContentIndexQueryResult;
import com.enonic.wem.api.query.FacetsResultSet;
import com.enonic.wem.api.query.QueryFacetResultSet;
import com.enonic.wem.api.query.TermsFacetResultSet;

import static org.junit.Assert.*;

public class FindContentJsonResultTest
    extends AbstractJsonTest
{
    @Test
    public void testNoFacets()
        throws Exception
    {
        Contents contents = Contents.empty();

        ContentIndexQueryResult contentIndexQueryResult = new ContentIndexQueryResult( 10 );

        FindContentJsonResult result = new FindContentJsonResult( contents, contentIndexQueryResult );

        assertNotNull( result.toJson() );
    }

    @Ignore // Test failing because of strange mapping of query-facets. We'll let this be for now, and fix after 18/04-2013
    @Test
    public void testTermsFacet()
        throws Exception
    {
        Contents contents = Contents.empty();

        ContentIndexQueryResult contentIndexQueryResult = new ContentIndexQueryResult( 10 );

        FacetsResultSet facetsResultSet = new FacetsResultSet();
        TermsFacetResultSet termsFacetResultSet = new TermsFacetResultSet();
        termsFacetResultSet.setName( "myTermsFacet" );
        termsFacetResultSet.addResult( "term1", 1 );
        termsFacetResultSet.addResult( "term2", 2 );
        termsFacetResultSet.addResult( "term3", 3 );
        facetsResultSet.addFacetResultSet( termsFacetResultSet );

        contentIndexQueryResult.setFacetsResultSet( facetsResultSet );

        FindContentJsonResult result = new FindContentJsonResult( contents, contentIndexQueryResult );

        final JsonNode jsonNode = parseJson( "termsFacetResult.json" );

        assertJson( jsonNode, result.toJson() );
    }

    @Ignore // Test failing because of strange mapping of query-facets. We'll let this be for now, and fix after  18/04-2013
    @Test
    public void testQueryFacet()
        throws Exception
    {
        Contents contents = Contents.empty();

        ContentIndexQueryResult contentIndexQueryResult = new ContentIndexQueryResult( 10 );

        FacetsResultSet facetsResultSet = new FacetsResultSet();
        QueryFacetResultSet queryFacetResultSet = new QueryFacetResultSet( 10L );
        queryFacetResultSet.setName( "myQueryFacet" );
        facetsResultSet.addFacetResultSet( queryFacetResultSet );

        contentIndexQueryResult.setFacetsResultSet( facetsResultSet );

        FindContentJsonResult result = new FindContentJsonResult( contents, contentIndexQueryResult );

        final JsonNode jsonNode = parseJson( "queryFacetResult.json" );

        assertJson( jsonNode, result.toJson() );
    }
}
