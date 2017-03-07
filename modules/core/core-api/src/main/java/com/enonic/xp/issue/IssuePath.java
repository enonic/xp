package com.enonic.xp.issue;

public class IssuePath
{

    private final static String PREFIX = "/issue/";

    private final String value;

    private IssuePath( IssueName issueName )
    {
        this.value = PREFIX + issueName;
    }

    public static IssuePath from( IssueName issueName )
    {
        return new IssuePath( issueName );
    }

    public String getValue()
    {
        return this.value;
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

        final IssuePath issuePath = (IssuePath) o;

        return value.equals( issuePath.value );

    }

    @Override
    public final int hashCode()
    {
        return value != null ? value.hashCode() : 0;
    }

    @Override
    public String toString()
    {
        return value;
    }
}
