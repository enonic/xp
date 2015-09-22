package com.enonic.xp.convert;

import com.enonic.xp.resource.ResourceKey;

final class ResourceKeyConverter
    implements Converter<ResourceKey>
{
    @Override
    public Class<ResourceKey> getType()
    {
        return ResourceKey.class;
    }

    @Override
    public ResourceKey convert( final Object value )
    {
        if ( value instanceof ResourceKey )
        {
            return (ResourceKey) value;
        }

        return ResourceKey.from( value.toString() );
    }
}
