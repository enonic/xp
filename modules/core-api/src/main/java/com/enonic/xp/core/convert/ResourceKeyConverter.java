package com.enonic.xp.core.convert;

import com.enonic.xp.core.resource.ResourceKey;

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
