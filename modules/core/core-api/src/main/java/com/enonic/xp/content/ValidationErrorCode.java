package com.enonic.xp.content;

import java.util.Objects;

import com.enonic.xp.app.ApplicationKey;

public final class ValidationErrorCode
{
    private static final String SEPARATOR = ":";

    private final ApplicationKey applicationKey;

    private final String code;

    private ValidationErrorCode( final ApplicationKey applicationKey, final String code )
    {
        this.applicationKey = applicationKey;
        this.code = Objects.requireNonNull( code );
    }

    public ApplicationKey getApplicationKey()
    {
        return applicationKey;
    }

    public String getCode()
    {
        return code;
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
        final ValidationErrorCode that = (ValidationErrorCode) o;
        return applicationKey.equals( that.applicationKey ) && code.equals( that.code );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( applicationKey, code );
    }

    @Override
    public String toString()
    {
        return applicationKey + SEPARATOR + code;
    }

    public static ValidationErrorCode from( final ApplicationKey applicationKey, final String code )
    {
        return new ValidationErrorCode( applicationKey, code );
    }

    public static ValidationErrorCode parse( final String value )
    {
        final int index = value.indexOf( SEPARATOR );
        if ( index != -1 )
        {
            return from( ApplicationKey.from( value.substring( 0, index ) ), value.substring( index + 1 ) );
        }
        else
        {
            throw new IllegalArgumentException( "Invalid ValidationErrorCode value " + value );
        }
    }
}
