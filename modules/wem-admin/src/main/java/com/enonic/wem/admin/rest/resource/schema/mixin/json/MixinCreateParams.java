package com.enonic.wem.admin.rest.resource.schema.mixin.json;

import com.enonic.wem.api.schema.mixin.MixinName;

public class MixinCreateParams
{
    private MixinName name;

    private String config;

    private String iconReference;

    public MixinName getName()
    {
        return name;
    }

    public void setName( final String name )
    {
        this.name = MixinName.from( name );
    }

    public String getConfig()
    {
        return config;
    }

    public void setConfig( final String config )
    {
        this.config = config;
    }

    public String getIconReference()
    {
        return iconReference;
    }

    public void setIconReference( final String iconReference )
    {
        this.iconReference = iconReference;
    }
}
