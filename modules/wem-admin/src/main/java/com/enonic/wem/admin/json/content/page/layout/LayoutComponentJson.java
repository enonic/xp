package com.enonic.wem.admin.json.content.page.layout;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.admin.json.content.page.PageComponentJson;
import com.enonic.wem.admin.json.content.page.region.RegionJson;
import com.enonic.wem.admin.json.data.DataJson;
import com.enonic.wem.admin.json.data.RootDataSetJson;
import com.enonic.wem.api.content.page.ComponentName;
import com.enonic.wem.api.content.page.layout.LayoutComponent;
import com.enonic.wem.api.content.page.layout.LayoutTemplateKey;

import static com.enonic.wem.api.content.page.layout.LayoutComponent.newLayoutComponent;

@SuppressWarnings("UnusedDeclaration")
public class LayoutComponentJson
    extends PageComponentJson<LayoutComponent>
{
    private final LayoutComponent layout;

    private final LayoutRegionsJson regionsJson;

    @JsonCreator
    public LayoutComponentJson( @JsonProperty("name") final String name, @JsonProperty("template") final String template,
                                @JsonProperty("config") final List<DataJson> config,
                                final @JsonProperty("regions") List<RegionJson> regions )
    {
        super( newLayoutComponent().
            name( ComponentName.from( name ) ).
            template( template != null ? LayoutTemplateKey.from( template ) : null ).
            config( config != null ? new RootDataSetJson( config ).getRootDataSet() : null ).
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
