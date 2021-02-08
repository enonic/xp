package com.enonic.xp.core.impl.image.effect;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;

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
        return new RectBorderFilter( size, color );
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
        return new RoundedFilter( radius, borderSize, borderColor );
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

    static ImageFunction adaptOperation( final BufferedImageOp operation )
    {
        return source -> operation.filter( source, null );
    }

    static ImageFunction adaptFilter( final ImageFilter filter )
    {
        return source -> {
            ImageProducer producer = new FilteredImageSource( source.getSource(), filter );
            Image image = Toolkit.getDefaultToolkit().createImage( producer );
            BufferedImage bufferedImage = ImageHelper.createImage( image.getWidth( null ), image.getHeight( null ), true );
            Graphics2D g = bufferedImage.createGraphics();
            g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
            g.drawImage( image, 0, 0, null );
            g.dispose();
            return bufferedImage;
        };
    }
}
