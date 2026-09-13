package com.enonic.xp.image;

import java.util.stream.Stream;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static java.util.stream.Collectors.joining;

/**
 * A named image scaling operation and its arguments.
 */
@NullMarked
public final class ScaleParams
{
    /**
     * The {@code full()} operation, which preserves the source dimensions.
     */
    public static final ScaleParams NO_SCALE = new ScaleParams( "full", null );

    private final String name;

    private final @Nullable Object[] args;

    /**
     * Creates a scaling operation without validating its name or arguments.
     * The argument array is retained rather than copied.
     *
     * @param name the scaling function name, such as {@code width}, {@code block} or {@code full}
     * @param args the function arguments
     */
    public ScaleParams( String name, @Nullable Object @Nullable [] args )
    {
        this.name = name;
        this.args = args != null ? args : new Object[0];
    }

    /**
     * Returns the scaling function name.
     *
     * @return the function name
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Returns the argument array. Changes to this array affect the operation.
     *
     * @return the arguments
     */
    public @Nullable Object[] getArguments()
    {
        return this.args;
    }

    /**
     * Applies an aspect-ratio hint to a width or height operation without modifying this instance.
     * The specified dimension is retained and the other dimension is rounded to the nearest
     * pixel, producing a {@code block} crop. Other scaling operations retain their own geometry.
     * The caller can keep this original operation when generating a URL.
     *
     * @param aspectRatio the positive integer ratio {@code width:height}
     * @return a block operation for width/height with a ratio, or this instance otherwise
     * @throws IllegalArgumentException if the ratio is malformed or outside the integer range,
     *     or a derived width/height operation has invalid dimensions
     */
    public ScaleParams withAspectRatio( final @Nullable String aspectRatio )
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

    /**
     * Formats the operation as a function call, for example {@code width(640)}.
     * String arguments are quoted.
     *
     * @return the function-call representation
     */
    @Override
    public String toString()
    {
        return this.name + Stream.of( this.args ).map( this::encode ).collect( joining( ",", "(", ")" ) );
    }

    private String encode( @Nullable Object arg )
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
