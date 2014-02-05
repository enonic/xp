package com.enonic.wem.admin.json.schema.mixin;

import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MixinXml;
import com.enonic.wem.xml.XmlSerializers;

public class MixinConfigJson
{
    private final String mixinXml;

    public MixinConfigJson( final Mixin model )
    {
        final MixinXml mixinXml = new MixinXml();
        mixinXml.from( model );
        this.mixinXml = XmlSerializers.mixin().serialize( mixinXml );
    }

    public String getMixinXml()
    {
        return mixinXml;
    }
}
