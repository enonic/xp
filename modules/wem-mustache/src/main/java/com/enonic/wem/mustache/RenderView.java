package com.enonic.wem.mustache;

import java.util.Map;

import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.script.command.Command;

public final class RenderView extends Command<String>
{
    private ResourceKey view;

    private Map<String, Object> parameters;

    public Map<String, Object> getParameters()
    {
        return parameters;
    }

    public void setParameters( final Map<String, Object> parameters )
    {
        this.parameters = parameters;
    }

    public ResourceKey getView()
    {
        return view;
    }

    public void setView( final ResourceKey view )
    {
        this.view = view;
    }
}
