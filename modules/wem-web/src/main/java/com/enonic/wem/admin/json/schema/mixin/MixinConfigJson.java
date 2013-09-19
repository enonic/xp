package com.enonic.wem.admin.json.schema.mixin;

import com.enonic.wem.admin.json.ItemJson;
import com.enonic.wem.admin.rest.resource.schema.SchemaImageUriResolver;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.core.schema.mixin.MixinXmlSerializer;

public class MixinConfigJson
    extends ItemJson
{
    private final static MixinXmlSerializer mixinXmlSerializer = new MixinXmlSerializer().prettyPrint( true );

    private final String mixinXml;

    private final String iconUrl;

    public MixinConfigJson( final Mixin model )
    {
        this.mixinXml = mixinXmlSerializer.toString( model );
        this.iconUrl = SchemaImageUriResolver.resolve( model.getSchemaKey() );
    }

    public String getMixinXml()
    {
        return mixinXml;
    }

    public String getIconUrl()
    {
        return iconUrl;
    }

    @Override
    public boolean getEditable()
    {
        return false;
    }

    @Override
    public boolean getDeletable()
    {
        return false;
    }
}
