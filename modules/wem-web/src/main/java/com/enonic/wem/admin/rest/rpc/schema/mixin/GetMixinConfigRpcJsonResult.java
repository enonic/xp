package com.enonic.wem.admin.rest.rpc.schema.mixin;

import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.admin.json.JsonResult;
import com.enonic.wem.admin.rest.resource.schema.SchemaImageUriResolver;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.core.schema.mixin.MixinXmlSerializer;

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
        json.put( "iconUrl", SchemaImageUriResolver.resolve( mixin.getSchemaKey() ) );
    }
}
