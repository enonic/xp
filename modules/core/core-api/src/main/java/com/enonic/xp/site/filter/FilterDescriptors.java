package com.enonic.xp.site.filter;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.support.AbstractImmutableEntityList;

public final class FilterDescriptors
    extends AbstractImmutableEntityList<FilterDescriptor>
{

    private FilterDescriptors( final ImmutableList<FilterDescriptor> list )
    {
        super( list );
    }

    public static FilterDescriptors empty()
    {
        final ImmutableList<FilterDescriptor> list = ImmutableList.of();
        return new FilterDescriptors( list );
    }

    public static FilterDescriptors from( final FilterDescriptor... FilterDescriptors )
    {
        return new FilterDescriptors( ImmutableList.copyOf( FilterDescriptors ) );
    }

    public static FilterDescriptors from( final Iterable<FilterDescriptor> FilterDescriptors )
    {
        return new FilterDescriptors( ImmutableList.copyOf( FilterDescriptors ) );
    }
}
