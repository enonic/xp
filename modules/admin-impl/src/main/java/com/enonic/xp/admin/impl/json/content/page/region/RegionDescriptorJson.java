package com.enonic.xp.admin.impl.json.content.page.region;


import com.enonic.wem.api.content.page.region.RegionDescriptor;

public class RegionDescriptorJson
{
    private RegionDescriptor regionDescriptor;

    public RegionDescriptorJson( final RegionDescriptor regionDescriptor )
    {
        this.regionDescriptor = regionDescriptor;
    }

    public String getName()
    {
        return this.regionDescriptor.getName();
    }
}
