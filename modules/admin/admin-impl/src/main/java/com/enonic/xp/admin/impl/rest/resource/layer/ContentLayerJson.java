package com.enonic.xp.admin.impl.rest.resource.layer;

import com.enonic.xp.layer.ContentLayer;

public class ContentLayerJson
{
    private String name;

    private String parentName;

    public ContentLayerJson( final ContentLayer contentLayer )
    {
        this.name = contentLayer.getName() == null ? null : contentLayer.getName().getValue();
        this.parentName = contentLayer.getParentName() == null ? null : contentLayer.getParentName().getValue();
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
