package com.enonic.xp.region;

import java.util.Collection;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public final class PartDescriptors
    extends AbstractImmutableEntityList<PartDescriptor>
{
    private PartDescriptors( final ImmutableList<PartDescriptor> list )
    {
        super( list );
    }

    public static PartDescriptors empty()
    {
        return new PartDescriptors( ImmutableList.of() );
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
