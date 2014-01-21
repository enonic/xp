package com.enonic.wem.admin.json.content.page.image;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.admin.json.content.page.PageComponentJson;
import com.enonic.wem.admin.json.data.DataJson;
import com.enonic.wem.admin.json.data.RootDataSetJson;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.page.ComponentName;
import com.enonic.wem.api.content.page.image.ImageComponent;
import com.enonic.wem.api.content.page.image.ImageTemplateKey;

import static com.enonic.wem.api.content.page.image.ImageComponent.newImageComponent;

@SuppressWarnings("UnusedDeclaration")
public class ImageComponentJson
    extends PageComponentJson<ImageComponent>
{
    private final ImageComponent image;

    @JsonCreator
    public ImageComponentJson( @JsonProperty("name") final String name, @JsonProperty("template") final String template,
                               @JsonProperty("config") final List<DataJson> config, @JsonProperty("image") final String image )
    {
        super( newImageComponent().
            name( ComponentName.from( name ) ).
            template( template != null ? ImageTemplateKey.from( template ) : null ).
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
