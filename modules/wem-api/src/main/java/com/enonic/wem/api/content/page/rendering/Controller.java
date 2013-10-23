package com.enonic.wem.api.content.page.rendering;


import com.enonic.wem.api.content.page.ControllerParams;

public abstract class Controller
{
    private final ControllerParams params;

    protected Controller( final ControllerParams params )
    {
        this.params = params;
    }

    public ControllerParams getParams()
    {
        return params;
    }

    public abstract String execute();

}
