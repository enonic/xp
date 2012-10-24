package com.enonic.wem.web.rest.rpc.jcr;

import org.codehaus.jackson.node.ArrayNode;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.jcr.GetNodes;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;

@Component
public final class GetJcrNodesRpcHandler
    extends AbstractDataRpcHandler
{
    public GetJcrNodesRpcHandler()
    {
        super( "jcr_get" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final int depth = context.param( "depth" ).asInteger( 3 );
        final String path = context.param( "path" ).asString( "/" );
        final ArrayNode nodes = this.client.execute( new GetNodes().path( path ).depth( depth ) );

        final GetJcrNodesJsonResult result = new GetJcrNodesJsonResult( nodes );
        context.setResult( result );
    }

}
