package com.enonic.wem.web.rest.rpc.content.mixin;

import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.mixin.Mixin;
import com.enonic.wem.core.content.mixin.MixinJsonSerializer;
import com.enonic.wem.web.json.JsonResult;
import com.enonic.wem.web.rest.resource.content.BaseTypeImageUriResolver;

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
        final ObjectNode mixinJson = (ObjectNode) MIXIN_JSON_SERIALIZER.toJson( mixin );
        mixinJson.put( "iconUrl", BaseTypeImageUriResolver.resolve( mixin.getBaseTypeKey() ) );
        json.put( "mixin", mixinJson );
    }
}
