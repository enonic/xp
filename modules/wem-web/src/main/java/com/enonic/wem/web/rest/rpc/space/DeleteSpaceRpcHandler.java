package com.enonic.wem.web.rest.rpc.space;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.space.DeleteSpace;
import com.enonic.wem.api.space.SpaceName;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;

@Component
public final class DeleteSpaceRpcHandler
    extends AbstractDataRpcHandler
{
    public DeleteSpaceRpcHandler()
    {
        super( "space_delete" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final SpaceName spaceName = SpaceName.from( context.param( "spaceName" ).notBlank().asString() );

        final DeleteSpace deleteSpaces = Commands.space().delete().name( spaceName );
        boolean deleted = client.execute( deleteSpaces );
        if ( deleted )
        {
            context.setResult( DeleteSpaceJsonResult.success() );
        }
        else
        {
            context.setResult( DeleteSpaceJsonResult.failure( String.format( "Space [%s] was not found", spaceName.name() ) ) );
        }
    }
}
