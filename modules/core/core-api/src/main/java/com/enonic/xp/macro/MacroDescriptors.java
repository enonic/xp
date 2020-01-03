package com.enonic.xp.macro;

import java.util.Collection;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntitySet;

@PublicApi
public final class MacroDescriptors
    extends AbstractImmutableEntitySet<MacroDescriptor>
{
    private MacroDescriptors( final ImmutableSet<MacroDescriptor> list )
    {
        super( list );
    }

    public static MacroDescriptors from( final MacroDescriptor... macroDescriptors )
    {
        return new MacroDescriptors( ImmutableSet.copyOf( macroDescriptors ) );
    }

    public static MacroDescriptors from( final Collection<MacroDescriptor> macroDescriptors )
    {
        return new MacroDescriptors( ImmutableSet.copyOf( macroDescriptors ) );
    }

    public static MacroDescriptors from( final Iterable<MacroDescriptor>... macroDescriptors )
    {
        final ImmutableSet.Builder<MacroDescriptor> keys = ImmutableSet.builder();
        for ( Iterable<MacroDescriptor> keysParam : macroDescriptors )
        {
            keys.addAll( keysParam );
        }
        return new MacroDescriptors( keys.build() );
    }

    public static MacroDescriptors empty()
    {
        return new MacroDescriptors( ImmutableSet.of() );
    }

}
