package com.enonic.wem.admin.json.schema.mixin;

import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.core.schema.mixin.MixinXmlSerializer;

public class MixinConfigJson
{
    private final static MixinXmlSerializer mixinXmlSerializer = new MixinXmlSerializer().
        includeName( false ).
        prettyPrint( true );

    private final String mixinXml;

    public MixinConfigJson( final Mixin model )
    {
        this.mixinXml = mixinXmlSerializer.toString( model );
    }

    public String getMixinXml()
    {
        return mixinXml;
    }
}
