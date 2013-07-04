package com.enonic.wem.admin.rpc.content;

import com.enonic.wem.admin.jsonrpc.JsonRpcContext;
import com.enonic.wem.admin.rpc.AbstractDataRpcHandler;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.GenerateContentName;

public class GenerateContentNameRpcHandler
    extends AbstractDataRpcHandler
{
    public GenerateContentNameRpcHandler()
    {
        super( "content_generateName" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final String displayName = context.param( "displayName" ).required().asString();

        final GenerateContentName generateContentName = Commands.content().generateContentName().displayName( displayName );

        final String generatedContentName = client.execute( generateContentName );
        context.setResult( new GenerateContentNameJsonResult( generatedContentName ) );
    }
}
