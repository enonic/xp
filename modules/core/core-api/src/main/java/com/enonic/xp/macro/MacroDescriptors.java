package com.enonic.xp.macro;

import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public final class MacroDescriptors
    extends AbstractImmutableEntityList<MacroDescriptor>
{
    private static final MacroDescriptors EMPTY = new MacroDescriptors( ImmutableList.of() );

    private MacroDescriptors( final ImmutableList<MacroDescriptor> list )
    {
        super( list );
    }

    public static MacroDescriptors empty()
    {
        return EMPTY;
    }

    public static MacroDescriptors from( final MacroDescriptor... macroDescriptors )
    {
        return fromInternal( ImmutableList.copyOf( macroDescriptors ) );
    }

    public static MacroDescriptors from( final Iterable<MacroDescriptor> macroDescriptors )
    {
        return macroDescriptors instanceof MacroDescriptors m ? m : fromInternal( ImmutableList.copyOf( macroDescriptors ) );
    }

    public static Collector<? super MacroDescriptor, ?, MacroDescriptors> collector()
    {
        return Collectors.collectingAndThen( ImmutableList.toImmutableList(), MacroDescriptors::fromInternal );
    }

    private static MacroDescriptors fromInternal( final ImmutableList<MacroDescriptor> list )
    {
        return list.isEmpty() ? EMPTY : new MacroDescriptors( list );
    }
}
