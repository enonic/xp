package com.enonic.wem.api.content.page.rendering;


import com.enonic.wem.api.Client;
import com.enonic.wem.api.content.page.Layout;
import com.enonic.wem.api.rendering.Context;
import com.enonic.wem.api.rendering.RenderingResult;

public class LayoutComponentType
    extends AbstractPageComponentType<Layout>
{
    @Override
    public RenderingResult execute( final Layout layout, final Context context, final Client client )
    {
        // TODO
        //ControllerExecutor controllerExecutor = new ControllerExecutorFactory( client ).layout( part );
        //controllerExecutor.execute();
        return null;
    }
}
