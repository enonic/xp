package com.enonic.xp.admin.impl.rest.resource;

import java.awt.image.BufferedImage;
import java.io.InputStream;

public interface BaseImageHelper
{
    public BufferedImage resizeImage( final InputStream is, final int size );
}
