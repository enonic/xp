package com.enonic.xp.region;

import java.util.Collection;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;

import com.enonic.xp.support.AbstractImmutableEntityList;

@Beta
public final class LayoutDescriptors
    extends AbstractImmutableEntityList<LayoutDescriptor>
{
    private LayoutDescriptors( final ImmutableList<LayoutDescriptor> list )
    {
        super( list );
    }

    public static LayoutDescriptors empty()
    {
        final ImmutableList<LayoutDescriptor> list = ImmutableList.of();
        return new LayoutDescriptors( list );
    }

    public static LayoutDescriptors from( final LayoutDescriptor... descriptors )
    {
        return descriptors != null ? new LayoutDescriptors( ImmutableList.copyOf( descriptors ) ) : LayoutDescriptors.empty();
    }

    public static LayoutDescriptors from( final Iterable<? extends LayoutDescriptor> descriptors )
    {
        return descriptors != null ? new LayoutDescriptors( ImmutableList.copyOf( descriptors ) ) : LayoutDescriptors.empty();
    }

    public static LayoutDescriptors from( final Collection<? extends LayoutDescriptor> descriptors )
    {
        return descriptors != null ? new LayoutDescriptors( ImmutableList.copyOf( descriptors ) ) : LayoutDescriptors.empty();
    }
}
