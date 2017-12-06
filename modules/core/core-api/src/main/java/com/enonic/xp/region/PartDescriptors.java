package com.enonic.xp.region;

import java.util.Collection;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;

import com.enonic.xp.support.AbstractImmutableEntityList;

@Beta
public final class PartDescriptors
    extends AbstractImmutableEntityList<PartDescriptor>
{
    private PartDescriptors( final ImmutableList<PartDescriptor> list )
    {
        super( list );
    }

    public static PartDescriptors empty()
    {
        final ImmutableList<PartDescriptor> list = ImmutableList.of();
        return new PartDescriptors( list );
    }

    public static PartDescriptors from( final PartDescriptor... descriptors )
    {
        return descriptors != null ? new PartDescriptors( ImmutableList.copyOf( descriptors ) ) : PartDescriptors.empty();
    }

    public static PartDescriptors from( final Iterable<? extends PartDescriptor> descriptors )
    {
        return descriptors != null ? new PartDescriptors( ImmutableList.copyOf( descriptors ) ) : PartDescriptors.empty();
    }

    public static PartDescriptors from( final Collection<? extends PartDescriptor> descriptors )
    {
        return descriptors != null ? new PartDescriptors( ImmutableList.copyOf( descriptors ) ) : PartDescriptors.empty();
    }
}
