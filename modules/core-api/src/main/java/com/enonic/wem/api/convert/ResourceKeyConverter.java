package com.enonic.wem.api.convert;

import com.enonic.wem.api.resource.ResourceKey;

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
