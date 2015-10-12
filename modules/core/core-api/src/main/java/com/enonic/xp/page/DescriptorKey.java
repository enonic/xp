package com.enonic.xp.page;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.google.common.annotations.Beta;

import com.enonic.xp.app.ApplicationKey;

@Beta
public final class DescriptorKey
{
    protected static final String SEPARATOR = ":";

    private final ApplicationKey applicationKey;

    private final String name;

    private final String refString;

    public DescriptorKey( final ApplicationKey applicationKey, final String name )
    {
        this.applicationKey = applicationKey;
        this.name = name;
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

    public static DescriptorKey from( final String s )
    {
        final String applicationKey = StringUtils.substringBefore( s, SEPARATOR );
        final String descriptorName = StringUtils.substringAfter( s, SEPARATOR );
        return new DescriptorKey( ApplicationKey.from( applicationKey ), descriptorName );
    }

    public static DescriptorKey from( final ApplicationKey applicationKey, final String descriptorName )
    {
        return new DescriptorKey( applicationKey, descriptorName );
    }
}
