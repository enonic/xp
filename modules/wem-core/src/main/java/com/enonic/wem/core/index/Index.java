package com.enonic.wem.core.index;

public enum Index
{
    WORKSPACE( "workspace", false ),
    VERSION( "version", false ),

    // The search index is just a placeholder to indicate that the index is a search index.
    // The actual name of the index will be workspace-<workspace-name> and will be created when needed
    SEARCH( "search", true );

    private final String name;

    private final boolean dynamic;

    private Index( final String name, final boolean dynamic )
    {
        this.name = name;
        this.dynamic = dynamic;
    }

    public boolean isDynamic()
    {
        return dynamic;
    }

    public String getName()
    {
        return name;
    }
}
