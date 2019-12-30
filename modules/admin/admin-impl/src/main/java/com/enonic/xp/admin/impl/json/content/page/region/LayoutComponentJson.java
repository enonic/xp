package com.enonic.xp.admin.impl.json.content.page.region;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.admin.impl.rest.resource.content.ComponentNameResolver;
import com.enonic.xp.data.PropertyArrayJson;
import com.enonic.xp.data.PropertyTreeJson;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.region.LayoutComponent;

@SuppressWarnings("UnusedDeclaration")
public class LayoutComponentJson
    extends DescriptorBasedComponentJson<LayoutComponent>
{
    private final LayoutComponent layout;

    private final LayoutRegionsJson regionsJson;

    @JsonCreator
    public LayoutComponentJson( @JsonProperty("descriptor") final String descriptor,
                                @JsonProperty("config") final List<PropertyArrayJson> config,
                                final @JsonProperty("regions") List<RegionJson> regions )
    {
        super( LayoutComponent.create().
            descriptor( descriptor != null ? DescriptorKey.from( descriptor ) : null ).
            config( config != null ? PropertyTreeJson.fromJson( config ) : null ).
            regions( regions != null ? new LayoutRegionsJson( regions ).getLayoutRegions() : null ).
            build(), null );

        this.layout = getComponent();
        this.regionsJson = new LayoutRegionsJson( layout.getRegions(), null );
    }

    public LayoutComponentJson( final LayoutComponent component, final ComponentNameResolver componentNameResolver )
    {
        super( component, componentNameResolver );
        this.layout = component;
        this.regionsJson = new LayoutRegionsJson( component.getRegions(), componentNameResolver );
    }

    public List<RegionJson> getRegions()
    {
        return regionsJson.getRegions();
    }
}
