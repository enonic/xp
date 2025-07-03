package com.enonic.xp.macro;

import java.util.Collection;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntityList;
import com.enonic.xp.support.AbstractImmutableEntitySet;

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

    public static MacroDescriptors from( final Collection<MacroDescriptor> macroDescriptors )
    {
        return fromInternal( ImmutableList.copyOf( macroDescriptors ) );
    }

    private static MacroDescriptors fromInternal( final ImmutableList<MacroDescriptor> list )
    {
        if ( list.isEmpty() )
        {
            return EMPTY;
        }
        else
        {
            return new MacroDescriptors( list );
        }
    }
}
