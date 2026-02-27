package com.enonic.xp.macro;

import java.util.Objects;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.DescriptorKey;

@PublicApi
public final class MacroKey
{
    private final DescriptorKey descriptorKey;

    private MacroKey( final DescriptorKey descriptorKey )
    {
        this.descriptorKey = descriptorKey;
    }

    public ApplicationKey getApplicationKey()
    {
        return descriptorKey.getApplicationKey();
    }

    public String getName()
    {
        return descriptorKey.getName();
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        final MacroKey that = (MacroKey) o;
        return Objects.equals( this.descriptorKey, that.descriptorKey );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( this.descriptorKey );
    }

    @Override
    public String toString()
    {
        return descriptorKey.toString();
    }

    public static MacroKey from( final String key )
    {
        return new MacroKey( DescriptorKey.from( key ) );
    }

    public static MacroKey from( final ApplicationKey applicationKey, final String macroName )
    {
        return new MacroKey( DescriptorKey.from( applicationKey, macroName ) );
    }
}
