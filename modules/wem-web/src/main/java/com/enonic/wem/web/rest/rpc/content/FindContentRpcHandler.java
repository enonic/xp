package com.enonic.wem.web.rest.rpc.content;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.content.ContentIds;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.query.ContentIndexQuery;
import com.enonic.wem.api.content.query.ContentIndexQueryResult;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;

@Component
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
        final boolean includeFacets = context.param( "include" ).asBoolean( true );
        final String interval = context.param( "interval" ).asString( "day" );

        ContentIndexQuery contentIndexQuery = new ContentIndexQuery();

        contentIndexQuery.setFullTextSearchString( fulltext );
        contentIndexQuery.setIncludeFacets( includeFacets );
        contentIndexQuery.setInterval( interval );

        final ContentIndexQueryResult contentIndexQueryResult = this.client.execute( Commands.content().find().query( contentIndexQuery ) );

        final Contents contents =
            this.client.execute( Commands.content().get().selectors( ContentIds.from( contentIndexQueryResult.getContentIds() ) ) );

        final FindContentJsonResult json = new FindContentJsonResult( contents, contentIndexQueryResult );

        context.setResult( json );
    }
}
