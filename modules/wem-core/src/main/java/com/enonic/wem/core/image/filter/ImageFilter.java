/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.wem.core.image.filter;

import java.awt.image.BufferedImage;

public interface ImageFilter
{
    public BufferedImage filter( BufferedImage source );
}
