package com.enonic.wem.api.command.resource;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.resource.Resource;

public class GetResource
    extends Command<Resource>
{
    private String module;

    private String path;


    public GetResource path( final String path )
    {
        this.path = path;
        return this;
    }

    public GetResource module( final String module )
    {
        this.module = module;
        return this;
    }

    public String getModule()
    {
        return module;
    }

    public String getPath()
    {
        return path;
    }

    @Override
    public void validate()
    {

    }
}
