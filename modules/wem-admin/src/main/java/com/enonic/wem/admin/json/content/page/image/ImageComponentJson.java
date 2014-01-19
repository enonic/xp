package com.enonic.wem.admin.json.content.page.image;


import com.enonic.wem.admin.json.content.page.PageComponentJson;
import com.enonic.wem.api.content.page.image.ImageComponent;

@SuppressWarnings("UnusedDeclaration")
public class ImageComponentJson
    extends PageComponentJson
{
    private final ImageComponent image;

    public ImageComponentJson( final ImageComponent component )
    {
        super( component );
        this.image = component;
    }

    public String getName()
    {
        return image.getName().toString();
    }

    public String getImage()
    {
        return image.getImage() != null ? image.getImage().toString() : null;
    }
}
