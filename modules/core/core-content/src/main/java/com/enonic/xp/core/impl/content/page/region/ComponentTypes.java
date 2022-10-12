package com.enonic.xp.core.impl.content.page.region;

import java.util.Map;

import com.enonic.xp.region.ComponentType;
import com.enonic.xp.region.FragmentComponentType;
import com.enonic.xp.region.ImageComponentType;
import com.enonic.xp.region.LayoutComponentType;
import com.enonic.xp.region.PartComponentType;
import com.enonic.xp.region.TextComponentType;

public final class ComponentTypes
{
    private static final Map<String, ComponentType> BY_SHORT_NAME =
        Map.of( LayoutComponentType.INSTANCE.toString(), LayoutComponentType.INSTANCE, ImageComponentType.INSTANCE.toString(),
                ImageComponentType.INSTANCE, PartComponentType.INSTANCE.toString(), PartComponentType.INSTANCE,
                TextComponentType.INSTANCE.toString(), TextComponentType.INSTANCE, FragmentComponentType.INSTANCE.toString(),
                FragmentComponentType.INSTANCE );

    private ComponentTypes()
    {
    }

    public static ComponentType byShortName( final String shortName )
    {
        return BY_SHORT_NAME.get( shortName );
    }
}
