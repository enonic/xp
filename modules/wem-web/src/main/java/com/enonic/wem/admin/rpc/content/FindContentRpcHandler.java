package com.enonic.wem.admin.rpc.content;

import org.codehaus.jackson.node.ObjectNode;
import org.joda.time.DateTime;

import com.google.common.base.Strings;

import com.enonic.wem.admin.jsonrpc.JsonRpcContext;
import com.enonic.wem.admin.rpc.AbstractDataRpcHandler;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.content.ContentIds;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.query.ContentIndexQuery;
import com.enonic.wem.api.content.query.ContentIndexQueryResult;
import com.enonic.wem.api.schema.content.QualifiedContentTypeNames;
import com.enonic.wem.api.space.SpaceNames;


public class FindContentRpcHandler
    extends AbstractDataRpcHandler
{
    public FindContentRpcHandler()
    {
        super( "content_find" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final String fulltext = context.param( "fulltext" ).asString( "" );
        final boolean includeFacets = context.param( "includeFacets" ).asBoolean( true );
        final String[] contentTypes = context.param( "contentTypes" ).asStringArray();
        final String[] spaces = context.param( "spaces" ).asStringArray();
        final Integer size = context.param( "size" ).asInteger();

        final ContentIndexQuery contentIndexQuery = new ContentIndexQuery();
        contentIndexQuery.setFullTextSearchString( fulltext );
        contentIndexQuery.setIncludeFacets( includeFacets );
        contentIndexQuery.setContentTypeNames( QualifiedContentTypeNames.from( contentTypes ) );
        contentIndexQuery.setSpaceNames( SpaceNames.from( spaces ) );

        if ( size != null )
        {
            contentIndexQuery.setSize( size );
        }

        final ObjectNode[] rangesJson = context.param( "ranges" ).asObjectArray();

        if ( rangesJson != null && rangesJson.length > 0 )
        {
            addRanges( contentIndexQuery, rangesJson );
        }

        if ( includeFacets )
        {
            addFacetes( context, contentIndexQuery );
        }

        final ContentIndexQueryResult contentIndexQueryResult = this.client.execute( Commands.content().find().query( contentIndexQuery ) );

        FacetResultSetEnricher.enrichFacetResult( contentIndexQueryResult.getFacetsResultSet(), this.client );

        final Contents contents =
            this.client.execute( Commands.content().get().selectors( ContentIds.from( contentIndexQueryResult.getContentIds() ) ) );

        final FindContentJsonResult json = new FindContentJsonResult( contents, contentIndexQueryResult );

        context.setResult( json );
    }

    private void addFacetes( final JsonRpcContext context, final ContentIndexQuery contentIndexQuery )
    {
        Object facetDef = context.param( "facets" ).asObject();

        if ( facetDef != null )
        {
            contentIndexQuery.setFacets( facetDef.toString() );
        }
    }

    private void addRanges( final ContentIndexQuery contentIndexQuery, final ObjectNode[] rangesJson )
    {
        for ( ObjectNode range : rangesJson )
        {
            contentIndexQuery.addRange( parseDateTimeParameter( range.get( "lower" ).asText() ),
                                        parseDateTimeParameter( range.get( "upper" ).asText() ) );
        }
    }


    private DateTime parseDateTimeParameter( final String dateTimeAsString )
    {
        if ( Strings.isNullOrEmpty( dateTimeAsString ) )
        {
            return null;
        }

        try
        {
            return DateTime.parse( dateTimeAsString );
        }
        catch ( Exception e )
        {
            return null;
        }
    }

}
