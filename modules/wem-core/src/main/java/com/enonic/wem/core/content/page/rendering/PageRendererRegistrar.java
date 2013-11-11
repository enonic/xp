package com.enonic.wem.core.content.page.rendering;


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
