package com.enonic.xp.admin.impl.json.content.page.region;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.admin.impl.rest.resource.content.ComponentNameResolver;
import com.enonic.xp.data.PropertyArrayJson;
import com.enonic.xp.data.PropertyTreeJson;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.region.PartComponent;

@SuppressWarnings("UnusedDeclaration")
public class PartComponentJson
    extends DescriptorBasedComponentJson<PartComponent>
{
    private final PartComponent part;

    public PartComponentJson( final PartComponent component, final ComponentNameResolver componentNameResolver )
    {
        super( component, componentNameResolver );
        this.part = component;
    }

    @JsonCreator
    public PartComponentJson( @JsonProperty("descriptor") final String descriptor,
                              @JsonProperty("config") final List<PropertyArrayJson> config )
    {
        super( PartComponent.create().
            descriptor( descriptor != null ? DescriptorKey.from( descriptor ) : null ).
            config( config != null ? PropertyTreeJson.fromJson( config ) : null ).
            build(), null );

        this.part = getComponent();
    }
}
