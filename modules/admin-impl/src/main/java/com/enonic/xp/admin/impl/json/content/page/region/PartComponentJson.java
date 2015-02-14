package com.enonic.xp.admin.impl.json.content.page.region;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.core.content.page.DescriptorKey;
import com.enonic.xp.core.content.page.region.ComponentName;
import com.enonic.xp.core.content.page.region.PartComponent;
import com.enonic.xp.core.data.PropertyArrayJson;
import com.enonic.xp.core.data.PropertyTreeJson;

import static com.enonic.xp.core.content.page.region.PartComponent.newPartComponent;

@SuppressWarnings("UnusedDeclaration")
public class PartComponentJson
    extends DescriptorBasedComponentJson<PartComponent>
{
    private final PartComponent part;

    public PartComponentJson( final PartComponent component )
    {
        super( component );
        this.part = component;
    }

    @JsonCreator
    public PartComponentJson( @JsonProperty("name") final String name, @JsonProperty("descriptor") final String descriptor,
                              @JsonProperty("config") final List<PropertyArrayJson> config )
    {
        super( newPartComponent().
            name( name != null ? ComponentName.from( name ) : null ).
            descriptor( descriptor != null ? DescriptorKey.from( descriptor ) : null ).
            config( config != null ? PropertyTreeJson.fromJson( config ) : null ).
            build() );

        this.part = getComponent();
    }
}
