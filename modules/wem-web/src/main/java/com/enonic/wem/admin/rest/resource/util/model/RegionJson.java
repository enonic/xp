package com.enonic.wem.admin.rest.resource.util.model;

import com.enonic.wem.admin.rest.resource.model.Item;
import com.enonic.wem.core.country.Region;

public class RegionJson
    extends Item
{
    private final Region model;

    public RegionJson( final Region model )
    {
        this.model = model;
    }

    public String getRegionCode()
    {
        return this.model.getCode();
    }

    public String getEnglishName()
    {
        return this.model.getEnglishName();
    }

    public String getLocalName()
    {
        return this.model.getLocalName();
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
