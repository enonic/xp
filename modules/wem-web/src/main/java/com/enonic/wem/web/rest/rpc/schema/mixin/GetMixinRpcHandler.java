package com.enonic.wem.web.rest.rpc.schema.mixin;


import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.Mixins;
import com.enonic.wem.api.schema.mixin.QualifiedMixinName;
import com.enonic.wem.api.schema.mixin.QualifiedMixinNames;
import com.enonic.wem.web.json.JsonErrorResult;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;


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
        final QualifiedMixinName qualifiedMixinName = new QualifiedMixinName( context.param( "mixin" ).required().asString() );

        final Mixin mixin = fetchMixin( qualifiedMixinName );

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
            context.setResult( new JsonErrorResult( "Mixin [{0}] was not found", qualifiedMixinName ) );
        }
    }

    private Mixin fetchMixin( final QualifiedMixinName qualifiedName )
    {
        final QualifiedMixinNames qualifiedNames = QualifiedMixinNames.from( qualifiedName );
        final Mixins mixinsResult = client.execute( Commands.mixin().get().names( qualifiedNames ) );
        return mixinsResult.isEmpty() ? null : mixinsResult.first();
    }
}
