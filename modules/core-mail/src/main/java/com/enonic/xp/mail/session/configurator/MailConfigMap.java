package com.enonic.xp.mail.session.configurator;

import java.util.Map;

import com.google.common.base.Strings;

public class MailConfigMap
{
    private final Map<String, String> map;

    public MailConfigMap( final Map<String, String> map ) {
        this.map = map;
    }

    public String getString( final String name )
    {
        final String value = this.map.get( name );
        if ( Strings.isNullOrEmpty( value ) )
        {
            return null;
        }

        return value;
    }

    public String getString( final String name, final String defValue )
    {
        final String value = this.map.get( name );
        if ( Strings.isNullOrEmpty( value ) )
        {
            return defValue;
        }

        return value;
    }

    public boolean getBoolean( final String name, final boolean defValue )
    {
        final String value = getString( name );
        return value != null ? "true".equals( value ) : defValue;
    }

    public int getInt( final String name, final int defValue )
    {
        final String value = this.map.get( name );
        if ( Strings.isNullOrEmpty( value ) )
        {
            return defValue;
        }

        return Integer.parseInt( value );
    }
}
