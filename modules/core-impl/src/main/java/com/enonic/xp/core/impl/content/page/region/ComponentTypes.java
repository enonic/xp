package com.enonic.xp.core.impl.content.page.region;

import java.util.LinkedHashMap;

import com.google.common.collect.Maps;

import com.enonic.wem.api.content.page.region.ComponentType;
import com.enonic.wem.api.content.page.region.ImageComponentType;
import com.enonic.wem.api.content.page.region.LayoutComponentType;
import com.enonic.wem.api.content.page.region.PartComponentType;
import com.enonic.wem.api.content.page.region.TextComponentType;

final class ComponentTypes
{
    private final static ComponentTypes INSTANCE = new ComponentTypes();

    private final LinkedHashMap<String, ComponentType> bySimpleClassName;

    private ComponentTypes()
    {
        this.bySimpleClassName = Maps.newLinkedHashMap();
        register( LayoutComponentType.INSTANCE );
        register( ImageComponentType.INSTANCE );
        register( PartComponentType.INSTANCE );
        register( TextComponentType.INSTANCE );
    }

    private void register( final ComponentType type )
    {
        this.bySimpleClassName.put( type.getComponentClass().getSimpleName(), type );
    }

    public static ComponentType bySimpleClassName( final String simpleClassName )
    {
        return INSTANCE.bySimpleClassName.get( simpleClassName );
    }
}
