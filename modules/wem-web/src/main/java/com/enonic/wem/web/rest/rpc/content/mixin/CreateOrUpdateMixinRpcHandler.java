package com.enonic.wem.web.rest.rpc.content.mixin;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.mixin.CreateMixin;
import com.enonic.wem.api.command.content.mixin.GetMixins;
import com.enonic.wem.api.command.content.mixin.UpdateMixins;
import com.enonic.wem.api.content.mixin.Mixin;
import com.enonic.wem.api.content.mixin.QualifiedMixinName;
import com.enonic.wem.api.content.mixin.QualifiedMixinNames;
import com.enonic.wem.core.content.ParsingException;
import com.enonic.wem.core.content.mixin.MixinXmlSerializer;
import com.enonic.wem.web.json.JsonErrorResult;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;

import static com.enonic.wem.api.command.Commands.mixin;
import static com.enonic.wem.api.content.mixin.MixinEditors.setMixin;

@Component
public class CreateOrUpdateMixinRpcHandler
    extends AbstractDataRpcHandler
{
    private final MixinXmlSerializer mixinXmlSerializer = new MixinXmlSerializer();

    public CreateOrUpdateMixinRpcHandler()
    {
        super( "mixin_createOrUpdate" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final String mixinJson = context.param( "mixin" ).required().asString();
        final Mixin mixin;
        try
        {
            mixin = mixinXmlSerializer.toMixin( mixinJson );
        }
        catch ( ParsingException e )
        {
            context.setResult( new JsonErrorResult( "Invalid Mixin format" ) );
            return;
        }

        if ( !mixinExists( mixin.getQualifiedName() ) )
        {
            final CreateMixin createCommand = mixin().create().mixin( mixin );
            client.execute( createCommand );
            context.setResult( CreateOrUpdateMixinJsonResult.created() );
        }
        else
        {
            final QualifiedMixinNames names = QualifiedMixinNames.from( mixin.getQualifiedName() );
            final UpdateMixins updateCommand = mixin().update().names( names ).editor( setMixin( mixin ) );
            client.execute( updateCommand );
            context.setResult( CreateOrUpdateMixinJsonResult.updated() );
        }
    }

    private boolean mixinExists( final QualifiedMixinName qualifiedName )
    {
        final GetMixins getMixins = mixin().get().names( QualifiedMixinNames.from( qualifiedName ) );
        return !client.execute( getMixins ).isEmpty();
    }
}
