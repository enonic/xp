package com.enonic.wem.admin.rpc.schema.mixin;


import com.enonic.wem.admin.json.JsonErrorResult;
import com.enonic.wem.admin.jsonrpc.JsonRpcContext;
import com.enonic.wem.admin.rpc.AbstractDataRpcHandler;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MixinName;


public class GetMixinRpcHandler
    extends AbstractDataRpcHandler
{
    public static final String FORMAT_XML = "XML";

    public static final String FORMAT_JSON = "JSON";

    public GetMixinRpcHandler()
    {
        super( "mixin_get" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final String format = context.param( "format" ).required().asString();
        final MixinName mixinName = MixinName.from( context.param( "qualifiedName" ).required().asString() );

        final Mixin mixin = fetchMixin( mixinName );

        if ( mixin != null )
        {
            if ( format.equalsIgnoreCase( FORMAT_JSON ) )
            {
                context.setResult( new GetMixinRpcJsonResult( mixin ) );
            }
            else if ( format.equalsIgnoreCase( FORMAT_XML ) )
            {
                context.setResult( new GetMixinConfigRpcJsonResult( mixin ) );
            }

        }
        else
        {
            context.setResult( new JsonErrorResult( "Mixin [{0}] was not found", mixinName ) );
        }
    }

    private Mixin fetchMixin( final MixinName qualifiedName )
    {
        return client.execute( Commands.mixin().get().byName( qualifiedName ) );
    }
}
