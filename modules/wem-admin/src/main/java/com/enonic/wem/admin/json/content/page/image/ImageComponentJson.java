package com.enonic.wem.admin.json.content.page.image;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.admin.json.content.page.AbstractDescriptorBasedPageComponentJson;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.page.ComponentName;
import com.enonic.wem.api.content.page.image.ImageComponent;
import com.enonic.wem.api.content.page.image.ImageDescriptorKey;
import com.enonic.wem.api.data.DataJson;
import com.enonic.wem.api.data.RootDataSetJson;

import static com.enonic.wem.api.content.page.image.ImageComponent.newImageComponent;

@SuppressWarnings("UnusedDeclaration")
public class ImageComponentJson
    extends AbstractDescriptorBasedPageComponentJson<ImageComponent>
{
    private final ImageComponent image;

    @JsonCreator
    public ImageComponentJson( @JsonProperty("name") final String name, @JsonProperty("descriptor") final String descriptor,
                               @JsonProperty("config") final List<DataJson> config, @JsonProperty("image") final String image )
    {
        super( newImageComponent().
            name( ComponentName.from( name ) ).
            descriptor( descriptor != null ? ImageDescriptorKey.from( descriptor ) : null ).
            image( image != null ? ContentId.from( image ) : null ).
            config( config != null ? new RootDataSetJson( config ).getRootDataSet() : null ).
            build() );

        this.image = getComponent();
    }

    public ImageComponentJson( final ImageComponent component )
    {
        super( component );
        this.image = component;
    }

    public String getImage()
    {
        return image.getImage() != null ? image.getImage().toString() : null;
    }
}
