package com.enonic.wem.admin.json.content.page.image;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.admin.json.content.page.AbstractPageComponentJson;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.page.ComponentName;
import com.enonic.wem.api.content.page.image.ImageComponent;
import com.enonic.wem.api.data.DataJson;
import com.enonic.wem.api.data.RootDataSetJson;

import static com.enonic.wem.api.content.page.image.ImageComponent.newImageComponent;

@SuppressWarnings("UnusedDeclaration")
public class ImageComponentJson
    extends AbstractPageComponentJson<ImageComponent>
{
    private final ImageComponent image;

    private final List<DataJson> config;

    @JsonCreator
    public ImageComponentJson( @JsonProperty("name") final String name, @JsonProperty("descriptor") final String descriptor,
                               @JsonProperty("config") final List<DataJson> config, @JsonProperty("image") final String image )
    {
        super( newImageComponent().
            name( ComponentName.from( name ) ).
            image( image != null ? ContentId.from( image ) : null ).
            config( config != null ? new RootDataSetJson( config ).getRootDataSet() : null ).
            build() );

        this.image = getComponent();
        this.config = null; // not needed when parsing JSON
    }

    public ImageComponentJson( final ImageComponent component )
    {
        super( component );
        this.image = component;
        this.config = this.image.getConfig() != null ? new RootDataSetJson( this.image.getConfig() ).getSet() : null;
    }

    public String getImage()
    {
        return image.getImage() != null ? image.getImage().toString() : null;
    }

    public List<DataJson> getConfig()
    {
        return config;
    }
}
