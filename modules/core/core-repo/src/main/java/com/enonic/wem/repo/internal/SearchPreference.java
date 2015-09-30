package com.enonic.wem.repo.internal;

public enum SearchPreference
{
    LOCAL( "_local" );

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
