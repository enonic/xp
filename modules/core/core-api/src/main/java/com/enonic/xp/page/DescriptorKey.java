package com.enonic.xp.page;

import java.util.Objects;

import org.apache.commons.lang.StringUtils;

import com.google.common.annotations.Beta;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.util.CharacterChecker;

@Beta
public final class DescriptorKey
{
    protected static final String SEPARATOR = ":";

    private final ApplicationKey applicationKey;

    private final String name;

    private final String refString;

    private DescriptorKey( final ApplicationKey applicationKey, final String name )
    {
        this.applicationKey = applicationKey;
        this.name = CharacterChecker.check( name, "Not a valid name for DescriptorKey [" + name + "]" );
        this.refString = applicationKey.toString() + SEPARATOR + name;
    }

    public ApplicationKey getApplicationKey()
    {
        return applicationKey;
    }

    public String getName()
    {
        return name;
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

        final DescriptorKey that = (DescriptorKey) o;
        return Objects.equals( this.refString, that.refString );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( this.refString );
    }

    @Override
    public String toString()
    {
        return refString;
    }

    public static DescriptorKey from( final String key )
    {
        final String applicationKey = StringUtils.substringBefore( key, SEPARATOR );
        final String descriptorName = StringUtils.substringAfter( key, SEPARATOR );
        return new DescriptorKey( ApplicationKey.from( applicationKey ), descriptorName );
    }

    public static DescriptorKey from( final ApplicationKey applicationKey, final String descriptorName )
    {
        return new DescriptorKey( applicationKey, descriptorName );
    }
}
