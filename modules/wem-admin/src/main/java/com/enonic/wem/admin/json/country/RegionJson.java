package com.enonic.wem.admin.json.country;

import com.enonic.wem.admin.json.ItemJson;
import com.enonic.wem.core.country.Region;

public class RegionJson
    implements ItemJson
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
