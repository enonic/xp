package com.enonic.xp.core.impl.content.page.region;

import java.util.LinkedHashMap;

import com.enonic.xp.region.ComponentType;
import com.enonic.xp.region.FragmentComponentType;
import com.enonic.xp.region.ImageComponentType;
import com.enonic.xp.region.LayoutComponentType;
import com.enonic.xp.region.PartComponentType;
import com.enonic.xp.region.TextComponentType;

public final class ComponentTypes
{
    private final static ComponentTypes INSTANCE = new ComponentTypes();

    private final LinkedHashMap<String, ComponentType> byShortName;

    private ComponentTypes()
    {
        this.byShortName = new LinkedHashMap<>();
        register( LayoutComponentType.INSTANCE );
        register( ImageComponentType.INSTANCE );
        register( PartComponentType.INSTANCE );
        register( TextComponentType.INSTANCE );
        register( FragmentComponentType.INSTANCE );
    }

    private void register( final ComponentType type )
    {
        this.byShortName.put( type.toString(), type );
    }

    public static ComponentType byShortName( final String shortName )
    {
        return INSTANCE.byShortName.get( shortName );
    }
}
