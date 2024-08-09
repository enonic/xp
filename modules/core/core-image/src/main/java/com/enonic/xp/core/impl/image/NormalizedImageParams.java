package com.enonic.xp.core.impl.image;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.core.impl.image.parser.FilterSetExpr;
import com.enonic.xp.image.Cropping;
import com.enonic.xp.image.FocalPoint;
import com.enonic.xp.image.ReadImageParams;
import com.enonic.xp.image.ScaleParams;
import com.enonic.xp.media.ImageOrientation;
import com.enonic.xp.util.BinaryReference;

/**
 * NormalizedImageParams normalizes {@link ReadImageParams} fields
 * It helps to generate fewer image cache files in {@link ImageServiceImpl}
 */
class NormalizedImageParams
{
    private final ContentId contentId;

    private final BinaryReference binaryReference;

    private final Cropping cropping;

    private final ScaleParams scaleParams;

    private final FocalPoint focalPoint;

    private final FilterSetExpr filterParam;

    private final int backgroundColor;

    private final String format;

    private final int quality;

    private final ImageOrientation orientation;

    NormalizedImageParams( ReadImageParams readImageParams )
    {
        this.contentId = readImageParams.getContentId();
        this.binaryReference = readImageParams.getBinaryReference();
        this.cropping = normalizeCropping( readImageParams.getCropping() );
        this.scaleParams = normalizeScaleParams( readImageParams );
        this.focalPoint = readImageParams.getFocalPoint();
        this.filterParam = FilterSetExpr.parse( readImageParams.getFilterParam() );
        this.format = normalizeFormat( readImageParams );
        this.backgroundColor = normalizeBackgroundColor( this.format, readImageParams );
        this.quality = readImageParams.getQuality();
        this.orientation = readImageParams.getOrientation();
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    public BinaryReference getBinaryReference()
    {
        return binaryReference;
    }

    public Cropping getCropping()
    {
        return cropping;
    }

    public ScaleParams getScaleParams()
    {
        return scaleParams;
    }

    public FocalPoint getFocalPoint()
    {
        return focalPoint;
    }

    public FilterSetExpr getFilterParam()
    {
        return filterParam;
    }

    public int getBackgroundColor()
    {
        return backgroundColor;
    }

    public String getFormat()
    {
        return format;
    }

    public int getQuality()
    {
        return quality;
    }

    public ImageOrientation getOrientation()
    {
        return orientation;
    }

    private static Cropping normalizeCropping( final Cropping cropping )
    {
        return cropping == null || cropping.isUnmodified() ? null : cropping;
    }

    private static String normalizeFormat( final ReadImageParams readImageParams )
    {
        // Existing code should not set format parameter, but if it does all bets are off.
        if ( readImageParams.getFormat() != null )
        {
            return readImageParams.getFormat();
        }
        // Limit to web formats we support. Leave WBMP and BMP support behind.
        // Tip: WEBP is not supported by ImageService implementation yet. Throw IllegalArgumentException here.
        final String mimeType = readImageParams.getMimeType();

        switch ( mimeType )
        {
            case "image/png":
                return "png";
            case "image/jpeg":
                // Because most existing cache hashes made with uppercase "JPEG" format.
                return "JPEG";
            case "image/gif":
                return "gif";
            default:
                throw new IllegalArgumentException( "Unsupported type " + mimeType );
        }
    }

    private static ScaleParams normalizeScaleParams( final ReadImageParams readImageParams )
    {
        final ScaleParams scaleParams = readImageParams.getScaleParams();
        if ( scaleParams != null )
        {
            if ( "full".equals( scaleParams.getName() ) )
            {
                // full scale with arguments is not supported
                if ( scaleParams.getArguments().length > 0 )
                {
                    throw new IllegalArgumentException( "Full scale cant have arguments" );
                }
                // Full same as no scale at all, in order to save cache space normalize to null
                return null;
            }
            else
            {
                return scaleParams;
            }
        }
        else
        {
            // Older style of scale params could be adapted to newer one.
            final int scaleSize = readImageParams.getScaleSize();
            if ( scaleSize > 0 )
            {
                if ( readImageParams.isScaleSquare() )
                {
                    return new ScaleParams( "square", new Object[]{scaleSize} );
                }
                else if ( readImageParams.isScaleWidth() )
                {
                    return new ScaleParams( "width", new Object[]{scaleSize} );
                }
                else
                {
                    return new ScaleParams( "max", new Object[]{scaleSize} );
                }
            }
            else
            {
                return null;
            }
        }
    }

    private static int normalizeBackgroundColor( final String format, final ReadImageParams readImageParams )
    {
        // For output format different from png there is no point to have background color, because it is not used.
        // But historically most often value is 0xFFFFFF, as it is a default in Web Image Service.
        return "png".equals( format ) ? 0xFFFFFF : readImageParams.getBackgroundColor();
    }
}
