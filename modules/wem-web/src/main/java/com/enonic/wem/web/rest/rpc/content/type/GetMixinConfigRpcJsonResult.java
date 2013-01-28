package com.enonic.wem.web.rest.rpc.content.type;

import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.type.form.Mixin;
import com.enonic.wem.core.content.type.MixinXmlSerializer;
import com.enonic.wem.web.json.JsonResult;

final class GetMixinConfigRpcJsonResult
    extends JsonResult
{
    private final static MixinXmlSerializer mixinXmlSerializer = new MixinXmlSerializer().prettyPrint( true );

    private final Mixin mixin;

    public GetMixinConfigRpcJsonResult( final Mixin mixin )
    {
        this.mixin = mixin;
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        final String mixinXml = mixinXmlSerializer.toString( mixin );
        json.put( "mixinXml", mixinXml );
    }
}
