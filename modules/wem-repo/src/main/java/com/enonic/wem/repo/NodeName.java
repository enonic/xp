package com.enonic.wem.repo;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.Name;

public class NodeName
    extends Name
{
    private NodeName( final String name )
    {
        super( checkNodeNameRestrictions( name ) );
    }

    private static String checkNodeNameRestrictions( final String name )
    {
        Preconditions.checkArgument( !"_".equals( name ), "name cannot be _" );

        return name;
    }

    public static NodeName from( final String name )
    {
        return new NodeName( name );
    }
}
