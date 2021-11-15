package com.enonic.xp.node;

public enum SearchPreference
{
    LOCAL( "_local" ), PRIMARY( "_primary" );

    private final String name;

    SearchPreference( final String name )
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
}
