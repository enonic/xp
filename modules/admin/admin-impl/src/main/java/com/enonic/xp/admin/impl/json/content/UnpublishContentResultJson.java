package com.enonic.xp.admin.impl.json.content;

public class UnpublishContentResultJson
{
    private final Integer successes;

    private final String contentName = ""; // TODO: XP-3691

    public UnpublishContentResultJson( final Integer size )
    {
        this.successes = size;
    }

    public Integer getSuccesses()
    {
        return successes;
    }
}
