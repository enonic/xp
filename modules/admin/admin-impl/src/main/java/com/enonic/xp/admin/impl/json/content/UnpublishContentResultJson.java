package com.enonic.xp.admin.impl.json.content;

public class UnpublishContentResultJson
{
    private final Integer successes;

    private final String contentName;

    public UnpublishContentResultJson( final Integer size, final String contentName )
    {
        this.successes = size;
        this.contentName = contentName;
    }

    public Integer getSuccesses()
    {
        return successes;
    }

    public String getContentName()
    {
        return contentName;
    }
}
