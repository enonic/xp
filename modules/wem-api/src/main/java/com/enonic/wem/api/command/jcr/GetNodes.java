package com.enonic.wem.api.command.jcr;

import org.codehaus.jackson.node.ArrayNode;

import com.enonic.wem.api.command.Command;

public final class GetNodes
    extends Command<ArrayNode>
{
    private int depth;

    private String path;

    public GetNodes path( final String path )
    {
        this.path = path;
        return this;
    }

    public String getPath()
    {
        return this.path;
    }

    public GetNodes depth( final int depth )
    {
        this.depth = depth;
        return this;

    }

    public int getDepth()
    {
        return this.depth;
    }

    @Override
    public void validate()
    {
    }
}
