package com.enonic.wem.admin.rpc.content;

import java.util.Iterator;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.schema.content.GetContentTypes;
import com.enonic.wem.api.command.space.GetSpaces;
import com.enonic.wem.api.facet.Facet;
import com.enonic.wem.api.facet.Facets;
import com.enonic.wem.api.facet.TermsFacet;
import com.enonic.wem.api.facet.TermsFacetEntry;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.QualifiedContentTypeNames;
import com.enonic.wem.api.space.Space;
import com.enonic.wem.api.space.SpaceName;

public class FacetEnricher
{

    public static void enrichFacets( final Facets facets, final Client client )
    {

        if ( facets == null )
        {
            return;
        }

        final Iterator<Facet> facetsIterator = facets.iterator();

        while ( facetsIterator.hasNext() )
        {
            final Facet next = facetsIterator.next();

            final String name = next.getName();

            if ( "contentType".equals( name ) && next instanceof TermsFacet )
            {
                enrichContentTypeFacetWithDisplayName( client, (TermsFacet) next );
            }
            else if ( "space".equals( name ) && next instanceof TermsFacet )
            {
                enrichSpaceFacetWithDisplayName( client, (TermsFacet) next );

            }
        }
    }

    private static void enrichSpaceFacetWithDisplayName( final Client client, final TermsFacet termsFacet )
    {
        termsFacet.setDisplayName( "Space" );

        for ( TermsFacetEntry resultSet : termsFacet.getResults() )
        {
            final String qualifiedSpaceName = resultSet.getTerm();

            final GetSpaces getSpaces = Commands.space().get().name( SpaceName.from( qualifiedSpaceName ) );

            final Space space = client.execute( getSpaces ).first();

            Preconditions.checkArgument( space != null, "Space [%s] not found", qualifiedSpaceName );

            if ( space != null )
            {
                resultSet.setDisplayName( space.getDisplayName() );
            }
        }
    }

    private static void enrichContentTypeFacetWithDisplayName( final Client client, final TermsFacet termsFacet )
    {
        termsFacet.setDisplayName( "Content Type" );

        for ( TermsFacetEntry resultSet : termsFacet.getResults() )
        {
            final String qualifiedContentTypeName = resultSet.getTerm();

            final GetContentTypes getContentTypes =
                Commands.contentType().get().qualifiedNames( QualifiedContentTypeNames.from( qualifiedContentTypeName ) );

            final ContentType contentType = client.execute( getContentTypes ).first();

            Preconditions.checkArgument( contentType != null, "ContentType [%s] not found", qualifiedContentTypeName );

            if ( contentType != null )
            {
                resultSet.setDisplayName( contentType.getDisplayName() );
            }
        }
    }
}
