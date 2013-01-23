package com.enonic.wem.web.rest.rpc.content.type;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.content.type.BaseType;
import com.enonic.wem.api.support.tree.Tree;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;

import static com.enonic.wem.api.command.Commands.baseType;

@Component
public final class GetBaseTypeTreeRpcHandler
    extends AbstractDataRpcHandler
{
    public GetBaseTypeTreeRpcHandler()
    {
        super( "baseType_tree" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final Tree<BaseType> baseTypeTree = client.execute( baseType().getTree() );
        context.setResult( new GetBaseTypeTreeJsonResult( baseTypeTree ) );
    }
}
