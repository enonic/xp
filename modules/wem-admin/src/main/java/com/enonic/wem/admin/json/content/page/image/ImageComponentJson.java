package com.enonic.wem.admin.json.content.page.image;


import java.util.List;

import com.enonic.wem.admin.json.content.page.PageComponentJson;
import com.enonic.wem.admin.json.data.DataJson;
import com.enonic.wem.admin.json.data.RootDataSetJson;
import com.enonic.wem.api.content.page.image.ImageComponent;

@SuppressWarnings("UnusedDeclaration")
public class ImageComponentJson
    extends PageComponentJson
{
    private final ImageComponent image;

    private final List<DataJson> config;

    public ImageComponentJson( final ImageComponent component )
    {
        super( component );
        this.image = component;
        this.config = new RootDataSetJson( image.getConfig() ).getSet();
    }

    public String getName()
    {
        return image.getName().toString();
    }

    public String getImage()
    {
        return image.getImage().toString();
    }

    public List<DataJson> getConfig()
    {
        return config;
    }
}
