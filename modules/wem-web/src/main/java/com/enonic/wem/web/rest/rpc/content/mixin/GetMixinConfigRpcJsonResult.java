package com.enonic.wem.web.rest.rpc.content.mixin;

import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.mixin.Mixin;
import com.enonic.wem.core.content.mixin.MixinXmlSerializer;
import com.enonic.wem.web.json.JsonResult;
import com.enonic.wem.web.rest.resource.content.BaseTypeImageUriResolver;

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
        json.put( "iconUrl", BaseTypeImageUriResolver.resolve( mixin.getBaseTypeKey() ) );
    }
}
