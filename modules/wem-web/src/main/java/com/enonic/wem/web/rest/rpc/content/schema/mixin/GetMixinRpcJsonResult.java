package com.enonic.wem.web.rest.rpc.content.schema.mixin;

import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.schema.mixin.Mixin;
import com.enonic.wem.core.content.schema.mixin.MixinJsonSerializer;
import com.enonic.wem.web.json.JsonResult;
import com.enonic.wem.web.rest.resource.content.schema.SchemaImageUriResolver;

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
        mixinJson.put( "iconUrl", SchemaImageUriResolver.resolve( mixin.getSchemaKey() ) );
        json.put( "mixin", mixinJson );
    }
}
