package com.enonic.xp.core.impl.schema.mapper;

import com.fasterxml.jackson.annotation.JsonCreator;

import com.enonic.xp.descriptor.DescriptorKey;

public class DescriptorKeyMapper
{
    @JsonCreator
    public static DescriptorKey from( String value )
    {
        return DescriptorKey.from( value );
    }
}
