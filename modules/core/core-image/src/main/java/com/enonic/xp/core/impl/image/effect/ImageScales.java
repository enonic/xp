package com.enonic.xp.core.impl.image.effect;

import com.google.common.base.Preconditions;

import com.enonic.xp.core.impl.image.ImageFunction;
import com.enonic.xp.core.impl.image.parser.CommandArgumentParser;
import com.enonic.xp.image.FocalPoint;

public class ImageScales
{
    private final int maxSideLength;

    public ImageScales( final int maxSideLength )
    {
        this.maxSideLength = maxSideLength;
    }

    public ImageFunction full( FocalPoint focalPoint, Object... args )
    {
        Preconditions.checkArgument( args.length == 0, "Too many arguments %s", args.length );

        return new FullScale();
    }

    public ImageFunction block( FocalPoint focalPoint, Object... args )
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

        return new BlockScale( width, height, xOffset, yOffset );
    }

    public ImageFunction square( FocalPoint focalPoint, Object... args )
    {
        Preconditions.checkArgument( args.length <= 1, "Too many arguments %s", args.length );

        final int size = CommandArgumentParser.getIntArg( args, 0, 0 );
        final double xOffset = focalPoint.xOffset();
        final double yOffset = focalPoint.yOffset();

        Preconditions.checkArgument( size > 0 && size <= maxSideLength, "size value must be between 0 and %s : %s", maxSideLength, size );

        return new SquareScale( size, xOffset, yOffset );
    }

    public ImageFunction max( FocalPoint focalPoint, Object... args )
    {
        Preconditions.checkArgument( args.length <= 1, "Too many arguments %s", args.length );

        final int size = CommandArgumentParser.getIntArg( args, 0, 0 );

        Preconditions.checkArgument( size > 0 && size <= maxSideLength, "size value must be between 0 and %s : %s", maxSideLength, size );

        return new MaxScale( size );
    }

    public ImageFunction wide( FocalPoint focalPoint, Object... args )
    {
        Preconditions.checkArgument( args.length <= 2, "Too many arguments %s", args.length );

        final int width = CommandArgumentParser.getIntArg( args, 0, 0 );
        final int height = CommandArgumentParser.getIntArg( args, 1, 0 );
        final double offset = focalPoint.yOffset();

        Preconditions.checkArgument( width > 0 && width <= maxSideLength, "width value must be between 0 and %s : %s", maxSideLength,
                                     width );
        Preconditions.checkArgument( height > 0 && height <= maxSideLength, "height value must be between 0 and %s : %s", maxSideLength,
                                     height );

        return new WideScale( width, height, offset );
    }

    public ImageFunction height( FocalPoint focalPoint, Object... args )
    {
        Preconditions.checkArgument( args.length <= 1, "Too many arguments %s", args.length );

        final int size = CommandArgumentParser.getIntArg( args, 0, 100 );

        Preconditions.checkArgument( size > 0 && size <= maxSideLength, "size value must be between 0 and %s : %s", maxSideLength, size );

        return new HeightScale( size );
    }

    public ImageFunction width( FocalPoint focalPoint, Object... args )
    {
        Preconditions.checkArgument( args.length <= 1, "Too many arguments %s", args.length );

        final int size = CommandArgumentParser.getIntArg( args, 0, 100 );

        Preconditions.checkArgument( size > 0 && size <= maxSideLength, "size value must be between 0 and %s : %s", maxSideLength, size );

        return new WidthScale( size );
    }
}
