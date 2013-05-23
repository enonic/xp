package com.enonic.wem.admin.rest.rpc.schema.mixin;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.admin.json.JsonResult;
import com.enonic.wem.admin.rest.resource.schema.SchemaImageUriResolver;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.Mixins;
import com.enonic.wem.core.schema.mixin.MixinJsonSerializer;

final class ListMixinsRpcJsonResult
    extends JsonResult
{
    private final static MixinJsonSerializer MIXIN_JSON_SERIALIZER = new MixinJsonSerializer();

    private final Mixins mixins;

    public ListMixinsRpcJsonResult( final Mixins mixins )
    {
        this.mixins = mixins;
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        final ArrayNode mixinArray = arrayNode();
        for ( Mixin mixin : mixins )
        {
            final ObjectNode mixinJson = (ObjectNode) serializeMixin( mixin );
            mixinJson.put( "iconUrl", SchemaImageUriResolver.resolve( mixin.getSchemaKey() ) );
            mixinArray.add( mixinJson );
        }
        json.put( "mixins", mixinArray );
    }

    private JsonNode serializeMixin( final Mixin mixin )
    {
        return MIXIN_JSON_SERIALIZER.toJson( mixin );
    }

}
