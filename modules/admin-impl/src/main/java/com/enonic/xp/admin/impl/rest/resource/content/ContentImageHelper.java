package com.enonic.xp.admin.impl.rest.resource.content;

import java.awt.image.BufferedImage;

import com.google.common.io.ByteSource;

import com.enonic.xp.admin.impl.rest.resource.BaseImageHelper;

interface ContentImageHelper
    extends BaseImageHelper
{
    BufferedImage readImage( final ByteSource blob, final ImageParams imageParams );

    BufferedImage readAndRotateImage( final ByteSource blob, final ImageParams imageParams );
}