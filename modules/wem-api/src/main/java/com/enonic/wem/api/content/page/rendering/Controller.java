package com.enonic.wem.api.content.page.rendering;


import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.rendering.Context;
import com.enonic.wem.api.rendering.RenderingResult;

import static com.enonic.wem.api.rendering.RenderingResult.newRenderingResult;

public final class Controller
{
    private final String javascriptSource;

    private final RootDataSet config;

    public Controller( final String javascriptSource, final RootDataSet config )
    {
        this.javascriptSource = javascriptSource;
        this.config = config;
    }

    public RenderingResult execute( final Context context )
    {
        // execute javascriptSource

        return newRenderingResult().success().build();
    }

}
