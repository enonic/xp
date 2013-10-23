package com.enonic.wem.api.content.page;


import com.enonic.wem.api.Path;
import com.enonic.wem.api.content.page.rendering.Controller;

public final class XViewController
    extends Controller
{
    private Path xslt;

    public XViewController( final ControllerParams params )
    {
        super( params );
//        this.xslt = new Path( params.getParam( "_view" ).getValue(), '/' );
    }

    @Override
    public String execute()
    {
        return "executed";
    }

}
