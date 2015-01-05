package com.enonic.wem.api.content.page.region;

import java.util.LinkedHashMap;

import com.google.common.collect.Maps;

public final class ComponentTypes
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
