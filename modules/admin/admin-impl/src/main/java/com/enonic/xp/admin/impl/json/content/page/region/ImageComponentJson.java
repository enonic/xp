package com.enonic.xp.admin.impl.json.content.page.region;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.admin.impl.rest.resource.content.ComponentNameResolver;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.data.PropertyArrayJson;
import com.enonic.xp.data.PropertyTreeJson;
import com.enonic.xp.region.ImageComponent;

@SuppressWarnings("UnusedDeclaration")
public class ImageComponentJson
    extends ComponentJson<ImageComponent>
{
    private final ImageComponent image;

    private final List<PropertyArrayJson> config;

    @JsonCreator
    public ImageComponentJson( @JsonProperty("config") final List<PropertyArrayJson> config, @JsonProperty("image") final String image )
    {
        super( ImageComponent.create().
            image( image != null ? ContentId.from( image ) : null ).
            config( config != null ? PropertyTreeJson.fromJson( config ) : null ).
            build(), null );

        this.image = getComponent();
        this.config = null; // not needed when parsing JSON
    }

    public ImageComponentJson( final ImageComponent component, final ComponentNameResolver componentNameResolver )
    {
        super( component, componentNameResolver );
        this.image = component;
        this.config = this.image.hasConfig() ? PropertyTreeJson.toJson( this.image.getConfig() ) : null;
    }

    public String getImage()
    {
        return image.hasImage() ? image.getImage().toString() : null;
    }

    public List<PropertyArrayJson> getConfig()
    {
        return config;
    }
}
