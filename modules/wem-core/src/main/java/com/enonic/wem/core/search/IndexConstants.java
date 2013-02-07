package com.enonic.wem.core.search;

public enum IndexConstants
{

    WEM_INDEX( "wem" );

    private final String id;

    private IndexConstants( final String id )
    {
        this.id = id;
    }

    public String string()
    {
        return this.id;
    }

}

