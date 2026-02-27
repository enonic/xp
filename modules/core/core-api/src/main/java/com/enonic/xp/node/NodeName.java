package com.enonic.xp.node;

import org.jspecify.annotations.NullMarked;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.name.Name;

@PublicApi
@NullMarked
public final class NodeName
    extends Name
{
    public static final NodeName ROOT = new NodeName( "", false );

    private NodeName( final String name, final boolean validate )
    {
        super( name, validate );
    }

    public boolean isRoot()
    {
        return ROOT.equals( this );
    }

    public static NodeName from( final String name )
    {
        return new NodeName( name, true );
    }

    public static NodeName from( final Name name )
    {
        return name instanceof NodeName nodeName ? nodeName : new NodeName( name.toString(), false );
    }

    static NodeName fromInternal( final String name )
    {
        return name.isEmpty() ? ROOT : new NodeName( name, false );
    }
}
