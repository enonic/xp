package com.enonic.wem.web.rest.rpc.content.type;

import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.type.form.Mixin;
import com.enonic.wem.core.content.type.MixinJsonSerializer;
import com.enonic.wem.web.json.JsonResult;

final class GetMixinRpcJsonResult
    extends JsonResult
{
    private final static MixinJsonSerializer MIXIN_JSON_SERIALIZER = new MixinJsonSerializer();

    private final Mixin mixin;

    public GetMixinRpcJsonResult( final Mixin mixin )
    {
        this.mixin = mixin;
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "mixin", MIXIN_JSON_SERIALIZER.toJson( mixin ) );
    }
}
