package com.enonic.xp.admin.impl.rest.resource.content;

import java.awt.image.BufferedImage;

import com.google.common.io.ByteSource;

import com.enonic.xp.admin.impl.rest.resource.BaseImageHelper;

interface ContentImageHelper
    extends BaseImageHelper
{
    public enum ImageFilter
    {
        SCALE_SQUARE_FILTER,
        SCALE_MAX_FILTER
    }

    BufferedImage readImage( final ByteSource blob, final int size, final ImageFilter imageFilter );
}