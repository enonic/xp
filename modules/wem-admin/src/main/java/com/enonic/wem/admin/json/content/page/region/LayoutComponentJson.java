package com.enonic.wem.admin.json.content.page.region;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.content.page.region.ComponentName;
import com.enonic.wem.api.content.page.region.LayoutComponent;
import com.enonic.wem.api.data.PropertyArrayJson;
import com.enonic.wem.api.data.PropertyTreeJson;

import static com.enonic.wem.api.content.page.region.LayoutComponent.newLayoutComponent;

@SuppressWarnings("UnusedDeclaration")
public class LayoutComponentJson
    extends DescriptorBasedComponentJson<LayoutComponent>
{
    private final LayoutComponent layout;

    private final LayoutRegionsJson regionsJson;

    @JsonCreator
    public LayoutComponentJson( @JsonProperty("name") final String name, @JsonProperty("descriptor") final String descriptor,
                                @JsonProperty("config") final List<PropertyArrayJson> config,
                                final @JsonProperty("regions") List<RegionJson> regions )
    {
        super( newLayoutComponent().
            name( ComponentName.from( name ) ).
            descriptor( descriptor != null ? DescriptorKey.from( descriptor ) : null ).
            config( config != null ? PropertyTreeJson.fromJson( config ) : null ).
            regions( regions != null ? new LayoutRegionsJson( regions ).getLayoutRegions() : null ).
            build() );

        this.layout = getComponent();
        this.regionsJson = new LayoutRegionsJson( layout.getRegions() );
    }

    public LayoutComponentJson( final LayoutComponent component )
    {
        super( component );
        this.layout = component;
        this.regionsJson = new LayoutRegionsJson( component.getRegions() );
    }

    public List<RegionJson> getRegions()
    {
        return regionsJson.getRegions();
    }
}
