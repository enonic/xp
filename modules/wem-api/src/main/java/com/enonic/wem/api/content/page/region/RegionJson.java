package com.enonic.wem.api.content.page.region;


import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.api.content.page.AbstractPageComponentJson;
import com.enonic.wem.api.content.page.PageComponent;

@SuppressWarnings("UnusedDeclaration")
public class RegionJson
{
    private final Region region;

    private final List<AbstractPageComponentJson> components = new ArrayList<>();

    @JsonCreator
    public RegionJson( @JsonProperty("name") final String name,
                       @JsonProperty("components") final List<AbstractPageComponentJson> componentJsons )
    {
        final Region.Builder builder = Region.newRegion();
        builder.name( name );
        for ( final AbstractPageComponentJson componentJson : componentJsons )
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
            this.components.add( AbstractPageComponentJson.fromPageComponent( component ) );
        }
    }

    public String getName()
    {
        return region.getName();
    }

    public List<AbstractPageComponentJson> getComponents()
    {
        return this.components;
    }

    @JsonIgnore
    public Region getRegion()
    {
        return this.region;
    }
}
