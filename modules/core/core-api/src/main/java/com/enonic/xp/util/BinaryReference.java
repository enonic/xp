package com.enonic.xp.util;

import org.apache.commons.lang.StringUtils;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

@Beta
public class BinaryReference
{
    private final String value;

    private BinaryReference( final String value )
    {
        this.value = sanitizeValue( value );
    }

    public static BinaryReference from( final String value )
    {
        Preconditions.checkArgument( !Strings.isNullOrEmpty( value ), "BinaryReference must not be null or empty" );
        return new BinaryReference( value );
    }

    @Override
    public String toString()
    {
        return this.value;
    }

    private String sanitizeValue( final String value )
    {
        return StringUtils.stripToEmpty( value ).replaceAll( "[\\\\/:\"*?<>|]+", "" );
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

        final BinaryReference that = (BinaryReference) o;

        if ( value != null ? !value.equals( that.value ) : that.value != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return value != null ? value.hashCode() : 0;
    }
}


