package com.enonic.wem.api.query;

public class FullTextSearch
    implements Constraint
{
    private final String text;

    public FullTextSearch( final String text )
    {
        this.text = text;
    }
}
