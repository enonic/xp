package com.enonic.wem.core.index;

public enum Index
{

    WORKSPACE( "workspace" ),
    VERSION( "version" ),

    // The search index is just a placeholder to indicate that the index is a search index.
    // The actual name of the index will be workspace-<workspace-name> and will be created when needed
    SEARCH( "search" );

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
