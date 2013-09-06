package com.enonic.wem.admin.rest.resource.schema.mixin.json;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.enonic.wem.admin.json.schema.mixin.MixinJson;
import com.enonic.wem.api.schema.mixin.Mixin;

@XmlRootElement
public class MixinGetJson
    extends AbstractMixinJson
{
    private final MixinJson mixin;

    public MixinGetJson( final Mixin model )
    {
        this.mixin = new MixinJson( model );
    }

    @XmlElement
    public MixinJson getMixin()
    {
        return this.mixin;
    }
}
