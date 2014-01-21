package com.enonic.wem.admin.json.content.page.region;


import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.admin.json.content.page.PageComponentJson;
import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.api.content.page.region.Region;

@SuppressWarnings("UnusedDeclaration")
public class RegionJson
{
    private final Region region;

    private final List<PageComponentJson> components = new ArrayList<>();

    @JsonCreator
    public RegionJson( @JsonProperty("name") final String name, @JsonProperty("components") final List<PageComponentJson> componentJsons )
    {
        final Region.Builder builder = Region.newRegion();
        builder.name( name );
        for ( final PageComponentJson componentJson : componentJsons )
        {
            builder.add( componentJson.getComponent() );
        }
        this.region = builder.build();
    }

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
