package com.enonic.xp.core.impl.image.effect;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.util.function.Consumer;

import com.jhlabs.image.BlockFilter;
import com.jhlabs.image.BumpFilter;
import com.jhlabs.image.EdgeFilter;
import com.jhlabs.image.EmbossFilter;
import com.jhlabs.image.FlipFilter;
import com.jhlabs.image.GammaFilter;
import com.jhlabs.image.GaussianFilter;
import com.jhlabs.image.GrayscaleFilter;
import com.jhlabs.image.HSBAdjustFilter;
import com.jhlabs.image.InvertFilter;
import com.jhlabs.image.RGBAdjustFilter;
import com.jhlabs.image.SharpenFilter;

import com.enonic.xp.core.impl.image.ImageFunction;
import com.enonic.xp.core.impl.image.parser.CommandArgumentParser;
import com.enonic.xp.image.ImageHelper;

public class ImageFilters
{
    public ImageFunction block( Object... args )
    {
        final int blockSize = CommandArgumentParser.getIntArg( args, 0, 2 );
        return adaptOperation( new BlockFilter( blockSize ) );
    }

    public ImageFunction blur( Object... args )
    {
        final int radius = CommandArgumentParser.getIntArg( args, 0, 2 );
        return adaptOperation( new GaussianFilter( radius ) );
    }

    public ImageFunction border( Object... args )
    {
        final int size = CommandArgumentParser.getIntArg( args, 0, 2 );
        final int color = CommandArgumentParser.getIntArg( args, 1, 0x000000 );
        return borderFunction( size, color );
    }

    public ImageFunction bump( Object... args )
    {
        return adaptOperation( new BumpFilter() );
    }

    public ImageFunction colorize( Object... args )
    {
        float r = CommandArgumentParser.getFloatArg( args, 0, 1f );
        float g = CommandArgumentParser.getFloatArg( args, 1, 1f );
        float b = CommandArgumentParser.getFloatArg( args, 2, 1f );
        return adaptFilter( new ColorizeFilter( r, g, b ) );
    }

    public ImageFunction edge( Object... args )
    {
        return adaptOperation( new EdgeFilter() );
    }

    public ImageFunction emboss( Object... args )
    {
        return adaptOperation( new EmbossFilter() );
    }

    public ImageFunction fliph( Object... args )
    {
        return flip( FlipFilter.FLIP_H );
    }

    public ImageFunction flipv( Object... args )
    {
        return flip( FlipFilter.FLIP_H );
    }

    public ImageFunction rotate90( Object... args )
    {
        return flip( FlipFilter.FLIP_90CW );
    }

    public ImageFunction rotate180( Object... args )
    {
        return flip( FlipFilter.FLIP_180 );
    }

    public ImageFunction rotate270( Object... args )
    {
        return flip( FlipFilter.FLIP_90CCW );
    }

    private ImageFunction flip( final int operation )
    {
        return adaptOperation( new FlipFilter( operation ) );
    }

    public ImageFunction gamma( Object... args )
    {
        float gamma = CommandArgumentParser.getFloatArg( args, 0, 0f );
        return adaptOperation( new GammaFilter( gamma ) );
    }

    public ImageFunction grayscale( Object... args )
    {
        return adaptOperation( new GrayscaleFilter() );
    }

    public ImageFunction hsbadjust( Object... args )
    {
        float h = CommandArgumentParser.getFloatArg( args, 0, 0f );
        float s = CommandArgumentParser.getFloatArg( args, 1, 0f );
        float b = CommandArgumentParser.getFloatArg( args, 2, 0f );
        return adaptOperation( new HSBAdjustFilter( h, s, b ) );
    }

    public ImageFunction hsbcolorize( Object... args )
    {
        final int rgb = CommandArgumentParser.getIntArg( args, 0, 0xFFFFFF );
        return adaptFilter( new HSBColorizeFilter( rgb ) );
    }

    public ImageFunction invert( Object... args )
    {
        return adaptOperation( new InvertFilter() );
    }

    public ImageFunction rgbadjust( Object... args )
    {
        float r = CommandArgumentParser.getFloatArg( args, 0, 0f );
        float g = CommandArgumentParser.getFloatArg( args, 1, 0f );
        float b = CommandArgumentParser.getFloatArg( args, 2, 0f );
        return adaptOperation( new RGBAdjustFilter( r, g, b ) );
    }

    public ImageFunction rounded( Object... args )
    {
        final int radius = CommandArgumentParser.getIntArg( args, 0, 10 );
        final int borderSize = CommandArgumentParser.getIntArg( args, 1, 0 );
        final int borderColor = CommandArgumentParser.getIntArg( args, 2, 0x000000 );
        return roundedFunction( radius, borderSize, borderColor );
    }

    public ImageFunction sepia( Object... args )
    {
        int depth = CommandArgumentParser.getIntArg( args, 0, 20 );
        return adaptFilter( new SepiaFilter( depth ) );
    }

    public ImageFunction sharpen( Object... args )
    {
        return adaptOperation( new SharpenFilter() );
    }

    private static ImageFunction adaptOperation( final BufferedImageOp operation )
    {
        return source -> operation.filter( source, null );
    }

    private static ImageFunction adaptFilter( final ImageFilter filter )
    {
        return source -> {
            ImageProducer producer = new FilteredImageSource( source.getSource(), filter );
            Image image = Toolkit.getDefaultToolkit().createImage( producer );
            BufferedImage bufferedImage = ImageHelper.createImage( image.getWidth( null ), image.getHeight( null ), true );
            withGraphics( bufferedImage, g -> g.drawImage( image, 0, 0, null ) );
            return bufferedImage;
        };
    }

    private static ImageFunction roundedFunction( final int radius, final int borderSize, final int borderColor )
    {
        return source -> {
            final int width = source.getWidth();
            final int height = source.getHeight();
            BufferedImage dest = ImageHelper.createImage( width, height, true );
            withGraphics( dest, g -> {
                int arc = radius * 2;

                if ( borderSize > 0 )
                {
                    g.setPaint( new Color( borderColor, false ) );
                    g.fillRoundRect( 0, 0, width, height, arc, arc );
                }

                arc = arc - ( borderSize * 2 );

                g.setPaint( new TexturePaint( source, new Rectangle2D.Float( 0, 0, width, height ) ) );
                g.fillRoundRect( borderSize, borderSize, width - ( borderSize * 2 ), height - ( borderSize * 2 ), arc, arc );
            } );

            return dest;
        };
    }

    private static ImageFunction borderFunction( final int size, final int color )
    {
        return source -> {
            final int width = source.getWidth();
            final int height = source.getHeight();
            BufferedImage dest = ImageHelper.createImage( width, height, true );
            withGraphics( dest, g -> {

                g.setPaint( new Color( color, false ) );

                // drawing the border around image consists of of 4 rectangles with thickness = this.size
                if ( size > 0 )
                {
                    g.fillRect( 0, 0, width, size );
                    g.fillRect( 0, 0, size, height );
                    g.fillRect( width - size, 0, size, height );
                    g.fillRect( 0, height - size, width, size );
                }

                g.setPaint( new TexturePaint( source, new Rectangle2D.Float( 0, 0, width, height ) ) );
                g.fillRect( size, size, width - ( size * 2 ), height - ( size * 2 ) );
            } );

            return dest;
        };
    }

    private static void withGraphics( final BufferedImage img, Consumer<Graphics2D> consumer )
    {
        Graphics2D g = img.createGraphics();
        try
        {
            g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
            consumer.accept( g );
        }
        finally
        {
            g.dispose();
        }
    }
}
