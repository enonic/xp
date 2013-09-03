package com.enonic.wem.query;

public class FullTextSearch
    implements Constraint
{
    private final String text;

    public FullTextSearch( final String text )
    {
        this.text = text;
    }
}
