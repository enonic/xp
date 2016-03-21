package com.enonic.xp.macro;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Stream;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableSet;
import static java.util.stream.Collectors.toSet;

import com.enonic.xp.support.AbstractImmutableEntitySet;

@Beta
public class MacroDescriptors
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

    public static MacroDescriptors from( final String... macroDescriptors )
    {
        return new MacroDescriptors( parsePrincipalKeys( macroDescriptors ) );
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
        return new MacroDescriptors( ImmutableSet.<MacroDescriptor>of() );
    }

    private static ImmutableSet<MacroDescriptor> parsePrincipalKeys( final String... macroDescriptors )
    {
        final Set<MacroDescriptor> descriptorKeyList = Stream.of( macroDescriptors ).map( MacroDescriptor::from ).collect( toSet() );
        return ImmutableSet.copyOf( descriptorKeyList );
    }
}
