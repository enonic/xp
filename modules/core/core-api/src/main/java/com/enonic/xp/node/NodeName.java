package com.enonic.xp.node;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.name.Name;

@PublicApi
public final class NodeName
    extends Name
{
    public static final NodeName ROOT = new NodeName( "", false );

    private NodeName( final String name, final boolean validate )
    {
        super( validate ? checkUnderscore( name ) : name, validate );
    }

    private static String checkUnderscore( final String name )
    {
        Preconditions.checkArgument( !"_".equals( name ), "name cannot be _" );
        return name;
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
        return name instanceof NodeName nodeName ? nodeName : new NodeName( checkUnderscore( name.toString() ), false );
    }

    static NodeName fromInternal( final String name )
    {
        return name.isEmpty() ? ROOT : new NodeName( name, false );
    }
}
