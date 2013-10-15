package com.enonic.wem.api.content.page.rendering;


import com.enonic.wem.api.Client;
import com.enonic.wem.api.content.page.Part;
import com.enonic.wem.api.rendering.Context;
import com.enonic.wem.api.rendering.RenderingResult;

public class PartComponentType
    extends AbstractPageComponentType<Part>
{
    @Override
    public RenderingResult execute( final Part part, final Context context, final Client client )
    {
        ControllerExecutor controllerExecutor = new ControllerExecutorFactory( client ).part( part );
        controllerExecutor.execute();
        return null;
    }
}
