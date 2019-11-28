package com.enonic.xp.admin.impl.rest.resource.content.page.part;

import com.enonic.xp.admin.impl.rest.resource.BaseImageHelper;

public class PartImageHelper
    extends BaseImageHelper
{
    private final byte[] defaultPartImage;


    public PartImageHelper()
    {
        defaultPartImage = loadDefaultImage( "part" );
    }

    public byte[] getDefaultPartImage()
    {
        return defaultPartImage;
    }

}
