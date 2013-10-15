package com.enonic.wem.api.content.page.rendering;


import com.enonic.wem.api.Client;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.rendering.Context;
import com.enonic.wem.api.rendering.RenderingResult;

public class PageComponentType
    extends AbstractPageComponentType<Page>
{
    @Override
    public RenderingResult execute( final Page page, final Context context, final Client client )
    {
        ControllerExecutor controllerExecutor = new ControllerExecutorFactory( client ).page( page );
        controllerExecutor.execute();
        return null;
    }
}
