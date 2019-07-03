package com.enonic.xp.admin.impl.rest.resource.layer;

import com.enonic.xp.layer.ContentLayer;

public class ContentLayerJson
{
    private String name;

    private String parentName;

    public ContentLayerJson( final ContentLayer contentLayer )
    {
        this.name = contentLayer.getName();
        this.parentName = contentLayer.getParentName();
    }

    public String getName()
    {
        return name;
    }

    public String getParentName()
    {
        return parentName;
    }
}
