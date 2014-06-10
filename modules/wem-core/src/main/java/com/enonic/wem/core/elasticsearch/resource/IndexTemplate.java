package com.enonic.wem.core.elasticsearch.resource;

public class IndexTemplate
{
    private final String source;

    public IndexTemplate( final String source )
    {
        this.source = source;
    }

    public String getSource()
    {
        return source;
    }
}
