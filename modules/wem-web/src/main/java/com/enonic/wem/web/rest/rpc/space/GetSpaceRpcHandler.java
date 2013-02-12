package com.enonic.wem.web.rest.rpc.space;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.space.Space;
import com.enonic.wem.api.space.SpaceName;
import com.enonic.wem.web.json.JsonErrorResult;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;

@Component
public final class GetSpaceRpcHandler
    extends AbstractDataRpcHandler
{

    public GetSpaceRpcHandler()
    {
        super( "space_get" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final String nameParam = context.param( "spaceName" ).notBlank().asString();
        final SpaceName spaceName = SpaceName.from( nameParam );
        final Space space = client.execute( Commands.space().get().name( spaceName ) ).first();
        if ( space != null )
        {
            context.setResult( new GetSpaceJsonResult( space ) );
        }
        else
        {
            context.setResult( new JsonErrorResult( "Space [{0}] was not found", spaceName ) );
        }
    }
}
