package com.enonic.xp.content;

import java.util.Objects;

public final class I18nKey
{
    private final String key;

    private I18nKey( final String key )
    {
        this.key = Objects.requireNonNull( key );
    }

    public static I18nKey from( final String key )
    {
        return new I18nKey( key );
    }

    @Override
    public String toString()
    {
        return key;
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
        final I18nKey i18nKey = (I18nKey) o;
        return key.equals( i18nKey.key );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( key );
    }
}
