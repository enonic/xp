package com.enonic.xp.core.impl.content.serializer;

import com.enonic.xp.region.ComponentType;
import com.enonic.xp.region.FragmentComponentType;
import com.enonic.xp.region.ImageComponentType;
import com.enonic.xp.region.LayoutComponentType;
import com.enonic.xp.region.PartComponentType;
import com.enonic.xp.region.TextComponentType;

public final class ComponentDataSerializerProvider
{
    private final PartComponentDataSerializer partDataSerializer;

    private final TextComponentDataSerializer textDataSerializer;

    private final LayoutComponentDataSerializer layoutDataSerializer;

    private final ImageComponentDataSerializer imageDataSerializer;

    private final FragmentComponentDataSerializer fragmentDataSerializer;

    private final RegionDataSerializer regionDataSerializer;

    public ComponentDataSerializerProvider()
    {
        this.regionDataSerializer = new RegionDataSerializer( this );
        this.partDataSerializer = new PartComponentDataSerializer();
        this.textDataSerializer = new TextComponentDataSerializer();
        this.imageDataSerializer = new ImageComponentDataSerializer();
        this.fragmentDataSerializer = new FragmentComponentDataSerializer();
        this.layoutDataSerializer = new LayoutComponentDataSerializer( this.regionDataSerializer );
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

    public RegionDataSerializer getRegionDataSerializer()
    {
        return regionDataSerializer;
    }

}
