package com.enonic.wem.web.rest.rpc.content;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.schema.content.GetContentTypes;
import com.enonic.wem.api.command.space.GetSpaces;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.api.query.FacetsResultSet;
import com.enonic.wem.api.query.TermsFacetResultSet;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.space.Space;
import com.enonic.wem.api.space.Spaces;

import static org.junit.Assert.*;


public class FacetResultSetEnricherTest
{
    private Client client;

    @Test
    public void testUntouchedFacet()
        throws Exception
    {
        client = Mockito.mock( Client.class );

        FacetsResultSet facetsResultSet = new FacetsResultSet();
        final TermsFacetResultSet contentTypeFacetResultSet = new TermsFacetResultSet();
        contentTypeFacetResultSet.setName( "dummyFacet" );
        contentTypeFacetResultSet.addResult( "test:test", 1 );
        contentTypeFacetResultSet.addResult( "test:test", 2 );
        contentTypeFacetResultSet.addResult( "test:test", 3 );

        facetsResultSet.addFacetResultSet( contentTypeFacetResultSet );

        FacetResultSetEnricher.enrichFacetResult( facetsResultSet, client );

        for ( TermsFacetResultSet.TermFacetResult result : contentTypeFacetResultSet.getResults() )
        {
            assertNotNull( result );
            assertEquals( "test:test", result.getDisplayName() );
        }
    }


    @Test
    public void testEnrichContentTypeFacetResult()
        throws Exception
    {
        client = Mockito.mock( Client.class );
        ContentType contentType = ContentType.newContentType().module( ModuleName.from( "mymodule" ) ).name( "test" ).displayName(
            "thisIsMyDisplayName" ).build();
        final ContentTypes contentTypes = ContentTypes.from( contentType );
        Mockito.when( client.execute( Mockito.isA( GetContentTypes.class ) ) ).thenReturn( contentTypes );

        FacetsResultSet facetsResultSet = new FacetsResultSet();
        final TermsFacetResultSet contentTypeFacetResultSet = new TermsFacetResultSet();
        contentTypeFacetResultSet.setName( "contentType" );
        contentTypeFacetResultSet.addResult( "test:test", 1 );
        contentTypeFacetResultSet.addResult( "demo:ost", 2 );
        contentTypeFacetResultSet.addResult( "demo:fisk", 3 );

        facetsResultSet.addFacetResultSet( contentTypeFacetResultSet );

        FacetResultSetEnricher.enrichFacetResult( facetsResultSet, client );

        for ( TermsFacetResultSet.TermFacetResult result : contentTypeFacetResultSet.getResults() )
        {
            assertNotNull( result );
            assertEquals( "thisIsMyDisplayName", result.getDisplayName() );
        }
    }

    @Test
    public void testEnrichSpaceFacetResult()
        throws Exception
    {
        client = Mockito.mock( Client.class );

        Space space = Space.newSpace().name( "myspace" ).displayName( "thisIsMyDisplayName" ).build();

        Mockito.when( client.execute( Mockito.isA( GetSpaces.class ) ) ).thenReturn( Spaces.from( space ) );

        FacetsResultSet facetsResultSet = new FacetsResultSet();
        final TermsFacetResultSet contentTypeFacetResultSet = new TermsFacetResultSet();
        contentTypeFacetResultSet.setName( "space" );
        contentTypeFacetResultSet.addResult( "myspace", 1 );
        contentTypeFacetResultSet.addResult( "myotherspace", 2 );

        facetsResultSet.addFacetResultSet( contentTypeFacetResultSet );

        FacetResultSetEnricher.enrichFacetResult( facetsResultSet, client );

        for ( TermsFacetResultSet.TermFacetResult result : contentTypeFacetResultSet.getResults() )
        {
            assertNotNull( result );
            assertEquals( "thisIsMyDisplayName", result.getDisplayName() );
        }
    }

}
