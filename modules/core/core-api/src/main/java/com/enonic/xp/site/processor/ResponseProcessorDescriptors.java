package com.enonic.xp.site.processor;

import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.support.AbstractImmutableEntityList;

public final class ResponseProcessorDescriptors
    extends AbstractImmutableEntityList<ResponseProcessorDescriptor>
{
    public static final ResponseProcessorDescriptors EMPTY = new ResponseProcessorDescriptors( ImmutableList.of() );

    private ResponseProcessorDescriptors( final ImmutableList<ResponseProcessorDescriptor> list )
    {
        super( list );
    }

    public static ResponseProcessorDescriptors empty()
    {
        return EMPTY;
    }

    public static ResponseProcessorDescriptors from( final ResponseProcessorDescriptor... processorDescriptors )
    {
        return fromInternal( ImmutableList.copyOf( processorDescriptors ) );
    }

    public static ResponseProcessorDescriptors from( final Iterable<ResponseProcessorDescriptor> responseDescriptors )
    {
        return fromInternal( ImmutableList.copyOf( responseDescriptors ) );
    }

    private static ResponseProcessorDescriptors fromInternal( final ImmutableList<ResponseProcessorDescriptor> xDataMappings )
    {
        return xDataMappings.isEmpty() ? EMPTY : new ResponseProcessorDescriptors( xDataMappings );
    }

    public static Collector<ResponseProcessorDescriptor, ?, ResponseProcessorDescriptors> collector()
    {
        return Collectors.collectingAndThen( ImmutableList.toImmutableList(), ResponseProcessorDescriptors::fromInternal );
    }
}
