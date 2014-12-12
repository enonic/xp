package com.enonic.wem.admin.status;

import com.fasterxml.jackson.databind.node.ObjectNode;

public abstract class StatusInfoBuilder
    implements Comparable<StatusInfoBuilder>
{
    private final String name;

    public StatusInfoBuilder( final String name )
    {
        this.name = name;
    }

    public final String getName()
    {
        return this.name;
    }

    public final void addInfo( final ObjectNode json )
    {
        build( json.putObject( this.name ) );
    }

    protected abstract void build( ObjectNode json );

    @Override
    public final int compareTo( final StatusInfoBuilder other )
    {
        return this.name.compareTo( other.name );
    }
}
