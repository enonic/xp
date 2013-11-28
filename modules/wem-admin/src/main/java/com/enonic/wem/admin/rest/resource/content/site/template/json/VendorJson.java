package com.enonic.wem.admin.rest.resource.content.site.template.json;

import com.enonic.wem.api.content.site.Vendor;

public class VendorJson
{
    private Vendor vendor;

    public VendorJson( Vendor vendor )
    {
        this.vendor = vendor;
    }

    public String getName()
    {
        return this.vendor.getName();
    }

    public String getUrl()
    {
        return this.vendor.getUrl();
    }
}
