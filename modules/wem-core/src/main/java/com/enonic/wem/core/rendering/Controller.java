package com.enonic.wem.core.rendering;


import com.enonic.wem.api.data.RootDataSet;

public final class Controller
{
    private final String javascriptSource;

    private final RootDataSet config;

    private final Context context;

    public Controller( final String javascriptSource, final RootDataSet config, final Context context )
    {
        this.javascriptSource = javascriptSource;
        this.config = config;
        this.context = context;
    }

    public ControllerResult execute()
    {
        // execute javascriptSource

        return ControllerResult.newControllerResult().success().build();
    }

}
