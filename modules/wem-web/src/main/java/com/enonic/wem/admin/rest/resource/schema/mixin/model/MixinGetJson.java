package com.enonic.wem.admin.rest.resource.schema.mixin.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.enonic.wem.api.schema.mixin.Mixin;

@XmlRootElement
public class MixinGetJson
    extends AbstractMixinJson
{
    private final MixinJson mixin;

    public MixinGetJson( final Mixin model )
    {
        this.mixin = new MixinJson(model);
    }

    @XmlElement
    public MixinJson getMixin()
    {
        return this.mixin;
    }
}
