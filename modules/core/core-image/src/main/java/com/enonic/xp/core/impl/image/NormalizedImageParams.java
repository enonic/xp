package com.enonic.xp.core.impl.image;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.core.impl.image.parser.FilterSetExpr;
import com.enonic.xp.image.Cropping;
import com.enonic.xp.image.FocalPoint;
import com.enonic.xp.image.ReadImageParams;
import com.enonic.xp.image.ScaleParams;
import com.enonic.xp.media.ImageOrientation;
import com.enonic.xp.style.ImageStyle;
import com.enonic.xp.style.ImageStyleSettings;
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

    private final String attachmentSha512;

    NormalizedImageParams( ReadImageParams readImageParams )
    {
        this( readImageParams, null );
    }

    NormalizedImageParams( final ReadImageParams readImageParams, final ImageStyle style )
    {
        final ImageStyleSettings settings = style == null ? null : ImageStyleSettings.from( style );
        this.contentId = readImageParams.getContentId();
        this.binaryReference = readImageParams.getBinaryReference();
        this.cropping = normalizeCropping( readImageParams.getCropping() );
        this.scaleParams = normalizeScaleParams( readImageParams ).withAspectRatio( settings == null ? null : settings.aspectRatio() );
        this.focalPoint = readImageParams.getFocalPoint();
        this.filterParam = FilterSetExpr.parse( settings == null ? readImageParams.getFilterParam() : settings.filter() );
        this.format = normalizeFormat( readImageParams );
        if ( style == null && ( "webp".equals( format ) || "avif".equals( format ) ) )
        {
            throw new IllegalArgumentException( "WebP and AVIF encoding requires a predefined image style" );
        }
        this.backgroundColor = supportsAlpha( format ) ? 0xFFFFFF : settings == null ? readImageParams.getBackgroundColor() : settings.background();
        this.quality = settings == null ? readImageParams.getQuality() : settings.quality();
        this.orientation = readImageParams.getOrientation();
        this.attachmentSha512 = readImageParams.getAttachmentSha512();
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

    public String getAttachmentSha512()
    {
        return attachmentSha512;
    }

    private static Cropping normalizeCropping( final Cropping cropping )
    {
        return cropping == null || cropping.isUnmodified() ? Cropping.DEFAULT : cropping;
    }

    private static String normalizeFormat( final ReadImageParams readImageParams )
    {
        // Limit to web formats we support. Leave WBMP and BMP support behind.
        // Modern encoders must only be reached through a predefined processing style.
        final String mimeType = readImageParams.getMimeType();

        return switch ( mimeType )
        {
            case "image/png" -> "png";
            case "image/jpeg" -> "jpeg";
            case "image/gif" -> "gif";
            case "image/webp" -> "webp";
            case "image/avif" -> "avif";
            default -> throw new IllegalArgumentException( "Unsupported type " + mimeType );
        };
    }

    private static ScaleParams normalizeScaleParams( final ReadImageParams readImageParams )
    {
        final ScaleParams scaleParams = readImageParams.getScaleParams();
        if ( scaleParams != null )
        {
            if ( ScaleParams.NO_SCALE.getName().equals( scaleParams.getName() ) )
            {
                // full scale with arguments is not supported
                if ( scaleParams.getArguments().length > 0 )
                {
                    throw new IllegalArgumentException( "Full scale cant have arguments" );
                }
                return ScaleParams.NO_SCALE;
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
                return ScaleParams.NO_SCALE;
            }
        }
    }

    static boolean supportsAlpha( final String format )
    {
        return "png".equals( format ) || "webp".equals( format ) || "avif".equals( format );
    }
}
