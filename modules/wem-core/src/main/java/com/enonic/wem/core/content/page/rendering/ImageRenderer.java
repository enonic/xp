package com.enonic.wem.core.content.page.rendering;


import com.enonic.wem.api.Client;
import com.enonic.wem.api.content.page.Image;
import com.enonic.wem.core.rendering.BaseRenderer;
import com.enonic.wem.core.rendering.Context;
import com.enonic.wem.core.rendering.Renderer;
import com.enonic.wem.core.rendering.RenderingResult;

public class ImageRenderer
    extends BaseRenderer
    implements Renderer<Image>
{
    public ImageRenderer( final Client client, final Context context )
    {
        super( client, context );
    }

    @Override
    public RenderingResult execute( final Image image )
    {
        return null;
    }
}
