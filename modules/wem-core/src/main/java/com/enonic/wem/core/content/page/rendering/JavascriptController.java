package com.enonic.wem.core.content.page.rendering;


import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.core.rendering.Context;

public final class JavascriptController
    implements Controller
{
    private final String javascriptSource;

    private final RootDataSet config;

    private final Context context;

    JavascriptController( final String javascriptSource, final RootDataSet config, final Context context )
    {
        this.javascriptSource = javascriptSource;
        this.config = config;
        this.context = context;
    }

    @Override
    public ControllerResult execute()
    {
        // execute javascriptSource

        return ControllerResult.newControllerResult().success().build();
    }

}
