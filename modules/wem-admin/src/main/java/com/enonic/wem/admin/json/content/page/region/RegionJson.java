package com.enonic.wem.admin.json.content.page.region;


import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.enonic.wem.admin.json.content.page.PageComponentJson;
import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.api.content.page.region.Region;

@SuppressWarnings("UnusedDeclaration")
public class RegionJson
{
    private final Region region;

    private final List<PageComponentJson> components = new ArrayList<>();

    public RegionJson( final Region region )
    {
        this.region = region;
        for ( PageComponent component : region.getComponents() )
        {
            this.components.add( PageComponentJson.fromPageComponent( component ) );
        }
    }

    public String getName()
    {
        return region.getName();
    }

    public List<PageComponentJson> getComponents()
    {
        return this.components;
    }

    @JsonIgnore
    public Region getRegion()
    {
        return this.region;
    }
}
