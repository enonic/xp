package com.enonic.wem.core.content.page.part;


import com.enonic.wem.api.Client;
import com.enonic.wem.api.content.page.part.PartComponent;
import com.enonic.wem.core.rendering.BaseRenderer;
import com.enonic.wem.core.rendering.Context;
import com.enonic.wem.core.rendering.Renderer;
import com.enonic.wem.core.rendering.RenderingResult;

public class PartRenderer
    extends BaseRenderer
    implements Renderer<PartComponent>
{
    public PartRenderer( final Client client, final Context context )
    {
        super( client, context );
    }

    @Override
    public RenderingResult execute( final PartComponent partComponent )
    {
        return null;
    }

}
