package com.enonic.wem.api.command.resource;

import com.enonic.wem.api.command.CommandBuilder;
import com.enonic.wem.api.resource.ResourcePath;

public final class GetResourceBuilder
    extends CommandBuilder<GetResource>
{
    public GetResourceBuilder()
    {
        super( new GetResource() );
    }

    public GetResourceBuilder path( final String path )
    {
        this.command.setPath( ResourcePath.create( path ) );
        return this;
    }

    public GetResourceBuilder path( final ResourcePath path )
    {
        this.command.setPath( path );
        return this;
    }

    public GetResourceBuilder includeData()
    {
        this.command.setIncludeData( true );
        return this;
    }
}
