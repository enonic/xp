package com.enonic.wem.admin.rest.resource.schema.mixin.json;

public class MixinCreateOrUpdateParams
{
    private String mixin;

    private String iconReference;

    public String getMixin()
    {
        return mixin;
    }

    public void setMixin( final String mixin )
    {
        this.mixin = mixin;
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
