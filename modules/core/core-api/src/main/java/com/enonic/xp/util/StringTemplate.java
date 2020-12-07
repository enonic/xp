package com.enonic.xp.util;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.enonic.xp.core.internal.Interpolator;

@Deprecated
public final class StringTemplate
{
    private static final Interpolator INTERPOLATOR = new Interpolator( "{{", "}}", '\\' );

    private final String template;

    public StringTemplate( final String template )
    {
        this.template = template;
    }

    public String apply( final Map<String, String> model )
    {
        return INTERPOLATOR.interpolate( this.template, model::get );
    }

    public static StringTemplate load( final Class<?> context, final String name )
    {
        final InputStream stream = context.getResourceAsStream( name );
        if ( stream == null )
        {
            throw new IllegalArgumentException( "Could not find resource [" + name + "]" );
        }
        try (stream)
        {
            final String value = new String( stream.readAllBytes(), StandardCharsets.UTF_8 );
            return new StringTemplate( value );
        }
        catch ( final Exception e )
        {
            throw Exceptions.unchecked( e );
        }
    }
}
