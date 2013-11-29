package com.enonic.wem.core.content.page.rendering;


import com.enonic.wem.core.content.page.image.ImageRendererFactory;
import com.enonic.wem.core.content.page.layout.LayoutRendererFactory;
import com.enonic.wem.core.content.page.part.PartRendererFactory;

public class PageRendererRegistrar
{

    public static void register()
    {
        PageRendererFactory.register();
        PartRendererFactory.register();
        LayoutRendererFactory.register();
        ImageRendererFactory.register();
    }
}
