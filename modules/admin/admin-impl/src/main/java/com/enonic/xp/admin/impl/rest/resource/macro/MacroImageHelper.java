package com.enonic.xp.admin.impl.rest.resource.macro;

import com.enonic.xp.admin.impl.rest.resource.BaseImageHelper;

public final class MacroImageHelper
    extends BaseImageHelper
{
    private final byte[] defaultMacroImage;

    public MacroImageHelper()
    {
        defaultMacroImage = loadDefaultImage( "macro" );
    }

    public byte[] getDefaultMacroImage()
    {
        return defaultMacroImage;
    }
}
