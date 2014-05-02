package com.enonic.wem.api.content.page;

import com.enonic.wem.api.content.page.image.ImageComponent;
import com.enonic.wem.api.content.page.layout.LayoutComponent;
import com.enonic.wem.api.content.page.part.PartComponent;


public enum PageComponentType
{
    IMAGE( "image", ImageComponent.class ),
    PART( "part", PartComponent.class ),
    LAYOUT( "layout", LayoutComponent.class ),
    TEXT( "text", LayoutComponent.class );

    private Class clazz;

    private String shortName;

    PageComponentType( final String shortName, final Class clazz )
    {
        this.shortName = shortName;
        this.clazz = clazz;
    }

    public String toString()
    {
        return shortName;
    }
}
