package com.enonic.wem.api.content.page.rendering;


import com.enonic.wem.api.content.page.ControllerParams;
import com.enonic.wem.api.content.page.XViewController;
import com.enonic.wem.api.resource.Resource;

public class ControllerFactory
{
    public static Controller create( final Resource controllerResource, final ControllerParams params )
    {
        return new XViewController( params );
    }
}
