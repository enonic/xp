package com.enonic.wem.web.rest.rpc.content.type;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.content.QualifiedMixinNames;
import com.enonic.wem.api.content.type.Mixins;
import com.enonic.wem.api.content.type.form.Mixin;
import com.enonic.wem.api.content.type.form.QualifiedMixinName;
import com.enonic.wem.web.json.JsonErrorResult;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;

@Component
public class GetMixinRpcHandler
    extends AbstractDataRpcHandler
{

    public GetMixinRpcHandler()
    {
        super( "mixin_get" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final QualifiedMixinName qualifiedMixinName = new QualifiedMixinName( context.param( "mixin" ).required().asString() );

        final Mixin mixin = fetchMixin( qualifiedMixinName );

        if ( mixin != null )
        {
            context.setResult( new GetMixinRpcJsonResult( mixin ) );
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
