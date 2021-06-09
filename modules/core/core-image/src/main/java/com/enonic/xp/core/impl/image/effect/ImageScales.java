package com.enonic.xp.core.impl.image.effect;

import com.google.common.base.Preconditions;

import com.enonic.xp.core.impl.image.parser.CommandArgumentParser;
import com.enonic.xp.image.FocalPoint;

public class ImageScales
{
    private final int maxSideLength;

    public ImageScales( final int maxSideLength )
    {
        this.maxSideLength = maxSideLength;
    }

    public ImageScaleFunction block( FocalPoint focalPoint, Object... args )
    {
        Preconditions.checkArgument( args.length <= 2, "Too many arguments %s", args.length );

        final int width = CommandArgumentParser.getIntArg( args, 0, 0 );
        final int height = CommandArgumentParser.getIntArg( args, 1, 0 );
        final double xOffset = focalPoint.xOffset();
        final double yOffset = focalPoint.yOffset();

        Preconditions.checkArgument( width > 0 && width <= maxSideLength, "width value must be between 0 and %s : %s", maxSideLength,
                                     width );
        Preconditions.checkArgument( height > 0 && height <= maxSideLength, "height value must be between 0 and %s : %s", maxSideLength,
                                     height );

        return adaptScaleCalculator( ScaleCalculator.block( width, height, xOffset, yOffset ) );
    }

    public ImageScaleFunction square( FocalPoint focalPoint, Object... args )
    {
        Preconditions.checkArgument( args.length <= 1, "Too many arguments %s", args.length );

        final int size = CommandArgumentParser.getIntArg( args, 0, 0 );
        final double xOffset = focalPoint.xOffset();
        final double yOffset = focalPoint.yOffset();

        Preconditions.checkArgument( size > 0 && size <= maxSideLength, "size value must be between 0 and %s : %s", maxSideLength, size );

        return adaptScaleCalculator( ScaleCalculator.block( size, size, xOffset, yOffset ) );
    }

    public ImageScaleFunction max( FocalPoint focalPoint, Object... args )
    {
        Preconditions.checkArgument( args.length <= 1, "Too many arguments %s", args.length );

        final int size = CommandArgumentParser.getIntArg( args, 0, 0 );

        Preconditions.checkArgument( size > 0 && size <= maxSideLength, "size value must be between 0 and %s : %s", maxSideLength, size );

        return adaptScaleCalculator( ScaleCalculator.max( size ) );
    }

    public ImageScaleFunction wide( FocalPoint focalPoint, Object... args )
    {
        Preconditions.checkArgument( args.length <= 2, "Too many arguments %s", args.length );

        final int width = CommandArgumentParser.getIntArg( args, 0, 0 );
        final int height = CommandArgumentParser.getIntArg( args, 1, 0 );
        final double offset = focalPoint.yOffset();

        Preconditions.checkArgument( width > 0 && width <= maxSideLength, "width value must be between 0 and %s : %s", maxSideLength,
                                     width );
        Preconditions.checkArgument( height > 0 && height <= maxSideLength, "height value must be between 0 and %s : %s", maxSideLength,
                                     height );

        return adaptScaleCalculator( ScaleCalculator.wide( width, height, offset ) );
    }

    public ImageScaleFunction height( FocalPoint focalPoint, Object... args )
    {
        Preconditions.checkArgument( args.length <= 1, "Too many arguments %s", args.length );

        final int size = CommandArgumentParser.getIntArg( args, 0, 100 );

        Preconditions.checkArgument( size > 0 && size <= maxSideLength, "size value must be between 0 and %s : %s", maxSideLength, size );

        return adaptScaleCalculator( ScaleCalculator.height( size ) );
    }

    public ImageScaleFunction width( FocalPoint focalPoint, Object... args )
    {
        Preconditions.checkArgument( args.length <= 1, "Too many arguments %s", args.length );

        final int size = CommandArgumentParser.getIntArg( args, 0, 100 );

        Preconditions.checkArgument( size > 0 && size <= maxSideLength, "size value must be between 0 and %s : %s", maxSideLength, size );

        return adaptScaleCalculator( ScaleCalculator.width( size ) );
    }

    private static ImageScaleFunction adaptScaleCalculator( final ScaleCalculator scaleCalculator )
    {
        return new ScaledFunction( scaleCalculator );
    }
}
