package com.enonic.wem.xslt;

import java.util.Map;

import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.script.command.Command;
import com.enonic.wem.script.command.CommandName;

@CommandName("view.renderXslt")
public final class RenderView
    extends Command<String>
{
    private ResourceKey view;

    private String inputXml;

    private Map<String, Object> parameters;

    public ResourceKey getView()
    {
        return view;
    }

    public void setView( final ResourceKey view )
    {
        this.view = view;
    }

    public String getInputXml()
    {
        return inputXml;
    }

    public void setInputXml( final String inputXml )
    {
        this.inputXml = inputXml;
    }

    public Map<String, Object> getParameters()
    {
        return parameters;
    }

    public void setParameters( final Map<String, Object> parameters )
    {
        this.parameters = parameters;
    }
}
