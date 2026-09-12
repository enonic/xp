package com.enonic.xp.core.impl.image;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.enonic.xp.core.impl.image.effect.ImageScaleFunction;
import com.enonic.xp.core.impl.image.parser.CommandArgumentParser;
import com.enonic.xp.core.impl.image.parser.FilterExpr;
import com.enonic.xp.media.ImageOrientation;

/** A bounded plan containing only fixed operators and parsed numeric values, never caller-supplied expressions. */
final class ImageMagickTransformPlan
{
    private final List<String> operations = new ArrayList<>();

    private final long maxPixels;

    private int width;

    private int height;

    private long peakPixels;

    private final boolean alpha;

    ImageMagickTransformPlan( final int width, final int height, final NormalizedImageParams params,
                              final ImageScaleFunction scale, final long maxPixels )
    {
        this( params, ImageGeometry.calculate( width, height, params, scale, maxPixels ), width, height, maxPixels );
    }

    static ImageMagickTransformPlan fromGeometry( final int width, final int height, final NormalizedImageParams params,
                                                  final ImageGeometry geometry, final long maxPixels )
    {
        return new ImageMagickTransformPlan( params, geometry, width, height, maxPixels );
    }

    private ImageMagickTransformPlan( final NormalizedImageParams params, final ImageGeometry geometry,
                                      final int width, final int height, final long maxPixels )
    {
        this.maxPixels = maxPixels;
        dimensions( width, height );
        orient( params.getOrientation() );
        if ( geometry.cropped() )
        {
            crop( geometry.cropWidth(), geometry.cropHeight(), geometry.cropX(), geometry.cropY() );
        }
        if ( geometry.scale() != null )
        {
            final var values = geometry.scale();
            dimensions( values.newWidth, values.newHeight );
            add( "-filter", "Lanczos", "-resize", geometry() + "!" );
            if ( values.subimage() )
            {
                crop( values.viewWidth, values.viewHeight, values.widthOffset, values.heightOffset );
            }
        }
        for ( FilterExpr filter : params.getFilterParam().getList() )
        {
            filter( filter );
        }
        this.alpha = NormalizedImageParams.supportsAlpha( params.getFormat() );
        if ( !alpha )
        {
            add( "-background", color( params.getBackgroundColor() ), "-alpha", "remove", "-alpha", "off" );
        }
    }

    List<String> operations()
    {
        return List.copyOf( operations );
    }

    int width()
    {
        return width;
    }

    int height()
    {
        return height;
    }

    long peakPixels()
    {
        return peakPixels;
    }

    boolean alpha()
    {
        return alpha;
    }

    private void dimensions( final int width, final int height )
    {
        final long pixels = (long) width * height;
        if ( width < 1 || height < 1 || pixels > maxPixels )
        {
            throw new IllegalArgumentException( "Transformation exceeds processing.maxPixels or has invalid dimensions" );
        }
        this.width = width;
        this.height = height;
        peakPixels = Math.max( peakPixels, pixels );
    }

    private void orient( final ImageOrientation orientation )
    {
        switch ( orientation )
        {
            case TopRight -> add( "-flop" );
            case BottomRight -> add( "-rotate", "180" );
            case BottomLeft -> add( "-flip" );
            case LeftTop ->
            {
                add( "-transpose" );
                dimensions( height, width );
            }
            case RightTop -> rotate( 90 );
            case RightBottom ->
            {
                add( "-transverse" );
                dimensions( height, width );
            }
            case LeftBottom -> rotate( 270 );
            default -> { }
        }
    }

    private void crop( final int width, final int height, final int x, final int y )
    {
        dimensions( width, height );
        add( "-crop", geometry() + "+" + x + "+" + y, "+repage" );
    }

    private void rotate( final int angle )
    {
        add( "-rotate", Integer.toString( angle ), "+repage" );
        if ( angle != 180 )
        {
            dimensions( height, width );
        }
    }

    private void filter( final FilterExpr filter )
    {
        final Object[] args = filter.getArguments();
        switch ( filter.getName() )
        {
            case "fliph", "flipv" -> add( "-flop" ); // Preserve XP's existing flipv alias of fliph.
            case "rotate90" -> rotate( 90 );
            case "rotate180" -> rotate( 180 );
            case "rotate270" -> rotate( 270 );
            case "invert" -> add( "-channel", "RGB", "-negate", "+channel" );
            case "grayscale" -> fx( "(r*77+g*151+b*28)/256" );
            case "gamma" -> add( "-channel", "RGB", "-gamma", number( args, 0, 0 ), "+channel" );
            case "blur" -> {
                final int radius = integer( args, 0, 2 );
                if ( radius > 0 )
                {
                    add( "-channel", "RGBA", "-blur", radius + "x" + ( radius / 3.0 ), "+channel" );
                }
            }
            case "sharpen" -> add( "-channel", "RGB", "-convolve", "0,-1,0,-1,5,-1,0,-1,0", "+channel" );
            case "bump" -> add( "-channel", "RGB", "-convolve", "-1,-1,0,-1,1,1,0,1,1", "+channel" );
            case "edge" -> add( "-channel", "RGB", "-edge", "1", "+channel" );
            case "emboss" -> add( "-channel", "RGB", "-emboss", "1", "+channel" );
            case "block" -> {
                final int size = integer( args, 0, 2 );
                add( "-scale", Math.max( 1, ( width + size - 1 ) / size ) + "x" + Math.max( 1, ( height + size - 1 ) / size ) + "!",
                     "-scale", geometry() + "!" );
            }
            case "border" -> border( integer( args, 0, 2 ), integer( args, 1, 0 ) );
            case "rounded" -> rounded( integer( args, 0, 10 ), integer( args, 1, 0 ), integer( args, 2, 0 ) );
            case "colorize" -> {
                fx( "floor((r+g+b)*255/3)/255" );
                multiply( "R", Math.max( 0, decimal( args, 0, 1 ) ) );
                multiply( "G", Math.max( 0, decimal( args, 1, 1 ) ) );
                multiply( "B", Math.max( 0, decimal( args, 2, 1 ) ) );
            }
            case "rgbadjust" -> {
                multiply( "R", 1 + decimal( args, 0, 0 ) );
                multiply( "G", 1 + decimal( args, 1, 0 ) );
                multiply( "B", 1 + decimal( args, 2, 0 ) );
            }
            case "sepia" -> {
                final double depth = Math.max( 0, integer( args, 0, 20 ) ) / 255.0;
                fx( "floor((r+g+b)*255/3)/255" );
                add( "-channel", "R", "-evaluate", "Add", depth * 200 + "%", "+channel",
                     "-channel", "G", "-evaluate", "Add", depth * 100 + "%", "+channel" );
            }
            case "hsbadjust" -> {
                final String h = number( args, 0, 0 );
                final String s = number( args, 1, 0 );
                final String b = number( args, 2, 0 );
                add( "-colorspace", "HSB" );
                fx( "channel(mod(r+(" + h + ")+ceil(abs(" + h + "))+1,1),min(1,max(0,g+(" + s + "))),min(1,max(0,b+(" + b + "))))" );
                add( "-colorspace", "sRGB" );
            }
            case "hsbcolorize" -> {
                final Color c = new Color( integer( args, 0, 0xffffff ) );
                final float[] hsb = Color.RGBtoHSB( c.getRed(), c.getGreen(), c.getBlue(), null );
                add( "-colorspace", "HSB" );
                fx( "channel(" + hsb[0] + ",g*" + hsb[1] + ",b*(g*" + hsb[2] + "+1-g))" );
                add( "-colorspace", "sRGB" );
            }
            default -> throw new IllegalArgumentException( "Unsupported native transformation filter " + filter.getName() );
        }
    }

    private void border( final int size, final int rgb )
    {
        if ( size == 0 )
        {
            return;
        }
        add( "-fill", color( rgb ), "-stroke", "none", "-draw",
             rectangle( 0, 0, width - 1, Math.min( size, height ) - 1 ) + " " +
             rectangle( 0, 0, Math.min( size, width ) - 1, height - 1 ) + " " +
             rectangle( Math.max( 0, width - size ), 0, width - 1, height - 1 ) + " " +
             rectangle( 0, Math.max( 0, height - size ), width - 1, height - 1 ) );
    }

    private void rounded( final int radius, final int border, final int rgb )
    {
        // Generate masks from cloned rasters, avoiding external resources or caller-supplied drawing programs.
        add( "(", "+clone", "-alpha", "off", "-fill", "black", "-colorize", "100", "-fill", "white", "-stroke", "none" );
        if ( width > border * 2 && height > border * 2 )
        {
            add( "-draw", roundRectangle( border, Math.max( 0, radius - border ) ) );
        }
        add( "-alpha", "copy", ")", "-compose", "DstIn", "-composite" );
        if ( border > 0 )
        {
            add( "-compose", "Over", "(", "+clone", "-alpha", "off", "-fill", color( rgb ), "-colorize", "100", "-alpha", "on",
                 "(", "+clone", "-fill", "black", "-colorize", "100", "-fill", "white", "-stroke", "none",
                 "-draw", roundRectangle( 0, radius ), "-alpha", "copy", ")", "-compose", "DstIn", "-composite", ")",
                 "+swap", "-compose", "Over", "-composite" );
        }
        add( "-compose", "Over" );
    }

    private String roundRectangle( final int inset, final int radius )
    {
        final double rx = Math.min( radius, ( width - 2 * inset - 1 ) / 2.0 );
        final double ry = Math.min( radius, ( height - 2 * inset - 1 ) / 2.0 );
        if ( rx <= 0 || ry <= 0 )
        {
            return rectangle( inset, inset, width - inset - 1, height - inset - 1 );
        }
        return "roundrectangle " + inset + "," + inset + " " + ( width - inset - 1 ) + "," + ( height - inset - 1 ) +
            " " + rx + "," + ry;
    }

    private static String rectangle( final int x, final int y, final int right, final int bottom )
    {
        return "rectangle " + x + "," + y + " " + right + "," + bottom;
    }

    private void multiply( final String channel, final double value )
    {
        add( "-channel", channel, "-evaluate", "Multiply", Double.toString( value ), "+channel" );
    }

    private void fx( final String expression )
    {
        add( "-channel", "RGB", "-fx", expression, "+channel" );
    }

    private static int integer( final Object[] args, final int index, final int fallback )
    {
        return CommandArgumentParser.getIntArg( args, index, fallback );
    }

    private static double decimal( final Object[] args, final int index, final float fallback )
    {
        final float value = CommandArgumentParser.getFloatArg( args, index, fallback );
        if ( !Float.isFinite( value ) )
        {
            throw new IllegalArgumentException( "Filter arguments must be finite" );
        }
        return value;
    }

    private static String number( final Object[] args, final int index, final float fallback )
    {
        return Double.toString( decimal( args, index, fallback ) );
    }

    private static String color( final int rgb )
    {
        return String.format( Locale.ROOT, "#%06x", rgb & 0xffffff );
    }

    private String geometry()
    {
        return width + "x" + height;
    }

    private void add( final String... args )
    {
        operations.addAll( List.of( args ) );
    }
}
