package com.enonic.wem.admin.rpc.content;

import java.util.Iterator;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.schema.content.GetContentTypes;
import com.enonic.wem.api.command.space.GetSpaces;
import com.enonic.wem.api.query.FacetResultSet;
import com.enonic.wem.api.query.FacetsResultSet;
import com.enonic.wem.api.query.TermsFacetResultSet;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.QualifiedContentTypeNames;
import com.enonic.wem.api.space.Space;
import com.enonic.wem.api.space.SpaceName;

public class FacetResultSetEnricher
{

    public static void enrichFacetResult( final FacetsResultSet facetsResultSet, final Client client )
    {

        if ( facetsResultSet == null )
        {
            return;
        }

        final Iterator<FacetResultSet> facetsIterator = facetsResultSet.iterator();

        while ( facetsIterator.hasNext() )
        {
            final FacetResultSet next = facetsIterator.next();

            final String name = next.getName();

            if ( "contentType".equals( name ) && next instanceof TermsFacetResultSet )
            {
                enrichContentTypeFacetWithDisplayName( client, (TermsFacetResultSet) next );
            }
            else if ( "space".equals( name ) && next instanceof TermsFacetResultSet )
            {
                enrichSpaceFacetWithDisplayName( client, (TermsFacetResultSet) next );

            }
        }
    }

    private static void enrichSpaceFacetWithDisplayName( final Client client, final TermsFacetResultSet termsFacetResultSet )
    {
        termsFacetResultSet.setDisplayName( "Space" );

        for ( TermsFacetResultSet.TermFacetResult resultSet : termsFacetResultSet.getResults() )
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

    private static void enrichContentTypeFacetWithDisplayName( final Client client, final TermsFacetResultSet termsFacetResultSet )
    {
        termsFacetResultSet.setDisplayName( "Content Type" );

        for ( TermsFacetResultSet.TermFacetResult resultSet : termsFacetResultSet.getResults() )
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
