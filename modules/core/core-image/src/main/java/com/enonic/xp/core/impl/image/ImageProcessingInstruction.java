package com.enonic.xp.core.impl.image;

import com.enonic.xp.image.Cropping;
import com.enonic.xp.image.FocalPoint;
import com.enonic.xp.image.ScaleParams;
import com.enonic.xp.media.ImageOrientation;

class ImageProcessingInstruction
{
    private final ScaleParams scaleParams;

    private final FocalPoint focalPoint;

    private final Cropping cropping;

    private final ImageOrientation orientation;

    private final String filterParam;

    private final String format;

    private final int quality;

    private final int backgroundColor;

    private final boolean progressive;

    ImageProcessingInstruction( final NormalizedImageParams params, final boolean progressive )
    {
        this.scaleParams = params.getScaleParams();
        this.focalPoint = params.getFocalPoint();
        this.cropping = params.getCropping();
        this.orientation = params.getOrientation();
        this.filterParam = params.getFilterParam().toString();
        this.format = params.getFormat();
        this.quality = params.getQuality();
        this.backgroundColor = params.getBackgroundColor();
        this.progressive = progressive;
    }

    public ScaleParams getScaleParams()
    {
        return scaleParams;
    }

    public FocalPoint getFocalPoint()
    {
        return focalPoint;
    }

    public Cropping getCropping()
    {
        return cropping;
    }

    public ImageOrientation getOrientation()
    {
        return orientation;
    }

    public String getFilterParam()
    {
        return filterParam;
    }

    public String getFormat()
    {
        return format;
    }

    public int getQuality()
    {
        return quality;
    }

    public int getBackgroundColor()
    {
        return backgroundColor;
    }

    public boolean isProgressive()
    {
        return progressive;
    }
}
