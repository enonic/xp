package com.enonic.wem.api.content.page.rendering;


import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;

import com.enonic.wem.api.content.page.ControllerParam;
import com.enonic.wem.api.content.page.ControllerParams;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.rendering.RenderingResult;

import static com.enonic.wem.api.rendering.RenderingResult.newRenderingResult;

public final class ControllerExecutor
{
    private final Controller controller;

    private final RootDataSet componentConfig;

    private final RootDataSet templateConfig;

    public ControllerExecutor( final Controller controller, final RootDataSet componentConfig, final RootDataSet templateConfig )
    {
        this.controller = controller;
        this.componentConfig = componentConfig;
        this.templateConfig = templateConfig;
    }

    RenderingResult execute()
    {
        // Resolve placeholders in controller params
        ControllerParams params = resolvePlaceholders( controller.getParams() );

        final String controllerResult = controller.execute();
        return newRenderingResult().success().build();
    }

    private ControllerParams resolvePlaceholders( final ControllerParams params )
    {
        if ( params.isEmpty() )
        {
            return params;
        }
        final List<ControllerParam> resolvedParamList = Lists.newArrayList();
        for ( ControllerParam param : params )
        {
            resolvedParamList.add( resolvePlaceholder( param ) );
        }
        return ControllerParams.from( resolvedParamList );
    }

    private ControllerParam resolvePlaceholder( final ControllerParam param )
    {
        final String name = param.getName();
        final boolean isPlaceHolder = name.startsWith( "${" ) && name.endsWith( "}" );
        if ( isPlaceHolder )
        {
            final String key = StringUtils.substringBetween( name, "${", "}" );
            if ( key.startsWith( "config" ) )
            {
                final String configKey = StringUtils.substringAfter( key, "config." );
                final String value = componentConfig.getProperty( configKey ).getString();
                return new ControllerParam( param.getName(), value );
            }
        }
        return param;
    }
}
