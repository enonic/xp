package com.enonic.xp.image;

import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public final class ScaleParams
{
    public static final ScaleParams NO_SCALE = new ScaleParams( "full", null );

    private final String name;

    private final Object[] args;

    public ScaleParams( String name, Object[] args )
    {
        this.name = name;
        this.args = args != null ? args : new Object[0];
    }

    public String getName()
    {
        return this.name;
    }

    public Object[] getArguments()
    {
        return this.args;
    }

    /** Uses a style's aspect ratio as a hint for width/height; other scale modes retain their own geometry. */
    public ScaleParams withAspectRatio( final String aspectRatio )
    {
        if ( aspectRatio == null )
        {
            return this;
        }
        if ( !aspectRatio.matches( "[1-9][0-9]*:[1-9][0-9]*" ) )
        {
            throw new IllegalArgumentException( "Invalid image aspect ratio" );
        }
        final String[] ratio = aspectRatio.split( ":" );
        final int horizontal = Integer.parseInt( ratio[0] );
        final int vertical = Integer.parseInt( ratio[1] );
        if ( !( "width".equals( name ) || "height".equals( name ) ) )
        {
            return this;
        }
        if ( args.length != 1 || !( args[0] instanceof Number value ) )
        {
            throw new IllegalArgumentException( "Width or height requires one positive dimension" );
        }
        final int dimension = value.intValue();
        final long derived = "width".equals( name ) ? Math.round( (double) dimension * vertical / horizontal ) :
            Math.round( (double) dimension * horizontal / vertical );
        if ( dimension <= 0 || derived <= 0 || derived > Integer.MAX_VALUE )
        {
            throw new IllegalArgumentException( "Invalid image dimensions for aspect ratio" );
        }
        return new ScaleParams( "block", "width".equals( name ) ? new Object[]{dimension, (int) derived} :
            new Object[]{(int) derived, dimension} );
    }

    @Override
    public String toString()
    {
        return this.name + Stream.of( this.args ).map( this::encode ).collect( joining( ",", "(", ")" ) );
    }

    private String encode( Object arg )
    {
        if ( arg == null )
        {
            return "";
        }

        if ( arg instanceof String )
        {
            return quote( (String) arg );
        }
        else
        {
            return arg.toString();
        }
    }

    private String quote( String arg )
    {
        if ( arg.contains( "'" ) )
        {
            return "\"" + arg + "\"";
        }
        else
        {
            return "'" + arg + "'";
        }
    }
}
