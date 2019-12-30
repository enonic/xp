package com.enonic.xp.node;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.name.Name;

@PublicApi
public final class NodeName
    extends Name
{
    public final static NodeName ROOT = new NodeName( "", false );

    private NodeName( final String name, final boolean validate )
    {
        super( name, validate );
        Preconditions.checkArgument( !"_".equals( name ), "name cannot be _" );
    }

    public boolean isRoot()
    {
        return this == ROOT;
    }

    public static NodeName from( final String name )
    {
        return new NodeName( name, true );
    }
}
