package com.enonic.xp.app;

import java.util.Collection;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.support.AbstractImmutableEntitySet;

@Beta
public final class ApplicationDescriptors
    extends AbstractImmutableEntitySet<ApplicationDescriptor>
{
    private ApplicationDescriptors( final ImmutableSet<ApplicationDescriptor> list )
    {
        super( list );
    }

    public static ApplicationDescriptors from( final ApplicationDescriptor... applicationDescriptors )
    {
        return new ApplicationDescriptors( ImmutableSet.copyOf( applicationDescriptors ) );
    }

    public static ApplicationDescriptors from( final Collection<ApplicationDescriptor> applicationDescriptors )
    {
        return new ApplicationDescriptors( ImmutableSet.copyOf( applicationDescriptors ) );
    }

    public static ApplicationDescriptors from( final Iterable<ApplicationDescriptor>... applicationDescriptors )
    {
        final ImmutableSet.Builder<ApplicationDescriptor> keys = ImmutableSet.builder();
        for ( Iterable<ApplicationDescriptor> keysParam : applicationDescriptors )
        {
            keys.addAll( keysParam );
        }
        return new ApplicationDescriptors( keys.build() );
    }

    public static ApplicationDescriptors empty()
    {
        return new ApplicationDescriptors( ImmutableSet.of() );
    }

}
