package com.enonic.xp.admin.impl.json.content.page.region;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.data.PropertyArrayJson;
import com.enonic.xp.data.PropertyTreeJson;
import com.enonic.xp.region.ComponentName;
import com.enonic.xp.region.FragmentComponent;

@SuppressWarnings("UnusedDeclaration")
public class FragmentComponentJson
    extends ComponentJson<FragmentComponent>
{
    private final FragmentComponent fragment;

    private final List<PropertyArrayJson> config;

    @JsonCreator
    public FragmentComponentJson( @JsonProperty("name") final String name, @JsonProperty("config") final List<PropertyArrayJson> config,
                                  @JsonProperty("fragment") final String fragment )
    {
        super( FragmentComponent.create().
            name( name != null ? ComponentName.from( name ) : null ).
            fragment( fragment != null ? ContentId.from( fragment ) : null ).
            config( config != null ? PropertyTreeJson.fromJson( config ) : null ).
            build() );

        this.fragment = getComponent();
        this.config = null; // not needed when parsing JSON
    }

    public FragmentComponentJson( final FragmentComponent component )
    {
        super( component );
        this.fragment = component;
        this.config = this.fragment.getConfig() != null ? PropertyTreeJson.toJson( this.fragment.getConfig() ) : null;
    }

    public String getFragment()
    {
        return fragment.getFragment() != null ? fragment.getFragment().toString() : null;
    }

    public List<PropertyArrayJson> getConfig()
    {
        return config;
    }
}
