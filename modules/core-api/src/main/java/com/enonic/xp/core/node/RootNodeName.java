package com.enonic.xp.core.node;

import com.google.common.base.Preconditions;

public class RootNodeName
    extends NodeName
{

    private RootNodeName()
    {
        super( "" );
    }

    protected void doValidateName( final String name )
    {
        Preconditions.checkArgument( "".equals( name ) );
    }

    public static RootNodeName create()
    {
        return new RootNodeName();
    }

}
