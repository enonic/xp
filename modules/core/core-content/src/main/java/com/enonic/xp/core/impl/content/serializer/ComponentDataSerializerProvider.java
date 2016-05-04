package com.enonic.xp.core.impl.content.serializer;

import com.enonic.xp.region.ComponentType;
import com.enonic.xp.region.FragmentComponentType;
import com.enonic.xp.region.ImageComponentType;
import com.enonic.xp.region.LayoutComponentType;
import com.enonic.xp.region.PartComponentType;
import com.enonic.xp.region.TextComponentType;

public final class ComponentDataSerializerProvider
{
    private final PartComponentDataSerializer partDataSerializer = new PartComponentDataSerializer();

    private final TextComponentDataSerializer textDataSerializer = new TextComponentDataSerializer();

    private final LayoutComponentDataSerializer layoutDataSerializer = new LayoutComponentDataSerializer();

    private final ImageComponentDataSerializer imageDataSerializer = new ImageComponentDataSerializer();

    private final FragmentComponentDataSerializer fragmentDataSerializer = new FragmentComponentDataSerializer();

    public ComponentDataSerializerProvider()
    {
    }

    public ComponentDataSerializer getDataSerializer( final ComponentType componentType )
    {
        if ( componentType instanceof PartComponentType )
        {
            return partDataSerializer;
        }
        if ( componentType instanceof LayoutComponentType )
        {
            return layoutDataSerializer;
        }
        if ( componentType instanceof TextComponentType )
        {
            return textDataSerializer;
        }
        if ( componentType instanceof ImageComponentType )
        {
            return imageDataSerializer;
        }
        if ( componentType instanceof FragmentComponentType )
        {
            return fragmentDataSerializer;
        }
        else
        {
            return null;
        }
    }

}
