package com.enonic.xp.node;

import com.google.common.base.Preconditions;

public class RootNodeName
    extends NodeName
{

    private RootNodeName()
    {
        super( "" );
    }

    @Override
    protected void doValidateName( final String name )
    {
        Preconditions.checkArgument( "".equals( name ) );
    }

    public static RootNodeName create()
    {
        return new RootNodeName();
    }

}
