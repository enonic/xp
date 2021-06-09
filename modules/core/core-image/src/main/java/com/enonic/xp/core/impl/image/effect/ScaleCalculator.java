package com.enonic.xp.core.impl.image.effect;

public interface ScaleCalculator
{
    Values calc( int sourceWidth, int sourceHeight );

    static ScaleCalculator wide( final int width, final int height, final double offset )
    {
        return ( final int sourceWidth, final int sourceHeight ) -> {
            int newHeight = (int) ( (double) sourceHeight * width / sourceWidth );

            int viewHeight = Math.min( height, newHeight );

            return scaledSub( width, newHeight, 0, centerOffset( newHeight, viewHeight, offset ), width, viewHeight );
        };
    }

    static ScaleCalculator width( final int size )
    {
        return ( final int sourceWidth, final int sourceHeight ) -> scaled( size, (int) ( (double) sourceHeight * size / sourceWidth ) );
    }

    static ScaleCalculator max( final int size )
    {
        return ( final int sourceWidth, final int sourceHeight ) -> {
            double scale = (double) size / Math.max( sourceWidth, sourceHeight );

            return scaled( (int) ( sourceWidth * scale ), (int) ( sourceHeight * scale ) );
        };
    }

    static ScaleCalculator height( final int size )
    {
        return ( final int sourceWidth, final int sourceHeight ) -> scaled( (int) ( (double) sourceWidth * size / sourceHeight ), size );
    }

    static ScaleCalculator block( final int width, final int height, final double xOffset, final double yOffset )
    {
        return ( final int sourceWidth, final int sourceHeight ) -> {
            int newWidth;
            int newHeight;

            final double heightScale = (double) height / sourceHeight;
            final double widthScale = (double) width / sourceWidth;

            if ( heightScale < widthScale )
            {
                newWidth = width;
                newHeight = (int) ( sourceHeight * widthScale );
            }
            else
            {
                newWidth = (int) ( sourceWidth * heightScale );
                newHeight = height;
            }

            return scaledSub( newWidth, newHeight, centerOffset( newWidth, width, xOffset ), centerOffset( newHeight, height, yOffset ),
                              width, height );
        };
    }

    private static int centerOffset( final int value1, final int value2, final double offset )
    {
        int diff = value1 - value2;
        final int centered = (int) ( value1 * offset ) - ( value2 / 2 );
        return Math.max( Math.min( centered, 0 ), diff );
    }


    private static Values scaledSub( final int newWidth, final int newHeight, final int widthOffset, final int heightOffset,
                                     final int viewWidth, final int viewHeight )
    {
        return new Values( newWidth, newHeight, widthOffset, heightOffset, viewWidth, viewHeight );
    }

    private static Values scaled( final int newWidth, final int newHeight )
    {
        return new Values( newWidth, newHeight, 0, 0, newWidth, newHeight );
    }

    class Values
    {
        public Values( final int newWidth, final int newHeight, final int widthOffset, final int heightOffset, final int viewWidth,
                       final int viewHeight )
        {
            this.newWidth = newWidth;
            this.newHeight = newHeight;
            this.widthOffset = widthOffset;
            this.heightOffset = heightOffset;
            this.viewWidth = viewWidth;
            this.viewHeight = viewHeight;
        }

        public final int newWidth;

        public final int newHeight;

        public final int widthOffset;

        public final int heightOffset;

        public final int viewWidth;

        public final int viewHeight;


        public boolean subimage()
        {
            return newWidth != viewWidth || newHeight != viewHeight;
        }

    }

}
