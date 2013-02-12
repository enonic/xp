package com.enonic.wem.core.search;

public enum IndexConstants
{

    WEM_INDEX( "wem" );

    private final String value;

    private IndexConstants( final String value )
    {
        this.value = value;
    }

    public String value()
    {
        return this.value;
    }

}

