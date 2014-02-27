package com.enonic.wem.admin.rest.resource.content.site.template.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.api.content.site.Vendor;

public class VendorJson
{
    final String name;

    final String url;

    @JsonCreator
    VendorJson( @JsonProperty("name") final String name, @JsonProperty("url") final String url )
    {
        this.name = name;
        this.url = url;
    }

    public VendorJson( final Vendor vendor )
    {
        this.name = vendor.getName();
        this.url = vendor.getUrl();
    }

    public String getName()
    {
        return this.name;
    }

    public String getUrl()
    {
        return this.url;
    }

    public Vendor toVendor()
    {
        return Vendor.newVendor().name( this.name ).url( this.url ).build();
    }
}
