package com.enonic.xp.admin.impl.json.content;

import com.enonic.xp.content.CompareContentResult;

public class CompareContentResultJson
{
    private final String compareStatus;

    private final String id;

    public CompareContentResultJson( final CompareContentResult compareContentResult )
    {
        this.compareStatus = compareContentResult.getCompareStatus().getStatus().name();
        this.id = compareContentResult.getContentId().toString();
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getCompareStatus()
    {
        return compareStatus;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getId()
    {
        return id;
    }
}
