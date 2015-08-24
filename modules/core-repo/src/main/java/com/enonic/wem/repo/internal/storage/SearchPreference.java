package com.enonic.wem.repo.internal.storage;

public enum SearchPreference
{
    LOCAL( "_local" );

    private String name;

    SearchPreference( final String name )
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
}
