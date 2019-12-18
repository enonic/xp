package com.enonic.xp.site.processor;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.support.AbstractImmutableEntityList;

public final class ResponseProcessorDescriptors
    extends AbstractImmutableEntityList<ResponseProcessorDescriptor>
{

    private ResponseProcessorDescriptors( final ImmutableList<ResponseProcessorDescriptor> list )
    {
        super( list );
    }

    public static ResponseProcessorDescriptors empty()
    {
        return new ResponseProcessorDescriptors( ImmutableList.of() );
    }

    public static ResponseProcessorDescriptors from( final ResponseProcessorDescriptor... processorDescriptors )
    {
        return new ResponseProcessorDescriptors( ImmutableList.copyOf( processorDescriptors ) );
    }

    public static ResponseProcessorDescriptors from( final Iterable<ResponseProcessorDescriptor> responseDescriptors )
    {
        return new ResponseProcessorDescriptors( ImmutableList.copyOf( responseDescriptors ) );
    }
}
