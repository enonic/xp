package com.enonic.wem.core.rendering;

import com.enonic.wem.api.rendering.Renderable;

public interface Renderer<T extends Renderable>
{
    RenderingResult execute( T renderable );
}
