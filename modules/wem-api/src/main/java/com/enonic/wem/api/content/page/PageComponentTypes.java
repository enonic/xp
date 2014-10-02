package com.enonic.wem.api.content.page;

import java.util.LinkedHashMap;

import com.google.common.collect.Maps;

import com.enonic.wem.api.content.page.image.ImageComponentType;
import com.enonic.wem.api.content.page.layout.LayoutComponentType;
import com.enonic.wem.api.content.page.part.PartComponentType;
import com.enonic.wem.api.content.page.text.TextComponentType;

public final class PageComponentTypes
{
    private final static PageComponentTypes INSTANCE = new PageComponentTypes();

    private final LinkedHashMap<String, PageComponentType> bySimpleClassName;

    private PageComponentTypes()
    {
        this.bySimpleClassName = Maps.newLinkedHashMap();
        register( LayoutComponentType.INSTANCE );
        register( ImageComponentType.INSTANCE );
        register( PartComponentType.INSTANCE );
        register( TextComponentType.INSTANCE );
    }

    private void register( final PageComponentType type )
    {
        this.bySimpleClassName.put( type.getComponentClass().getSimpleName(), type );
    }

    public static PageComponentType bySimpleClassName( final String simpleClassName )
    {
        return INSTANCE.bySimpleClassName.get( simpleClassName );
    }
}
