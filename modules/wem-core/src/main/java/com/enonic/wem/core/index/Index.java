package com.enonic.wem.core.index;

public enum Index
{
    NODB( "nodb" ),
    STORE( "store" ),
    WORKSPACE( "workspace" ),
    VERSION( "version" );

    private final String name;

    private Index( final String name )
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
}
