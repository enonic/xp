package com.enonic.wem.api.content.page;


import com.enonic.wem.api.Path;

public class XViewController
    extends Controller
{
    private Path xslt;

    public XViewController( ControllerParams params )
    {
        this.xslt = new Path( params.getAString( "_view" ), '/' );
    }

}
