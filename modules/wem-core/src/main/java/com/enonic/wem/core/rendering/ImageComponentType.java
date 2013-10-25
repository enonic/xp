package com.enonic.wem.core.rendering;


import com.enonic.wem.api.content.image.Image;
import com.enonic.wem.core.rendering.ComponentType;
import com.enonic.wem.core.rendering.Context;
import com.enonic.wem.core.rendering.RenderingResult;

public class ImageComponentType
    implements ComponentType<Image>
{
    @Override
    public RenderingResult execute( final Image image, final Context context)
    {
        return null;
    }
}
