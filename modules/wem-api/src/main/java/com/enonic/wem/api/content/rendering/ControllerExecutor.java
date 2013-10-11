package com.enonic.wem.api.content.rendering;


import java.util.ArrayList;
import java.util.List;

import com.enonic.wem.api.data.RootDataSet;

public class ControllerExecutor
{
    private Controller controller;

    private RootDataSet componentConfig;

    private RootDataSet templateConfig;

    public ControllerExecutor( final Controller controller, final RootDataSet componentConfig, final RootDataSet templateConfig )
    {
        this.controller = controller;
        this.componentConfig = componentConfig;
        this.templateConfig = templateConfig;
    }

    RenderingResult render()
    {
        // Resolve placeholders in controller params
        List<ControllerParam> params = resolvePlaceholders( controller.getParams() );

        controller.execute();
        return null;
    }

    private List<ControllerParam> resolvePlaceholders( List<ControllerParam> params )
    {

        return new ArrayList<>();
    }

}
