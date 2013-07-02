package com.enonic.wem.admin.rpc.schema.mixin;

import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.admin.json.JsonResult;
import com.enonic.wem.admin.rest.resource.schema.SchemaImageUriResolver;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.core.schema.mixin.MixinJsonSerializer;

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
