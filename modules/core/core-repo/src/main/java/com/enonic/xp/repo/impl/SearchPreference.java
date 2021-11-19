package com.enonic.xp.repo.impl;

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
