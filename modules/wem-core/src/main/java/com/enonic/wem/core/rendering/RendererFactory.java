package com.enonic.wem.core.rendering;

import com.enonic.wem.api.Client;

public interface RendererFactory
{
    Renderer create( Client client, Context context );
}
