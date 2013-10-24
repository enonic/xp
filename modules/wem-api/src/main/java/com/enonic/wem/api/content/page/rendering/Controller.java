package com.enonic.wem.api.content.page.rendering;


import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.rendering.Context;
import com.enonic.wem.api.rendering.RenderingResult;

import static com.enonic.wem.api.rendering.RenderingResult.newRenderingResult;

public final class Controller
{
    private final ModuleResourceKey javascriptResource;

    private final RootDataSet config;

    private final Context context;

    public Controller( final ModuleResourceKey javascriptResource, final RootDataSet config, final Context context )
    {
        this.javascriptResource = javascriptResource;
        this.config = config;
        this.context = context;
    }

    public RenderingResult execute()
    {
        return newRenderingResult().success().build();
    }

}
