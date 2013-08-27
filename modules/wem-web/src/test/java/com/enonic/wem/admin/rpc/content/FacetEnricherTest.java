package com.enonic.wem.admin.rpc.content;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.schema.content.GetContentTypes;
import com.enonic.wem.api.command.space.GetSpaces;
import com.enonic.wem.api.facet.Facets;
import com.enonic.wem.api.facet.TermsFacet;
import com.enonic.wem.api.facet.TermsFacetEntry;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.space.Space;
import com.enonic.wem.api.space.Spaces;

import static org.junit.Assert.*;


public class FacetEnricherTest
{
    private Client client;

    @Test
    public void testUntouchedFacet()
        throws Exception
    {
        client = Mockito.mock( Client.class );

        Facets facets = new Facets();
        final TermsFacet contentTypeFacet = new TermsFacet();
        contentTypeFacet.setName( "dummyFacet" );
        contentTypeFacet.addResult( "test:test", 1 );
        contentTypeFacet.addResult( "test:test", 2 );
        contentTypeFacet.addResult( "test:test", 3 );

        facets.addFacet( contentTypeFacet );

        FacetEnricher.enrichFacets( facets, client );

        for ( TermsFacetEntry result : contentTypeFacet.getResults() )
        {
            assertNotNull( result );
            assertEquals( "test:test", result.getDisplayName() );
        }
    }


    @Test
    public void testEnrichContentTypeFacet()
        throws Exception
    {
        client = Mockito.mock( Client.class );
        ContentType contentType = ContentType.newContentType().module( ModuleName.from( "mymodule" ) ).name( "test" ).displayName(
            "thisIsMyDisplayName" ).build();
        final ContentTypes contentTypes = ContentTypes.from( contentType );
        Mockito.when( client.execute( Mockito.isA( GetContentTypes.class ) ) ).thenReturn( contentTypes );

        Facets facets = new Facets();
        final TermsFacet contentTypeFacet = new TermsFacet();
        contentTypeFacet.setName( "contentType" );
        contentTypeFacet.addResult( "test:test", 1 );
        contentTypeFacet.addResult( "demo:ost", 2 );
        contentTypeFacet.addResult( "demo:fisk", 3 );

        facets.addFacet( contentTypeFacet );

        FacetEnricher.enrichFacets( facets, client );

        for ( TermsFacetEntry result : contentTypeFacet.getResults() )
        {
            assertNotNull( result );
            assertEquals( "thisIsMyDisplayName", result.getDisplayName() );
        }
    }

    @Test
    public void testEnrichSpaceFacet()
        throws Exception
    {
        client = Mockito.mock( Client.class );

        Space space = Space.newSpace().name( "myspace" ).displayName( "thisIsMyDisplayName" ).build();

        Mockito.when( client.execute( Mockito.isA( GetSpaces.class ) ) ).thenReturn( Spaces.from( space ) );

        Facets facets = new Facets();
        final TermsFacet contentTypeFacet = new TermsFacet();
        contentTypeFacet.setName( "space" );
        contentTypeFacet.addResult( "myspace", 1 );
        contentTypeFacet.addResult( "myotherspace", 2 );

        facets.addFacet( contentTypeFacet );

        FacetEnricher.enrichFacets( facets, client );

        for ( TermsFacetEntry result : contentTypeFacet.getResults() )
        {
            assertNotNull( result );
            assertEquals( "thisIsMyDisplayName", result.getDisplayName() );
        }
    }

}
