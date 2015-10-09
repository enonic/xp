package com.enonic.xp.util;

import java.net.URL;
import java.util.Map;

import org.apache.commons.lang3.text.StrSubstitutor;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public final class StringTemplate
{
    private final String template;

    public StringTemplate( final String template )
    {
        this.template = template;
    }

    public String apply( final Map<String, String> model )
    {
        final StrSubstitutor substitutor = new StrSubstitutor( model, "{{", "}}", '\\' );
        return substitutor.replace( this.template );
    }

    public static StringTemplate load( final Class context, final String name )
    {
        final URL url = context.getResource( name );
        if ( url == null )
        {
            throw new IllegalArgumentException( "Could not find resource [" + name + "]" );
        }

        try
        {
            final String value = Resources.toString( url, Charsets.UTF_8 );
            return new StringTemplate( value );
        }
        catch ( final Exception e )
        {
            throw Exceptions.unchecked( e );
        }
    }
}
