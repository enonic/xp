package com.enonic.wem.api.command.resource;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.api.resource.ResourcePath;

public final class GetResource
    extends Command<Resource>
{
    private ResourcePath path;

    private boolean includeData;

    public ResourcePath getPath()
    {
        return path;
    }

    public void setPath( final ResourcePath path )
    {
        this.path = path;
    }

    public boolean isIncludeData()
    {
        return includeData;
    }

    public void setIncludeData( final boolean includeData )
    {
        this.includeData = includeData;
    }

    @Override
    public void validate()
    {
    }
}
