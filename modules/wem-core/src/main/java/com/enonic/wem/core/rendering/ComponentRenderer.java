package com.enonic.wem.core.rendering;


import com.enonic.wem.api.content.page.Renderable;

public interface ComponentRenderer<T extends Renderable>
{
    RenderingResult execute( T component, Context context );
}
