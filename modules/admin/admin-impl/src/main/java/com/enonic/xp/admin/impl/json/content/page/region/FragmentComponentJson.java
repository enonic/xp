package com.enonic.xp.admin.impl.json.content.page.region;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.admin.impl.rest.resource.content.ComponentNameResolver;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.data.PropertyArrayJson;
import com.enonic.xp.region.FragmentComponent;

@SuppressWarnings("UnusedDeclaration")
public class FragmentComponentJson
    extends ComponentJson<FragmentComponent>
{
    private final FragmentComponent fragment;

    @JsonCreator
    public FragmentComponentJson( @JsonProperty("config") final List<PropertyArrayJson> config,
                                  @JsonProperty("fragment") final String fragment )
    {
        super( FragmentComponent.create().
            fragment( fragment != null ? ContentId.from( fragment ) : null ).
            build(), null );

        this.fragment = getComponent();
    }

    public FragmentComponentJson( final FragmentComponent component, final ComponentNameResolver componentNameResolver )
    {
        super( component, componentNameResolver );
        this.fragment = component;
    }

    public String getFragment()
    {
        return fragment.getFragment() != null ? fragment.getFragment().toString() : null;
    }
}
